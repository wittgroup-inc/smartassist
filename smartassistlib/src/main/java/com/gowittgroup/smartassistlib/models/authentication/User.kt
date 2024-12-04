package com.gowittgroup.smartassistlib.models.authentication

data class User(
    val id: String = "",
    val displayName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val email: String = "",
    val photoUrl: String? = null
)