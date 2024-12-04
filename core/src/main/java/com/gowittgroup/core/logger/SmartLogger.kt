package com.gowittgroup.core.logger


import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import javax.inject.Inject


class SmartLogger @Inject constructor() : Logger {

    private var isDebugEvn = false

    override fun initLogger(isDebugEnvironment: Boolean){
        isDebugEvn = isDebugEnvironment
        if (isDebugEvn) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree(isDebugEvn))
        }
    }

    override fun v(tag: String?, message: String) {
        if (tag != null) {
            Timber.tag(tag).v(message)
        } else {
            Timber.v(message)
        }
    }

    override fun d(tag: String?, message: String) {
        if (tag != null) {
            Timber.tag(tag).d(message)
        } else {
            Timber.d(message)
        }
    }

    override fun i(tag: String?, message: String) {
        if (tag != null) {
            Timber.tag(tag).i(message)
        } else {
            Timber.i(message)
        }
    }

    override fun w(tag: String?, message: String) {
        if (tag != null) {
            Timber.tag(tag).w(message)
        } else {
            Timber.w(message)
        }
    }

    override fun e(tag: String?, message: String) {
        if (tag != null) {
            Timber.tag(tag).e(message)
        } else {
            Timber.e(message)
        }
    }

    override fun e(tag: String?, throwable: Throwable?) {
        if (tag != null) {
            Timber.tag(tag).e(throwable)
        } else {
            Timber.e(throwable)
        }
    }

    override fun e(tag: String?, message: String, throwable: Throwable?) {
        if (tag != null) {
            Timber.tag(tag).e(throwable, message)
        } else {
            Timber.e(throwable, message)
        }
    }

    private class CrashlyticsTree(private val envDebug: Boolean = false) : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.ERROR || priority == Log.WARN) {
                FirebaseCrashlytics.getInstance().log(message)
                t?.let { FirebaseCrashlytics.getInstance().recordException(it) }
            }

            if (envDebug) {
                Log.println(priority, tag, message)
            }
        }
    }
}
