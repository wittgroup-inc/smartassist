package com.gowittgroup.smartassistlib.repositories

import com.gowittgroup.smartassistlib.models.BannerResponse
import com.gowittgroup.smartassistlib.models.Prompts
import com.gowittgroup.smartassistlib.models.PromptsCategory
import com.gowittgroup.smartassistlib.models.Resource
import kotlinx.coroutines.flow.Flow

interface BannerRepository {
    suspend fun getBanner(): Resource<BannerResponse>
}
