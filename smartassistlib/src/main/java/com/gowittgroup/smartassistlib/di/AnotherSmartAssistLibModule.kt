package com.gowittgroup.smartassistlib.di

import com.gowittgroup.smartassistlib.datasources.AiDataSource
import com.gowittgroup.smartassistlib.datasources.ChatGpt
import com.gowittgroup.smartassistlib.datasources.ConversationHistoryDataSource
import com.gowittgroup.smartassistlib.datasources.ConversationHistoryDataSourceImpl
import com.gowittgroup.smartassistlib.datasources.Gemini
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
interface  AnotherSmartAssistLibModule {
    @Binds
    @Singleton
    fun bindsAiDataSource(gemini: Gemini): AiDataSource

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
    fun bindsAnswerRepository(answerRepositoryImpl: AnswerRepositoryImpl): AnswerRepository

    @Binds
    @Singleton
    fun bindsSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    fun bindsPromptsRepository(promptsRepositoryImpl: PromptsRepositoryImpl): PromptsRepository

    @Binds
    @Singleton
    fun bindsConversationHistoryRepository(conversationHistoryRepositoryImpl: ConversationHistoryRepositoryImpl):ConversationHistoryRepository


}