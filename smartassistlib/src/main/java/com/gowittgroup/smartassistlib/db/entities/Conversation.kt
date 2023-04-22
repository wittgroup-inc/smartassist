package com.gowittgroup.smartassistlib.db.entities

data class Conversation(val data: String, val isQuestion: Boolean, val forSystem: Boolean = false)
