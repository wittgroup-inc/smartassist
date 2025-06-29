# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.gemalto.jp2.** { *; }
-dontwarn com.gemalto.jp2.**


-keep class java.lang.invoke.** { *; }
-dontwarn java.lang.invoke.**
-keep class j$.** { *; }
-dontwarn j$.**

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn com.gowittgroup.core.logger.SmartLog
-dontwarn com.gowittgroup.smartassistlib.data.datasources.ai.AiDataSource
-dontwarn com.gowittgroup.smartassistlib.data.datasources.ai.AiToolsDataSource
-dontwarn com.gowittgroup.smartassistlib.data.datasources.ai.AiToolsDataSourceImpl
-dontwarn com.gowittgroup.smartassistlib.data.datasources.ai.ChatGpt
-dontwarn com.gowittgroup.smartassistlib.data.datasources.ai.Gemini
-dontwarn com.gowittgroup.smartassistlib.data.datasources.ai.ModerationDataSource
-dontwarn com.gowittgroup.smartassistlib.data.datasources.ai.ModerationDataSourceImpl
-dontwarn com.gowittgroup.smartassistlib.data.datasources.authentication.AuthenticationDataSource
-dontwarn com.gowittgroup.smartassistlib.data.datasources.authentication.AuthenticationDataSourceImpl
-dontwarn com.gowittgroup.smartassistlib.data.datasources.banner.BannerDataSource
-dontwarn com.gowittgroup.smartassistlib.data.datasources.banner.BannerDataSourceImpl
-dontwarn com.gowittgroup.smartassistlib.data.datasources.conversationhistory.ConversationHistoryDataSource
-dontwarn com.gowittgroup.smartassistlib.data.datasources.conversationhistory.ConversationHistoryDataSourceImpl
-dontwarn com.gowittgroup.smartassistlib.data.datasources.prompts.PromptsDataSource
-dontwarn com.gowittgroup.smartassistlib.data.datasources.prompts.PromptsDataSourceImpl
-dontwarn com.gowittgroup.smartassistlib.data.datasources.settings.SettingsDataSource
-dontwarn com.gowittgroup.smartassistlib.data.datasources.settings.SettingsDataSourceImpl
-dontwarn com.gowittgroup.smartassistlib.data.datasources.subscription.Event$PurchaseStatus$Error
-dontwarn com.gowittgroup.smartassistlib.data.datasources.subscription.Event$PurchaseStatus$Success
-dontwarn com.gowittgroup.smartassistlib.data.datasources.subscription.Event
-dontwarn com.gowittgroup.smartassistlib.data.datasources.subscription.SubscriptionDataSource
-dontwarn com.gowittgroup.smartassistlib.data.datasources.subscription.SubscriptionDatasourceImpl
-dontwarn com.gowittgroup.smartassistlib.data.di.SmartAssistDataModule
-dontwarn com.gowittgroup.smartassistlib.data.di.SmartAssistDataModule_ProvidesFirebaseAuthFactory
-dontwarn com.gowittgroup.smartassistlib.data.di.SmartAssistDataModule_ProvidesFirebaseDatabaseFactory
-dontwarn com.gowittgroup.smartassistlib.data.di.SmartAssistDataModule_ProvidesFirestoreFactory
-dontwarn com.gowittgroup.smartassistlib.data.di.SmartAssistDataModule_ProvidesGsonFactory
-dontwarn com.gowittgroup.smartassistlib.data.repositories.ai.AnswerRepositoryImpl
-dontwarn com.gowittgroup.smartassistlib.data.repositories.authentication.AuthenticationRepositoryImpl
-dontwarn com.gowittgroup.smartassistlib.data.repositories.banner.BannerRepositoryImpl
-dontwarn com.gowittgroup.smartassistlib.data.repositories.converstationhistory.ConversationHistoryRepositoryImpl
-dontwarn com.gowittgroup.smartassistlib.data.repositories.prompts.PromptsRepositoryImpl
-dontwarn com.gowittgroup.smartassistlib.data.repositories.settings.SettingsRepositoryImpl
-dontwarn com.gowittgroup.smartassistlib.data.repositories.subscription.SubscriptionRepositoryImpl
-dontwarn com.gowittgroup.smartassistlib.db.dao.ConversationHistoryDao
-dontwarn com.gowittgroup.smartassistlib.db.di.SmartAssistDbModule_ProvidesAppDatabaseFactory
-dontwarn com.gowittgroup.smartassistlib.db.di.SmartAssistDbModule_ProvidesConversationDaoFactory
-dontwarn com.gowittgroup.smartassistlib.db.entities.Conversation
-dontwarn com.gowittgroup.smartassistlib.db.entities.ConversationHistory
-dontwarn com.gowittgroup.smartassistlib.domain.models.Resource$Error
-dontwarn com.gowittgroup.smartassistlib.domain.models.Resource$Success
-dontwarn com.gowittgroup.smartassistlib.domain.models.Resource
-dontwarn com.gowittgroup.smartassistlib.domain.models.ResourceKt
-dontwarn com.gowittgroup.smartassistlib.domain.models.StreamResource$Error
-dontwarn com.gowittgroup.smartassistlib.domain.models.StreamResource$Initiated
-dontwarn com.gowittgroup.smartassistlib.domain.models.StreamResource$StreamCompleted
-dontwarn com.gowittgroup.smartassistlib.domain.models.StreamResource$StreamInProgress
-dontwarn com.gowittgroup.smartassistlib.domain.models.StreamResource$StreamStarted
-dontwarn com.gowittgroup.smartassistlib.domain.models.StreamResource
-dontwarn com.gowittgroup.smartassistlib.domain.repositories.ai.AnswerRepository
-dontwarn com.gowittgroup.smartassistlib.domain.repositories.authentication.AuthenticationRepository
-dontwarn com.gowittgroup.smartassistlib.domain.repositories.banner.BannerRepository
-dontwarn com.gowittgroup.smartassistlib.domain.repositories.converstationhistory.ConversationHistoryRepository
-dontwarn com.gowittgroup.smartassistlib.domain.repositories.prompts.PromptsRepository
-dontwarn com.gowittgroup.smartassistlib.domain.repositories.settings.SettingsRepository
-dontwarn com.gowittgroup.smartassistlib.domain.repositories.subscription.SubscriptionRepository
-dontwarn com.gowittgroup.smartassistlib.models.ai.AiTools
-dontwarn com.gowittgroup.smartassistlib.models.authentication.AuthProvider
-dontwarn com.gowittgroup.smartassistlib.models.authentication.SignUpModel
-dontwarn com.gowittgroup.smartassistlib.models.authentication.User
-dontwarn com.gowittgroup.smartassistlib.models.banner.Banner$Companion
-dontwarn com.gowittgroup.smartassistlib.models.banner.Banner
-dontwarn com.gowittgroup.smartassistlib.models.banner.BannerContent
-dontwarn com.gowittgroup.smartassistlib.models.prompts.Prompts$Companion
-dontwarn com.gowittgroup.smartassistlib.models.prompts.Prompts
-dontwarn com.gowittgroup.smartassistlib.models.prompts.PromptsCategory
-dontwarn com.gowittgroup.smartassistlib.models.subscriptions.Offer
-dontwarn com.gowittgroup.smartassistlib.models.subscriptions.Pricing
-dontwarn com.gowittgroup.smartassistlib.models.subscriptions.Product
-dontwarn com.gowittgroup.smartassistlib.models.subscriptions.Subscription
-dontwarn com.gowittgroup.smartassistlib.models.subscriptions.SubscriptionKt
-dontwarn com.gowittgroup.smartassistlib.network.ChatGptHeaderInterceptor
-dontwarn com.gowittgroup.smartassistlib.network.ChatGptService
-dontwarn com.gowittgroup.smartassistlib.network.di.SmartAssistNetworkModule_ProvidesChatGptServiceFactory
-dontwarn com.gowittgroup.smartassistlib.network.di.SmartAssistNetworkModule_ProvidesHeaderInterceptorFactory
-dontwarn com.gowittgroup.smartassistlib.network.di.SmartAssistNetworkModule_ProvidesLoggingInterceptorFactory
-dontwarn com.gowittgroup.smartassistlib.network.di.SmartAssistNetworkModule_ProvidesOkHttClientFactory
-dontwarn com.gowittgroup.smartassistlib.network.di.SmartAssistNetworkModule_ProvidesRetrofitFactory
-dontwarn com.gowittgroup.smartassistlib.sharedpref.di.SmartAssistSharedPrefModule_ProvideSharedPreferenceFactory
-dontwarn com.gowittgroup.smartassistlib.util.AiDataSourceProvider
-dontwarn com.gowittgroup.smartassistlib.util.GenerativeModelFactory
-dontwarn com.gowittgroup.smartassistlib.util.KeyManager