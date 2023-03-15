package com.wittgroup.smartassistlib.db.dao

import androidx.room.*
import com.wittgroup.smartassistlib.db.entities.ConversationHistory

@Dao
interface ConversationHistoryDao {
    @Query("SELECT * FROM ConversationHistory ORDER BY timestamp DESC")
    fun getAll(): List<ConversationHistory>

    @Query("SELECT * FROM ConversationHistory WHERE conversationId = :id")
    fun loadByIds(id: Long): ConversationHistory

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg history: ConversationHistory)

    @Delete
    fun delete(history: ConversationHistory)
}
