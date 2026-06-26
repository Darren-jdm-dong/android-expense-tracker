package com.example.expense.data.db

import androidx.room.TypeConverter
import com.example.expense.data.db.entity.RecurringFrequency
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class Converters {
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? {
        return value?.toPlainString()
    }

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? {
        return value?.let { BigDecimal(it) }
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.toString()
    }

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun fromRecurringFrequency(frequency: RecurringFrequency?): String? {
        return frequency?.name
    }

    @TypeConverter
    fun toRecurringFrequency(frequencyString: String?): RecurringFrequency? {
        return frequencyString?.let { RecurringFrequency.valueOf(it) }
    }
}
