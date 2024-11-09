package com.gowittgroup.smartassistlib.datasources

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gowittgroup.smartassistlib.datasources.PromptsDataSourceImpl.Companion.TAG
import com.gowittgroup.smartassistlib.models.BannerResponse
import com.gowittgroup.smartassistlib.models.Prompts
import com.gowittgroup.smartassistlib.models.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class BannerDataSourceImpl @Inject constructor(): BannerDataSource {
   override suspend fun getBanner(): Resource<MutableSharedFlow<BannerResponse>> {
      val database = Firebase.database
      val myRef = database.getReference("/prompt_data")
      val result = MutableSharedFlow<BannerResponse>(1)
      val localCoroutineScope = CoroutineScope(Dispatchers.IO)
      myRef.addValueEventListener(object : ValueEventListener {
         override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            try {
               val bannerResponse = dataSnapshot.getValue(BannerResponse::class.java)
               Log.d(TAG, "Value is: $bannerResponse")
               localCoroutineScope.launch {
                  bannerResponse?.let { result.emit(it) } ?: result.emit(BannerResponse.EMPTY)
               }
            } catch (e: Exception) {
               Log.e(TAG, "Failed to read value.")
               localCoroutineScope.launch {
                  result.emit(BannerResponse.EMPTY)
               }
            }
         }

         override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.e(TAG, "Failed to read value.", error.toException())
            localCoroutineScope.launch {
               result.emit(BannerResponse.EMPTY)
            }
         }
      })
      return Resource.Success(result)
   }
}
