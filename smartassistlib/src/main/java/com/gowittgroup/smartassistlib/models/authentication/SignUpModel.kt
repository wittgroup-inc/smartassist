package com.gowittgroup.smartassistlib.models.authentication

data class SignUpModel(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)