package com.gowittgroup.smartassistlib.data.datasources.ai

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassistlib.data.datasources.settings.SettingsDataSource
import com.gowittgroup.smartassistlib.db.entities.Conversation
import com.gowittgroup.smartassistlib.domain.models.ClarifyingQuestion
import com.gowittgroup.smartassistlib.domain.models.PromptAssembly
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.models.StreamResource
import com.gowittgroup.smartassistlib.domain.models.Template
import com.gowittgroup.smartassistlib.domain.models.successOr
import com.gowittgroup.smartassistlib.mappers.toMessages
import com.gowittgroup.smartassistlib.models.ai.AiTools
import com.gowittgroup.smartassistlib.models.ai.ChatCompletionRequest
import com.gowittgroup.smartassistlib.models.ai.ChatCompletionStreamResponse
import com.gowittgroup.smartassistlib.models.ai.Message
import com.gowittgroup.smartassistlib.network.ChatEventSourceListener
import com.gowittgroup.smartassistlib.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import javax.inject.Inject

private const val STREAM_COMPLETED_TOKEN = "[DONE]"

class ChatGpt @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
    private val client: OkHttpClient,
    private val moderationDataSource: ModerationDataSource,
    private val gson: Gson
) : AiDataSource {

    override suspend fun getModels(): Resource<List<String>> {
        return Resource.Success(listOf(settingsDataSource.getDefaultChatModel()))
    }

    override suspend fun getReply(conversations: List<Conversation>): Resource<Flow<StreamResource<String>>> {
        SmartLog.d(TAG, "You will get reply from : ChatGpt")
        var model = settingsDataSource.getSelectedAiModel().successOr("")
        if (model.isEmpty()) {
            model = settingsDataSource.getDefaultChatModel()
            settingsDataSource.chooseAiModel(model)
        }
        val userId = settingsDataSource.getUserId().successOr("")


        val result = MutableSharedFlow<StreamResource<String>>(1)

        val moderationResult = moderationDataSource.getModerationResult(conversations.last().data)
        when (moderationResult) {

            is Resource.Success -> if (!moderationResult.data.isSafe) {
                return Resource.Error(
                    RuntimeException(
                        "Content is flagged as ${
                            moderationResult.data.cause.joinToString(
                                ","
                            )
                        }"
                    )
                )
            }

            is Resource.Error -> {
                SmartLog.e(TAG,
                    moderationResult.exception.message
                        ?: moderationResult.exception.stackTraceToString()
                )
            }
        }
        return try {
            loadReply(
                message = conversations.toMessages(),
                model = model,
                userId = userId,
                listener = createChatEventSourceListener(result)
            )
            Resource.Success(result)
        } catch (e: Exception) {
            SmartLog.e(TAG, e.stackTraceToString())
            Resource.Error(e)
        }

    }

    override suspend fun fetchClarifyingQuestions(
        idea: String
    ): Resource<Flow<List<ClarifyingQuestion>>> {
        return try {
            val system = """
            You are a prompt assistant. 
            Produce up to 6 clarifying questions as JSON array like:
            [
              {"id":"audience","key":"audience","text":"Who is the audience?"}
            ]
        """.trimIndent()
            val user = "Rough idea: $idea"

            val raw = runCompletion(system, user) // helper to call LLM once
            val parsed: List<ClarifyingQuestion> = parseQuestions(raw)

            // Wrap parsed list into a cold Flow
            Resource.Success(flowOf(parsed))
        } catch (e: Exception) {
            SmartLog.e(TAG, e.stackTraceToString())
            Resource.Error(e)
        }
    }

    override suspend fun fetchAssembledPrompt(
        templateId: String,
        details: Map<String, String>
    ): Resource<Flow<PromptAssembly>> {
        return try {
            val system =
                "You are a prompt engineer. Given idea and answers, return JSON { \"system\":..., \"userPrompt\":... }"

            val user = buildString {
                appendLine("Rough idea: ${details["idea"] ?: ""}")
                appendLine("Answers:")
                details.forEach { (k, v) -> appendLine("- $k: $v") }
            }

            val raw = runCompletion(system, user)
            val parsed: PromptAssembly = parseAssembly(raw)

            // Wrap into cold Flow for consistency with signature
            Resource.Success(flowOf(parsed))
        } catch (e: Exception) {
            SmartLog.e(TAG, e.stackTraceToString())
            Resource.Error(e)
        }
    }


    override suspend fun fetchTemplates(): Resource<Flow<List<Template>>> {
        return try {
            val templates = listOf(
                Template(
                    id = "email_marketing",
                    title = "Email (Marketing)",
                    description = "Short marketing email",
                    placeholders = listOf("product", "audience", "tone", "length")
                ),
                Template(
                    id = "blog_outline",
                    title = "Blog Outline",
                    description = "Detailed outline",
                    placeholders = listOf("topic", "audience", "sections")
                )
            )

            Resource.Success(flowOf(templates))
        } catch (e: Exception) {
            SmartLog.e(TAG, e.stackTraceToString())
            Resource.Error(e)
        }
    }

    private fun parseQuestions(raw: String): List<ClarifyingQuestion> = runCatching {
        val trimmed = raw.trim()
        val start = trimmed.indexOf('[')
        val end = trimmed.lastIndexOf(']')
        if (start >= 0 && end > start) {
            val json = trimmed.substring(start, end + 1)
            val arr: JsonArray = JsonParser.parseString(json).asJsonArray
            arr.map { el ->
                val o: JsonObject = el.asJsonObject
                ClarifyingQuestion(
                    id = o.get("id")?.asString ?: java.util.UUID.randomUUID().toString(),
                    question = o.get("text")?.asString
                        ?: o.get("key")?.asString
                        ?: o.entrySet().firstOrNull()?.value?.asString
                        ?: "Please clarify"
                )
            }
        } else emptyList()
    }.getOrElse {
        // --- fallback defaults ---
        listOf(
            ClarifyingQuestion("audience", "Who is the audience?"),
            ClarifyingQuestion("tone", "Preferred tone (formal, casual)?"),
            ClarifyingQuestion("length", "Length / word limit?")
        )
    }


    private fun parseAssembly(raw: String): PromptAssembly = runCatching {
        val start = raw.indexOf('{')
        val end = raw.lastIndexOf('}')
        if (start >= 0 && end > start) {
            val json = raw.substring(start, end + 1)
            val el: JsonObject = JsonParser.parseString(json).asJsonObject

            val system =
                el.get("system")?.asString
                    ?: el.get("systemPreamble")?.asString
                    ?: "You are helpful."

            val userPrompt =
                el.get("userPrompt")?.asString
                    ?: el.get("prompt")?.asString
                    ?: raw

            PromptAssembly("$system\n\n$userPrompt")
        } else {
            PromptAssembly("You are helpful.\n\n$raw")
        }
    }.getOrElse { PromptAssembly("You are helpful.\n\n$raw") }

    private suspend fun runCompletion(system: String, user: String): String {
        val messages = listOf(
            Message(role = "system", content = system),
            Message(role = "user", content = user)
        )
        val request = ChatCompletionRequest(
            model = settingsDataSource.getSelectedAiModel()
                .successOr(settingsDataSource.getDefaultChatModel()),
            messages = messages,
            user = settingsDataSource.getUserId().successOr("")
        )

        val body =
            gson.toJson(request).toRequestBody("application/json; charset=utf-8".toMediaType())
        val call = client.newCall(
            Request.Builder()
                .url("${Constants.CHAT_GPT_BASE_URL}${Constants.CHAT_GPT_API_VERSION}/chat/completions")
                .post(body)
                .build()
        )
        return withContext(Dispatchers.IO) {
            call.execute().use { response ->
                if (!response.isSuccessful) throw RuntimeException("HTTP ${response.code}")
                response.body?.string() ?: throw RuntimeException("Empty response")
            }
        }
    }


    private fun createChatEventSourceListener(result: MutableSharedFlow<StreamResource<String>>) =
        object : ChatEventSourceListener() {
            var completeRes = ""
            var started = false
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                super.onEvent(eventSource, id, type, data)
                SmartLog.d(TAG, "Received Data: $data")
                runBlocking {
                    if (data != STREAM_COMPLETED_TOKEN) {
                        val response = gson.fromJson(data, ChatCompletionStreamResponse::class.java)
                        sendData(response)
                    } else {
                        SmartLog.d(TAG, "Complete Response: $completeRes")
                        result.emit(StreamResource.StreamCompleted(true))
                    }
                }

            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                super.onFailure(eventSource, t, response)
                SmartLog.e(TAG, t?.stackTraceToString() ?: "")
                coroutineScope.launch {
                    result.emit(StreamResource.Error(RuntimeException(response?.message)))
                }
            }

            private suspend fun sendData(response: ChatCompletionStreamResponse) {
                response.choices[0].delta.content?.let {
                    SmartLog.d(TAG, "Parsed fine")
                    if (!started) {
                        result.emit(StreamResource.Initiated(AiTools.CHAT_GPT))
                        result.emit(StreamResource.StreamStarted(it.trimStart()))
                        SmartLog.d(TAG, "OnStart: Sending to UI: ${it.trimStart()}")
                        started = true
                    } else {
                        completeRes += it
                        result.emit(StreamResource.StreamInProgress(it))
                    }
                }
            }
        }

    private suspend fun loadReply(
        message: List<Message>,
        model: String,
        userId: String,
        listener: EventSourceListener
    ) {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestStr =
            gson.toJson(ChatCompletionRequest(model = model, messages = message, user = userId))
        SmartLog.d(TAG, "requestBody: $requestStr")
        val body = requestStr.toRequestBody(mediaType)
        makeRequest(body, listener)
    }

    private suspend fun makeRequest(body: RequestBody, listener: EventSourceListener) {
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("${Constants.CHAT_GPT_BASE_URL}${Constants.CHAT_GPT_API_VERSION}/chat/completions")
                .addHeader("Accept", "text/event-stream")
                .post(body)
                .build()

            EventSources.createFactory(client)
                .newEventSource(request = request, listener = listener)
        }
    }

    companion object {
        private val TAG = ChatGpt::class.simpleName
    }
}