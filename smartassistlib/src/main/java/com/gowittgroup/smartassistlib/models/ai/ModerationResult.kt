package com.gowittgroup.smartassistlib.models.ai

data class ModerationResult(val isSafe: Boolean, val cause: List<String>)
