package com.gowittgroup.smartassistlib.datasources

import android.content.Context
import android.content.SharedPreferences

object LocalPreferenceManager {


    private const val READ_ALOUD = "READ_ALOUD"
    private const val AI_MODEL = "AI_MODEL"
    private const val AI_TOOL = "AI_TOOL"
    private const val USER_ID = "USER_ID"
    private const val HANDS_FREE_MODE = "HANDS_FREE_MODE"
    private const val USER_SUBSCRIPTION_STATUS = "USER_SUBSCRIPTION_STATUS"

    fun customPreference(context: Context, name: String): SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    private inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    var SharedPreferences.readAloud
        get() = getBoolean(READ_ALOUD, false)
        set(value) {
            editMe {
                it.putBoolean(READ_ALOUD, value)
            }
        }

    var SharedPreferences.aiModel
        get() = getString(AI_MODEL, "")
        set(value) {
            editMe {
                it.putString(AI_MODEL, value)
            }
        }

    var SharedPreferences.aiTool
        get() = getInt(AI_TOOL, 0)
        set(value) {
            editMe {
                it.putInt(AI_TOOL, value)
            }
        }

    var SharedPreferences.userId
        get() = getString(USER_ID, "")
        set(value) {
            editMe {
                it.putString(USER_ID, value)
            }
        }

    var SharedPreferences.clearValues
        get() = { }
        set(value) {
            editMe {
                it.clear()
            }
        }

    var SharedPreferences.handsFreeMode
        get() = getBoolean(HANDS_FREE_MODE, false)
        set(value) {
            editMe {
                it.putBoolean(HANDS_FREE_MODE, value)
            }
        }

    var SharedPreferences.userSubscriptionStatus
        get() = getBoolean(USER_SUBSCRIPTION_STATUS, false)
        set(value) {
            editMe {
                it.putBoolean(USER_SUBSCRIPTION_STATUS, value)
            }
        }
}
