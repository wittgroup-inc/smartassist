package com.gowittgroup.smartassistlib.data.repositories.banner

import com.gowittgroup.smartassistlib.data.datasources.banner.BannerDataSource
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.repositories.banner.BannerRepository
import com.gowittgroup.smartassistlib.models.banner.Banner
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BannerRepositoryImpl @Inject constructor(private val bannerDataSource: BannerDataSource) :
    BannerRepository {
    override suspend fun getBanner(): Resource<Flow<Banner>> = bannerDataSource.getBanner()
}
