package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY createdAtEpochMillis DESC")
    fun observeTransactions(): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT * FROM transactions
        WHERE (:query IS NULL OR description LIKE '%' || :query || '%')
        AND (:type IS NULL OR type = :type)
        AND (:startEpochMillis IS NULL OR createdAtEpochMillis >= :startEpochMillis)
        AND (:endEpochMillis IS NULL OR createdAtEpochMillis < :endEpochMillis)
        ORDER BY createdAtEpochMillis DESC
        """
    )
    fun observeTransactionsFiltered(
        query: String?,
        type: TransactionType?,
        startEpochMillis: Long?,
        endEpochMillis: Long?
    ): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<TransactionEntity>)

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteById(transactionId: String)
}
