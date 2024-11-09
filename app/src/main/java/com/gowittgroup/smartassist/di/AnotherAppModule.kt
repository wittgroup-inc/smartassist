package com.gowittgroup.smartassist.di

import com.gowittgroup.smartassist.ui.aboutscreen.AboutScreenTranslations
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.analytics.SmartAnalyticsImpl
import com.gowittgroup.smartassist.ui.faqscreen.FaqScreenTranslations
import com.gowittgroup.smartassist.ui.faqscreen.FaqScreenTranslationsImpl
import com.gowittgroup.smartassist.ui.homescreen.HomeScreenTranslations
import com.gowittgroup.smartassist.ui.homescreen.HomeScreenTranslationsImpl
import com.gowittgroup.smartassist.ui.promptscreen.PromptsScreenTranslations
import com.gowittgroup.smartassist.ui.promptscreen.PromptsScreenTranslationsImpl
import com.gowittgroup.smartassist.ui.settingsscreen.AboutScreenTranslationsImpl
import com.gowittgroup.smartassist.ui.settingsscreen.SettingScreenTranslations
import com.gowittgroup.smartassist.ui.settingsscreen.SettingScreenTranslationsImpl
import com.gowittgroup.smartassist.util.NetworkUtil
import com.gowittgroup.smartassist.util.NetworkUtilImpl
import com.gowittgroup.smartassistlib.datasources.AuthenticationService
import com.gowittgroup.smartassistlib.datasources.AuthenticationServiceImpl
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

    @Binds
    @Singleton
    fun bindsAboutScreenTranslations(aboutScreenTranslationsImpl: AboutScreenTranslationsImpl): AboutScreenTranslations

    @Binds
    @Singleton
    fun bindsFaqScreenTranslations(faqScreenTranslationsImpl: FaqScreenTranslationsImpl): FaqScreenTranslations

    @Binds
    @Singleton
    fun bindsAuthenticationService(authService: AuthenticationServiceImpl): AuthenticationService

}