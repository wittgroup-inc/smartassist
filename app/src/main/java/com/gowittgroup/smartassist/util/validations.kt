package com.gowittgroup.smartassist.util

fun isEmailValid(email: String): Boolean {

    return email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))
}

fun isPasswordStrong(password: String): Boolean {
    return password.length >= 8 &&
            password.any { it.isUpperCase() } &&
            password.any { it.isLowerCase() } &&
            password.any { it.isDigit() } &&
            password.any { "!@#\$%^&*()-_=+[]{}|;:,.<>?".contains(it) }
}