package com.gowittgroup.core.logger

object SmartLog {
    private val logger: Logger = SmartLogger()

    fun initLogger(envDebug: Boolean){
        logger.initLogger(envDebug)
    }

    fun v(tag: String?, message: String) = logger.v(tag, message)
    fun d(tag: String?, message: String) = logger.d(tag, message)
    fun i(tag: String?, message: String) = logger.i(tag, message)
    fun w(tag: String?, message: String) = logger.w(tag, message)
    fun e(tag: String?, message: String) = logger.e(tag, message)
    fun e(tag: String?, throwable: Throwable?) = logger.e(tag, throwable)
    fun e(tag: String?, message: String, throwable: Throwable?) = logger.e(tag, message, throwable)
}