package com.example.domain

import com.example.data.local.NumberFormatStyle
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

object NumberFormatter {

    fun formatNumber(
        value: Double,
        decimalPlaces: Int = 2,
        formatStyle: NumberFormatStyle = NumberFormatStyle.INDIAN
    ): String {
        if (value.isNaN()) return "0"
        if (value.isInfinite()) return if (value > 0) "∞" else "-∞"

        val isNegative = value < 0
        val absValue = abs(value)

        val pattern = when (decimalPlaces) {
            0 -> "#,##0"
            1 -> "#,##0.0"
            2 -> "#,##0.00"
            3 -> "#,##0.000"
            else -> "#,##0.0000"
        }

        return if (formatStyle == NumberFormatStyle.INDIAN) {
            val formatted = formatIndianStyle(absValue, decimalPlaces)
            if (isNegative) "-$formatted" else formatted
        } else {
            val symbols = DecimalFormatSymbols(Locale.US)
            val df = DecimalFormat(pattern, symbols)
            val formatted = df.format(absValue)
            if (isNegative) "-$formatted" else formatted
        }
    }

    fun formatCurrency(
        value: Double,
        currencySymbol: String = "₹",
        decimalPlaces: Int = 2,
        formatStyle: NumberFormatStyle = NumberFormatStyle.INDIAN
    ): String {
        val num = formatNumber(value, decimalPlaces, formatStyle)
        return "$currencySymbol $num"
    }

    fun formatPercent(value: Double, decimalPlaces: Int = 2): String {
        val pattern = when (decimalPlaces) {
            0 -> "#,##0"
            1 -> "#,##0.0"
            else -> "#,##0.00"
        }
        val df = DecimalFormat(pattern, DecimalFormatSymbols(Locale.US))
        return "${df.format(value)}%"
    }

    private fun formatIndianStyle(value: Double, decimalPlaces: Int): String {
        val longVal = value.toLong()
        val remainder = value - longVal
        val s = longVal.toString()
        val len = s.length

        val intFormatted = if (len <= 3) {
            s
        } else {
            val lastThree = s.substring(len - 3)
            val rest = s.substring(0, len - 3)
            val builder = StringBuilder()
            var count = 0
            for (i in rest.length - 1 downTo 0) {
                builder.append(rest[i])
                count++
                if (count == 2 && i > 0) {
                    builder.append(',')
                    count = 0
                }
            }
            builder.reverse().toString() + "," + lastThree
        }

        if (decimalPlaces <= 0) return intFormatted

        val decPattern = "%.${decimalPlaces}f"
        val decStr = String.format(Locale.US, decPattern, remainder).substring(1) // gets ".XX"
        return intFormatted + decStr
    }
}
