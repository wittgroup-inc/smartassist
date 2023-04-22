package com.gowittgroup.smartassistlib.datasources

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gowittgroup.smartassistlib.models.PromptResponse
import com.gowittgroup.smartassistlib.models.Prompts
import com.gowittgroup.smartassistlib.models.PromptsCategory
import com.gowittgroup.smartassistlib.models.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class PromptsDataSourceImpl : PromptsDataSource {
    override suspend fun getPromptsCategories(): Resource<Flow<List<PromptsCategory>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPromptsForCategory(categoryId: Long): Resource<Flow<Prompts>> {
        TODO("Not yet implemented")
    }


    override suspend fun getAllPrompts(): Resource<Flow<List<Prompts>>> {
        val database = Firebase.database
        val myRef = database.getReference("")
        val result = MutableSharedFlow<List<Prompts>>(1)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val promptResponse = dataSnapshot.getValue(PromptResponse::class.java)
                Log.d(TAG, "Value is: $promptResponse")
                GlobalScope.launch {
                    promptResponse?.let { result.emit(it.data) } ?: result.emit(emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", error.toException())
                Resource.Error(error.toException())
            }
        })
        return Resource.Success(result)
    }


    companion object {
        val TAG = PromptsDataSourceImpl::class.simpleName
    }
}
