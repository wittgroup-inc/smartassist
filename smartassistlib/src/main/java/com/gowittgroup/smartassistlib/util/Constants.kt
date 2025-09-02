package com.gowittgroup.smartassistlib.util

import com.gowittgroup.smartassistlib.models.ai.AiTools
import com.gowittgroup.smartassistlib.util.Constants.ChatGptModels.GPT_4O_MINI
import com.gowittgroup.smartassistlib.util.Constants.GeminiModels.GEMINI_2_DOT_5_FLASH_LITE

object Constants {
    const val CHAT_GPT_API_VERSION = "v1"
    const val CHAT_GPT_BASE_URL = "https://api.openai.com/"
    const val CHAT_GPT_DEFAULT_CHAT_AI_MODEL = GPT_4O_MINI
    const val GEMINI_DEFAULT_CHAT_AI_MODEL = GEMINI_2_DOT_5_FLASH_LITE
    val DEFAULT_AI_TOOL = AiTools.GEMINI
    const val USER_COLLECTION_PATH = "users"
    object UserDataKey{
        const val FIRST_NAME = "firstName"
        const val LAST_NAME = "lastName"
        const val DATE_OF_BIRTH = "dateOfBirth"
        const val GENDER = "gender"
        const val EMAIL = "email"
    }

    const val SUBSCRIPTION_COLLECTION_PATH = "subscriptions"
    const val SUBSCRIPTION_SUB_COLLECTION_PATH = "subscriptions"
    object SubscriptionDataKey{
        const val PRODUCT_ID = "productId"
        const val SUBSCRIPTION_ID = "subscriptionId"
        const val STATUS = "status"
        const val PURCHASE_DATE = "purchaseDate"
        const val EXPIRY_DATE = "expiryDate"
    }

    object SubscriptionStatusValue{
        const val ACTIVE = "active"
        const val INACTIVE = "inactive"
    }

    object SubscriptionSKUs {
        const val SMART_PREMIUM = "smart_premium"
        const val BASIC_SUBSCRIPTION = "basic_subscription"
    }


    object SmartPremiumPlans {
        const val SMART_DAILY = "smart-daily"
        const val SMART_MONTHLY = "smart-monthly"
        const val SMART_YEARLY = "smart-yearly"
    }

    object SubscriptionDurationCode {
        const val ONE_DAY = "P1D"
        const val ONE_MONTH = "P1M"
        const val ONE_YEAR = "P1Y"
    }

    object BasicSubscriptionPlans {
        const val basic_free = "basic_free"
    }

    object ChatGptModels{
        const val GPT_4O_MINI = "gpt-4o-mini"
        const val GPT_4O = "gpt-4o"
        const val GPT_4 = "gpt-4"
    }

    object GeminiModels{
        const val GEMINI_2_DOT_5_FLASH_LITE = "gemini-2.5-flash-lite"
        const val GEMINI_2_DOT_5_FLASH = "gemini-2.5-flash"
        const val GEMINI_2_DOT_5_PRO = "gemini-2.5-pro"
        const val GEMINI_2_DOT_0_FLASH = "gemini-2.0-flash"
    }
}
