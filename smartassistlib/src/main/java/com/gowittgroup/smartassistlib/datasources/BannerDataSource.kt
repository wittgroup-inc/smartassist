package com.gowittgroup.smartassistlib.datasources

import com.gowittgroup.smartassistlib.models.BannerResponse
import com.gowittgroup.smartassistlib.models.Resource
import kotlinx.coroutines.flow.MutableSharedFlow


interface BannerDataSource {
   suspend fun getBanner(): Resource<MutableSharedFlow<BannerResponse>>
}
