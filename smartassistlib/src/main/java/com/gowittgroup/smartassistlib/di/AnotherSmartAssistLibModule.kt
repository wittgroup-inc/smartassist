package com.gowittgroup.smartassistlib.di

import com.gowittgroup.smartassistlib.datasources.AiDataSource
import com.gowittgroup.smartassistlib.datasources.AiToolsDataSource
import com.gowittgroup.smartassistlib.datasources.AiToolsDataSourceImpl
import com.gowittgroup.smartassistlib.datasources.ChatGpt
import com.gowittgroup.smartassistlib.datasources.ConversationHistoryDataSource
import com.gowittgroup.smartassistlib.datasources.ConversationHistoryDataSourceImpl
import com.gowittgroup.smartassistlib.datasources.Gemini
import com.gowittgroup.smartassistlib.datasources.PromptsDataSource
import com.gowittgroup.smartassistlib.datasources.PromptsDataSourceImpl
import com.gowittgroup.smartassistlib.datasources.SettingsDataSource
import com.gowittgroup.smartassistlib.datasources.SettingsDataSourceImpl
import com.gowittgroup.smartassistlib.datasources.authentication.AuthenticationDataSource
import com.gowittgroup.smartassistlib.datasources.authentication.AuthenticationDataSourceImpl
import com.gowittgroup.smartassistlib.datasources.banner.BannerDataSource
import com.gowittgroup.smartassistlib.datasources.banner.BannerDataSourceImpl
import com.gowittgroup.smartassistlib.datasources.subscription.SubscriptionDataSource
import com.gowittgroup.smartassistlib.datasources.subscription.SubscriptionDatasourceImpl
import com.gowittgroup.smartassistlib.repositories.AnswerRepository
import com.gowittgroup.smartassistlib.repositories.AnswerRepositoryImpl
import com.gowittgroup.smartassistlib.repositories.ConversationHistoryRepository
import com.gowittgroup.smartassistlib.repositories.ConversationHistoryRepositoryImpl
import com.gowittgroup.smartassistlib.repositories.PromptsRepository
import com.gowittgroup.smartassistlib.repositories.PromptsRepositoryImpl
import com.gowittgroup.smartassistlib.repositories.SettingsRepository
import com.gowittgroup.smartassistlib.repositories.SettingsRepositoryImpl
import com.gowittgroup.smartassistlib.repositories.authentication.AuthenticationRepository
import com.gowittgroup.smartassistlib.repositories.authentication.AuthenticationRepositoryImpl
import com.gowittgroup.smartassistlib.repositories.banner.BannerRepository
import com.gowittgroup.smartassistlib.repositories.banner.BannerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

const val CHAT_GPT = "CHAT_GPT"
const val GEMINI = "GEMINI"

@Module
@InstallIn(SingletonComponent::class)
interface AnotherSmartAssistLibModule {
    @Binds
    @Singleton
    @Named(GEMINI)
    fun bindsGeminiDataSource(gemini: Gemini): AiDataSource

    @Binds
    @Singleton
    @Named(CHAT_GPT)
    fun bindsChatGptDataSource(chatGpt: ChatGpt): AiDataSource

    @Binds
    @Singleton
    fun bindsSettingsDataSource(settingsDataSourceImpl: SettingsDataSourceImpl): SettingsDataSource

    @Binds
    @Singleton
    fun bindsPromptsDataSource(promptsDataSourceImpl: PromptsDataSourceImpl): PromptsDataSource

    @Binds
    @Singleton
    fun bindsConversationHistoryDataSource(conversationHistoryDataSourceImpl: ConversationHistoryDataSourceImpl): ConversationHistoryDataSource

    @Binds
    @Singleton
    fun bindsAiToolsDataSource(aiToolsDataSource: AiToolsDataSourceImpl): AiToolsDataSource


    @Binds
    @Singleton
    fun bindsBannerDataSource(bannerDataSource: BannerDataSourceImpl): BannerDataSource

    @Binds
    @Singleton
    fun bindsAuthenticationDataSource(bannerRepository: AuthenticationDataSourceImpl): AuthenticationDataSource

    @Binds
    @Singleton
    fun bindsSubscriptionDataSource(bannerRepository: SubscriptionDatasourceImpl): SubscriptionDataSource

    @Binds
    @Singleton
    fun bindsAnswerRepository(answerRepositoryImpl: AnswerRepositoryImpl): AnswerRepository

    @Binds
    @Singleton
    fun bindsSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    fun bindsPromptsRepository(promptsRepositoryImpl: PromptsRepositoryImpl): PromptsRepository

    @Binds
    @Singleton
    fun bindsConversationHistoryRepository(conversationHistoryRepositoryImpl: ConversationHistoryRepositoryImpl): ConversationHistoryRepository

    @Binds
    @Singleton
    fun bindsBannerRepository(bannerRepository: BannerRepositoryImpl): BannerRepository

    @Binds
    @Singleton
    fun bindsAuthenticationRepository(bannerRepository: AuthenticationRepositoryImpl): AuthenticationRepository

}