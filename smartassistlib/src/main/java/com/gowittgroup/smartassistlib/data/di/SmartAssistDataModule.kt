package com.gowittgroup.smartassistlib.data.di

import com.gowittgroup.smartassistlib.data.datasources.ai.AiDataSource
import com.gowittgroup.smartassistlib.data.datasources.ai.AiToolsDataSource
import com.gowittgroup.smartassistlib.data.datasources.ai.AiToolsDataSourceImpl
import com.gowittgroup.smartassistlib.data.datasources.ai.ChatGpt
import com.gowittgroup.smartassistlib.data.datasources.ai.Gemini
import com.gowittgroup.smartassistlib.data.datasources.authentication.AuthenticationDataSource
import com.gowittgroup.smartassistlib.data.datasources.authentication.AuthenticationDataSourceImpl
import com.gowittgroup.smartassistlib.data.datasources.banner.BannerDataSource
import com.gowittgroup.smartassistlib.data.datasources.banner.BannerDataSourceImpl
import com.gowittgroup.smartassistlib.data.datasources.conversationhistory.ConversationHistoryDataSource
import com.gowittgroup.smartassistlib.data.datasources.conversationhistory.ConversationHistoryDataSourceImpl
import com.gowittgroup.smartassistlib.data.datasources.prompts.PromptsDataSource
import com.gowittgroup.smartassistlib.data.datasources.prompts.PromptsDataSourceImpl
import com.gowittgroup.smartassistlib.data.datasources.settings.SettingsDataSource
import com.gowittgroup.smartassistlib.data.datasources.settings.SettingsDataSourceImpl
import com.gowittgroup.smartassistlib.data.datasources.subscription.SubscriptionDataSource
import com.gowittgroup.smartassistlib.data.datasources.subscription.SubscriptionDatasourceImpl
import com.gowittgroup.smartassistlib.data.repositories.ai.AnswerRepositoryImpl
import com.gowittgroup.smartassistlib.data.repositories.authentication.AuthenticationRepositoryImpl
import com.gowittgroup.smartassistlib.data.repositories.banner.BannerRepositoryImpl
import com.gowittgroup.smartassistlib.data.repositories.converstationhistory.ConversationHistoryRepositoryImpl
import com.gowittgroup.smartassistlib.data.repositories.prompts.PromptsRepositoryImpl
import com.gowittgroup.smartassistlib.data.repositories.settings.SettingsRepositoryImpl
import com.gowittgroup.smartassistlib.data.repositories.subscription.SubscriptionRepositoryImpl
import com.gowittgroup.smartassistlib.domain.repositories.ai.AnswerRepository
import com.gowittgroup.smartassistlib.domain.repositories.authentication.AuthenticationRepository
import com.gowittgroup.smartassistlib.domain.repositories.banner.BannerRepository
import com.gowittgroup.smartassistlib.domain.repositories.converstationhistory.ConversationHistoryRepository
import com.gowittgroup.smartassistlib.domain.repositories.prompts.PromptsRepository
import com.gowittgroup.smartassistlib.domain.repositories.settings.SettingsRepository
import com.gowittgroup.smartassistlib.domain.repositories.subscription.SubscriptionRepository
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
interface SmartAssistDataModule {
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
    fun bindsAuthenticationRepository(authenticationRepository: AuthenticationRepositoryImpl): AuthenticationRepository

    @Binds
    @Singleton
    fun bindsSubscriptionRepository(bannerRepository: SubscriptionRepositoryImpl): SubscriptionRepository
}