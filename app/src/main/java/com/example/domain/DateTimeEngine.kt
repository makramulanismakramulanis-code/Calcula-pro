package com.example.domain

import com.example.data.model.CalculationResult
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object DateTimeEngine {

    fun calculateAge(birthYear: Int, birthMonth: Int, birthDay: Int): CalculationResult {
        val birthCal = Calendar.getInstance().apply {
            set(birthYear, birthMonth - 1, birthDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (birthCal.after(today)) {
            return CalculationResult("Future Date", "Invalid Birth Date", "Birth date cannot be in the future")
        }

        var years = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)
        var months = today.get(Calendar.MONTH) - birthCal.get(Calendar.MONTH)
        var days = today.get(Calendar.DAY_OF_MONTH) - birthCal.get(Calendar.DAY_OF_MONTH)

        if (days < 0) {
            months--
            val prevMonthCal = (today.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
            days += prevMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        if (months < 0) {
            years--
            months += 12
        }

        val diffMillis = today.timeInMillis - birthCal.timeInMillis
        val totalDays = TimeUnit.MILLISECONDS.toDays(diffMillis)
        val totalWeeks = totalDays / 7
        val totalHours = totalDays * 24

        // Next Birthday
        val nextBirthday = Calendar.getInstance().apply {
            set(today.get(Calendar.YEAR), birthMonth - 1, birthDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(today) || equals(today)) {
                add(Calendar.YEAR, 1)
            }
        }
        val daysUntilBirthday = TimeUnit.MILLISECONDS.toDays(nextBirthday.timeInMillis - today.timeInMillis)
        val birthdayDayOfWeek = SimpleDateFormat("EEEE", Locale.US).format(nextBirthday.time)

        val primaryStr = "$years Years, $months Months, $days Days"

        return CalculationResult(
            primaryValue = primaryStr,
            primaryLabel = "Current Age",
            secondarySummary = "Next Birthday in $daysUntilBirthday days (on a $birthdayDayOfWeek)",
            breakdown = listOf(
                "Age in Years & Months" to "$years Years, $months Months, $days Days",
                "Total Days Lived" to "${NumberFormatter.formatNumber(totalDays.toDouble(), 0)} Days",
                "Total Weeks" to "${NumberFormatter.formatNumber(totalWeeks.toDouble(), 0)} Weeks",
                "Total Hours" to "${NumberFormatter.formatNumber(totalHours.toDouble(), 0)} Hours",
                "Next Birthday Countdown" to "$daysUntilBirthday Days remaining ($birthdayDayOfWeek)"
            ),
            formulaUsed = "Calendar Period Delta:\nYears = ΔYear; Months = ΔMonth; Days = ΔDay with leap-year normalization",
            explanation = "Your chronological age calculated from date of birth against the current local date."
        )
    }

    fun calculateDateDifference(
        startYear: Int, startMonth: Int, startDay: Int,
        endYear: Int, endMonth: Int, endDay: Int
    ): CalculationResult {
        val startCal = Calendar.getInstance().apply {
            set(startYear, startMonth - 1, startDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endCal = Calendar.getInstance().apply {
            set(endYear, endMonth - 1, endDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val isSwapped = endCal.before(startCal)
        val earlyCal = if (isSwapped) endCal else startCal
        val lateCal = if (isSwapped) startCal else endCal

        val diffMillis = lateCal.timeInMillis - earlyCal.timeInMillis
        val totalDays = TimeUnit.MILLISECONDS.toDays(diffMillis)
        val totalWeeks = totalDays / 7
        val remainingDays = totalDays % 7

        // Working days count
        var workingDays = 0
        var weekendDays = 0
        val cursor = earlyCal.clone() as Calendar
        while (cursor.before(lateCal)) {
            val dow = cursor.get(Calendar.DAY_OF_WEEK)
            if (dow == Calendar.SATURDAY || dow == Calendar.SUNDAY) {
                weekendDays++
            } else {
                workingDays++
            }
            cursor.add(Calendar.DAY_OF_MONTH, 1)
        }

        return CalculationResult(
            primaryValue = "$totalDays Days",
            primaryLabel = "Total Days Difference",
            secondarySummary = "$totalWeeks Weeks and $remainingDays Days",
            breakdown = listOf(
                "Total Calendar Days" to "$totalDays Days",
                "Working Days (Mon - Fri)" to "$workingDays Days",
                "Weekend Days (Sat - Sun)" to "$weekendDays Days",
                "Equivalent Weeks" to "${NumberFormatter.formatNumber(totalDays / 7.0, 1)} Weeks",
                "Equivalent Months (approx)" to "${NumberFormatter.formatNumber(totalDays / 30.4375, 1)} Months",
                "Equivalent Years (approx)" to "${NumberFormatter.formatNumber(totalDays / 365.25, 2)} Years"
            ),
            formulaUsed = "Elapsed Days = (End Date - Start Date) / (1000 × 60 × 60 × 24)"
        )
    }

    fun calculateDateMath(
        year: Int, month: Int, day: Int,
        addDays: Int, addMonths: Int, addYears: Int,
        isSubtract: Boolean = false
    ): CalculationResult {
        val cal = Calendar.getInstance().apply {
            set(year, month - 1, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val multiplier = if (isSubtract) -1 else 1
        cal.add(Calendar.DAY_OF_MONTH, addDays * multiplier)
        cal.add(Calendar.MONTH, addMonths * multiplier)
        cal.add(Calendar.YEAR, addYears * multiplier)

        val sdf = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US)
        val resultDateStr = sdf.format(cal.time)

        return CalculationResult(
            primaryValue = resultDateStr,
            primaryLabel = if (isSubtract) "Resulting Past Date" else "Resulting Target Date",
            secondarySummary = "${if (isSubtract) "Subtracted" else "Added"} $addYears yrs, $addMonths mos, $addDays days",
            breakdown = listOf(
                "Target Date" to resultDateStr,
                "Day of Week" to SimpleDateFormat("EEEE", Locale.US).format(cal.time),
                "Day of Year" to "${cal.get(Calendar.DAY_OF_YEAR)} of ${cal.get(Calendar.YEAR)}"
            ),
            formulaUsed = "Gregorian Calendar relative addition/subtraction"
        )
    }
}
