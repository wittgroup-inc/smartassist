/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gowittgroup.smartassist

import android.content.Context
import com.gowittgroup.smartassistlib.datasources.*
import com.gowittgroup.smartassistlib.repositories.*

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val aiDataSource: AiDataSource
    val settingsDataSource: SettingsDataSource
    val conversationHistoryDataSource: ConversationHistoryDataSource
    val conversationHistoryRepository: ConversationHistoryRepository
    val answerRepository: AnswerRepository
    val settingsRepository: SettingsRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    override val aiDataSource: AiDataSource by lazy {
        ChatGpt(settingsDataSource)
    }

    override val settingsDataSource: SettingsDataSource by lazy {
        SettingsDataSourceImpl(LocalPreferenceManager.customPreference(applicationContext, "smart_assist_pref"))
    }

    override val conversationHistoryDataSource: ConversationHistoryDataSource by lazy {
        ConversationHistoryDataSourceImpl(applicationContext)
    }

    override val conversationHistoryRepository: ConversationHistoryRepository by lazy {
        ConversationHistoryRepositoryImpl(conversationHistoryDataSource)
    }

    override val answerRepository: AnswerRepository by lazy {
        AnswerRepositoryImpl(aiDataSource)
    }

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(aiDataSource, settingsDataSource)
    }

}