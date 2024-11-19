package com.gowittgroup.smartassistlib.util

object Constants {
    const val CHAT_GPT_API_VERSION = "v1"
    const val CHAT_GPT_BASE_URL = "https://api.openai.com/"
    const val CHAT_GPT_DEFAULT_CHAT_AI_MODEL = "gpt-4o-mini"
    const val GEMINI_DEFAULT_CHAT_AI_MODEL = "gemini-1.5-pro"
    const val USER_COLLECTION_PATH = "users"
    object UserDataKey{
        const val FIRST_NAME = "firstName"
        const val LAST_NAME = "lastName"
        const val DATE_OF_BIRTH = "dateOfBirth"
        const val GENDER = "gender"
        const val EMAIL = "email"
    }

    const val SUBSCRIPTION_COLLECTION_PATH = "subscriptions"
    object SubscriptionDataKey{
        const val SUBSCRIPTION_ID = "subscriptionId"
        const val STATUS = "status"
        const val EXPIRY_DATE = "expiryDate"
    }

    object SubscriptionStatusResultKey{
        const val SUBSCRIPTION = "subscriptions"
        const val STATUS = "status"
    }

    object SubscriptionStatusValue{
        const val ACTIVE = "active"
        const val INACTIVE = "inactive"
    }
}
