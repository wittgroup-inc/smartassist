package com.gowittgroup.smartassistlib.repositories.banner

import com.gowittgroup.smartassistlib.models.banner.Banner
import com.gowittgroup.smartassistlib.models.Resource
import kotlinx.coroutines.flow.Flow

interface BannerRepository {
    suspend fun getBanner(): Resource<Flow<Banner>>
}
