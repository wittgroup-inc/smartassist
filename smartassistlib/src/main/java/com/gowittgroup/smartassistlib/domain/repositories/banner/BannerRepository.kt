package com.gowittgroup.smartassistlib.domain.repositories.banner

import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.banner.Banner
import kotlinx.coroutines.flow.Flow

interface BannerRepository {
    suspend fun getBanner(): Resource<Flow<Banner>>
}
