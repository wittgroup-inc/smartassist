package com.gowittgroup.smartassist.util

object AvatarGenerator {
    val styles = listOf("adventurer-neutral", "miniavs", "big-ears", "bottts", "thumbs", "personas")

    fun generateAvatars(userId: String): List<String> {
        return styles.map { style ->
            "https://api.dicebear.com/6.x/${style}/png?seed=${userId}"
        }
    }
}