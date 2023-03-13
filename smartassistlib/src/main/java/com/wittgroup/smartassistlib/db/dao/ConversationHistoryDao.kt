package com.wittgroup.smartassistlib.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.wittgroup.smartassistlib.db.entities.ConversationHistory

@Dao
interface ConversationHistoryDao {
    @Query("SELECT * FROM ConversationHistory")
    fun getAll(): List<ConversationHistory>

    @Query("SELECT * FROM ConversationHistory WHERE conversationId = :id")
    fun loadByIds(id: Long): ConversationHistory

    @Insert
    fun insertAll(vararg history: ConversationHistory)

    @Delete
    fun delete(history: ConversationHistory)
}
