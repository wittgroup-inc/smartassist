package com.gowittgroup.smartassistlib.data.datasources.banner

import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.banner.Banner
import kotlinx.coroutines.flow.Flow


interface BannerDataSource {
   suspend fun getBanner(): Resource<Flow<Banner>>
}
