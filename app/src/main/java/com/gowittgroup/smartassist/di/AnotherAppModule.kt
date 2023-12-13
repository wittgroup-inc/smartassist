package com.gowittgroup.smartassist.di

import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.analytics.SmartAnalyticsImpl
import com.gowittgroup.smartassist.ui.homescreen.HomeScreenTranslations
import com.gowittgroup.smartassist.ui.homescreen.HomeScreenTranslationsImpl
import com.gowittgroup.smartassist.ui.promptscreen.PromptsScreenTranslations
import com.gowittgroup.smartassist.ui.promptscreen.PromptsScreenTranslationsImpl
import com.gowittgroup.smartassist.ui.settingsscreen.SettingScreenTranslations
import com.gowittgroup.smartassist.ui.settingsscreen.SettingScreenTranslationsImpl
import com.gowittgroup.smartassist.util.NetworkUtil
import com.gowittgroup.smartassist.util.NetworkUtilImpl
import com.gowittgroup.smartassistlib.datasources.AiDataSource
import com.gowittgroup.smartassistlib.datasources.ChatGpt
import com.gowittgroup.smartassistlib.datasources.ConversationHistoryDataSource
import com.gowittgroup.smartassistlib.datasources.ConversationHistoryDataSourceImpl
import com.gowittgroup.smartassistlib.datasources.PromptsDataSource
import com.gowittgroup.smartassistlib.datasources.PromptsDataSourceImpl
import com.gowittgroup.smartassistlib.datasources.SettingsDataSource
import com.gowittgroup.smartassistlib.datasources.SettingsDataSourceImpl
import com.gowittgroup.smartassistlib.repositories.AnswerRepository
import com.gowittgroup.smartassistlib.repositories.AnswerRepositoryImpl
import com.gowittgroup.smartassistlib.repositories.ConversationHistoryRepository
import com.gowittgroup.smartassistlib.repositories.ConversationHistoryRepositoryImpl
import com.gowittgroup.smartassistlib.repositories.PromptsRepository
import com.gowittgroup.smartassistlib.repositories.PromptsRepositoryImpl
import com.gowittgroup.smartassistlib.repositories.SettingsRepository
import com.gowittgroup.smartassistlib.repositories.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface  AnotherAppModule {

    @Binds
    @Singleton
    fun bindsNetworkUtil(networkUtilImpl: NetworkUtilImpl): NetworkUtil

    @Binds
    @Singleton
    fun bindsSmartAnalytics(smartAnalyticsImpl: SmartAnalyticsImpl): SmartAnalytics

    @Binds
    @Singleton
    fun bindsHomeScreenTranslations(homeScreenTranslationsImpl: HomeScreenTranslationsImpl): HomeScreenTranslations

    @Binds
    @Singleton
    fun bindsSettingScreenTranslations(settingsScreenTranslationsImpl: SettingScreenTranslationsImpl): SettingScreenTranslations

    @Binds
    @Singleton
    fun bindsPromptsScreenTranslations(promptsScreenTranslationsImpl: PromptsScreenTranslationsImpl): PromptsScreenTranslations

}