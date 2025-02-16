package com.gowittgroup.smartassistlib.data.datasources.prompts

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.prompts.PromptResponse
import com.gowittgroup.smartassistlib.models.prompts.Prompts
import com.gowittgroup.smartassistlib.models.prompts.PromptsCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PromptsDataSourceImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : PromptsDataSource {

    override suspend fun getPromptsCategories(): Resource<Flow<List<PromptsCategory>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPromptsForCategory(categoryId: Long): Resource<Flow<Prompts>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllPrompts(): Resource<Flow<List<Prompts>>> {
        val myRef = firebaseDatabase.getReference("/prompt_data")
        val result = MutableSharedFlow<List<Prompts>>(1)
        val localCoroutineScope = CoroutineScope(Dispatchers.IO)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                try {
                    val promptResponse = dataSnapshot.getValue(PromptResponse::class.java)
                    SmartLog.d(TAG, "Value is: $promptResponse")
                    localCoroutineScope.launch {
                        promptResponse?.let { result.emit(it.data) } ?: result.emit(emptyList())
                    }
                } catch (e: Exception) {
                    SmartLog.e(TAG, "Failed to read value.")
                    localCoroutineScope.launch {
                        result.emit(emptyList())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

                SmartLog.e(TAG, "Failed to read value.", error.toException())
                localCoroutineScope.launch {
                    result.emit(emptyList())
                }
            }
        })
        return Resource.Success(result)
    }


    companion object {
        private val TAG = PromptsDataSourceImpl::class.simpleName
    }
}
