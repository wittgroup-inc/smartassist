package com.gowittgroup.smartassistlib.models

data class SignUpModel(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val gender: String
)