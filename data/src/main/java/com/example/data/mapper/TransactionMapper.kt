package com.example.data.mapper

import com.example.data.local.TransactionEntity
import com.example.domain.model.CreateTransactionCommand
import com.example.domain.model.FinancialTransaction
import com.example.domain.model.UpdateTransactionCommand
import java.time.Instant
import java.util.UUID

fun TransactionEntity.toDomain(): FinancialTransaction = FinancialTransaction(
    id = id,
    description = description,
    amount = amount.toBigDecimal(),
    type = type,
    createdAt = Instant.ofEpochMilli(createdAtEpochMillis)
)

fun FinancialTransaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    description = description,
    amount = amount.toPlainString(),
    type = type,
    createdAtEpochMillis = createdAt.toEpochMilli()
)

fun CreateTransactionCommand.toEntity(): TransactionEntity = TransactionEntity(
    id = UUID.randomUUID().toString(),
    description = description,
    amount = amount.toPlainString(),
    type = type,
    createdAtEpochMillis = Instant.now().toEpochMilli()
)

fun UpdateTransactionCommand.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    description = description,
    amount = amount.toPlainString(),
    type = type,
    createdAtEpochMillis = createdAt.toEpochMilli()
)
