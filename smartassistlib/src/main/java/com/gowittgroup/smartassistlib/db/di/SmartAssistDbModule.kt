package com.gowittgroup.smartassistlib.db.di

import android.content.Context
import androidx.room.Room
import com.gowittgroup.smartassistlib.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SmartAssistDbModule {

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java, "smart_assist"
        ).build()

    @Provides
    @Singleton
    fun providesConversationDao(appDatabase: AppDatabase) = appDatabase.conversationHistoryDao()
}