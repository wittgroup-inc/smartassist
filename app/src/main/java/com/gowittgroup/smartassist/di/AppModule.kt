package com.gowittgroup.smartassist.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.homescreen.HomeScreenTranslations
import com.gowittgroup.smartassist.ui.promptscreen.PromptsScreenTranslations
import com.gowittgroup.smartassist.ui.settingsscreen.SettingScreenTranslations
import com.gowittgroup.smartassist.util.NetworkUtil
import com.gowittgroup.smartassistlib.datasources.AiDataSource
import com.gowittgroup.smartassistlib.datasources.ConversationHistoryDataSource
import com.gowittgroup.smartassistlib.datasources.PromptsDataSource
import com.gowittgroup.smartassistlib.datasources.SettingsDataSource
import com.gowittgroup.smartassistlib.repositories.AnswerRepository
import com.gowittgroup.smartassistlib.repositories.ConversationHistoryRepository
import com.gowittgroup.smartassistlib.repositories.PromptsRepository
import com.gowittgroup.smartassistlib.repositories.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics = FirebaseAnalytics.getInstance(context)


}