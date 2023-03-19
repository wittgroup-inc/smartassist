package com.gowittgroup.smartassistlib.datasources

import android.content.Context
import android.content.SharedPreferences

object LocalPreferenceManager {

    private const val READ_ALOUD = "READ_ALOUD"
    private const val AI_MODEL = "AI_MODEL"

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

    var SharedPreferences.clearValues
        get() = { }
        set(value) {
            editMe {
                it.clear()
            }
        }
}
