package com.gowittgroup.smartassistlib.datasources.banner

import com.gowittgroup.smartassistlib.models.banner.Banner
import com.gowittgroup.smartassistlib.models.Resource
import kotlinx.coroutines.flow.Flow


interface BannerDataSource {
   suspend fun getBanner(): Resource<Flow<Banner>>
}
