package com.gowittgroup.smartassistlib.repositories

import android.util.Log
import com.gowittgroup.smartassistlib.datasources.BannerDataSource
import com.gowittgroup.smartassistlib.models.BannerContent
import com.gowittgroup.smartassistlib.models.BannerResponse
import com.gowittgroup.smartassistlib.models.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class BannerRepositoryImpl @Inject constructor(private val bannerDataSource: BannerDataSource) :
    BannerRepository {
    override suspend fun getBanner(): Resource<BannerResponse> {
      //  val flow = MutableSharedFlow<BannerResponse>()
        Log.d("pawan", "emiting-")
      //  flow.emit(
          val b=  BannerResponse(
                shouldShowBanner = true,
                BannerContent(
                    title = "New update available",
                    subTitle = "Please go and install",
                    description = "It contains new updates"
                ),

                )
      //  )
        return Resource.Success(b)
    }


//bannerDataSource.getBanner()
}
