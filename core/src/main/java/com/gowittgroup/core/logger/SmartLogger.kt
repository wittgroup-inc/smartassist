package com.gowittgroup.core.logger


import android.util.Log
import com.google.firebase.crashlytics.BuildConfig

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import javax.inject.Inject


class SmartLogger @Inject constructor() : Logger {

    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
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

    private class CrashlyticsTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.ERROR || priority == Log.WARN) {
                FirebaseCrashlytics.getInstance().log(message)
                t?.let { FirebaseCrashlytics.getInstance().recordException(it) }
            }

            if (BuildConfig.DEBUG) {
                Log.println(priority, tag, message)
            }
        }
    }
}
