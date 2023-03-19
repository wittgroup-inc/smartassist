package com.gowittgroup.smartassistlib.db.dao

import androidx.room.*
import com.gowittgroup.smartassistlib.db.entities.ConversationHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationHistoryDao {
    @Query("SELECT * FROM ConversationHistory ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ConversationHistory>>

    @Query("SELECT * FROM ConversationHistory WHERE conversationId = :id")
    fun loadByIds(id: Long): ConversationHistory

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg history: ConversationHistory)

    @Delete
    fun delete(history: ConversationHistory)
}
