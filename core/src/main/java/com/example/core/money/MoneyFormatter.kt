package com.example.core.money

import java.math.BigDecimal
import java.math.RoundingMode

object MoneyFormatter {
    fun normalize(value: BigDecimal): BigDecimal = value.setScale(2, RoundingMode.HALF_EVEN)
}
