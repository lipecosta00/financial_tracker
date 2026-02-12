package com.example.data.local

import androidx.room.TypeConverter
import com.example.domain.model.TransactionType

class TransactionTypeConverter {

    @TypeConverter
    fun fromType(type: TransactionType): String = type.name

    @TypeConverter
    fun toType(value: String): TransactionType = TransactionType.valueOf(value)
}
