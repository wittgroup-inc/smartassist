package com.gowittgroup.smartassistlib.data.datasources.banner

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.banner.Banner
import com.gowittgroup.smartassistlib.models.banner.BannerResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class BannerDataSourceImpl @Inject constructor(): BannerDataSource {
   override suspend fun getBanner(): Resource<Flow<Banner>> {
      val database = Firebase.database
      val myRef = database.getReference("/banner_data")
      val result = MutableSharedFlow<Banner>(1)
      val localCoroutineScope = CoroutineScope(Dispatchers.IO)
      myRef.addValueEventListener(object : ValueEventListener {
         override fun onDataChange(dataSnapshot: DataSnapshot) {


            try {
               val banner = dataSnapshot.getValue(BannerResponse::class.java)
               SmartLog.d(TAG, "Value is: $banner")
               localCoroutineScope.launch {
                  banner?.let { result.emit(it.data) } ?: result.emit(Banner.EMPTY)
               }
            } catch (e: Exception) {
               SmartLog.e(TAG, "Failed to read value.")
               localCoroutineScope.launch {
                  result.emit(Banner.EMPTY)
               }
            }
         }

         override fun onCancelled(error: DatabaseError) {

            SmartLog.e(TAG, "Failed to read value.", error.toException())
            localCoroutineScope.launch {
               result.emit(Banner.EMPTY)
            }
         }
      })
      return Resource.Success(result)
   }

   companion object {
      private val TAG = BannerDataSourceImpl::class.simpleName
   }
}
