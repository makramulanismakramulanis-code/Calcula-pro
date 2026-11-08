package com.example.domain

import com.example.data.model.CalculationResult
import com.example.data.model.ChartData
import com.example.data.model.ChartSegment
import kotlin.math.pow

object HealthEngine {

    enum class Gender { MALE, FEMALE }
    enum class ActivityLevel(val multiplier: Double, val label: String) {
        SEDENTARY(1.2, "Sedentary (Little or no exercise)"),
        LIGHT(1.375, "Lightly Active (Exercise 1-3 days/week)"),
        MODERATE(1.55, "Moderately Active (Exercise 3-5 days/week)"),
        VERY_ACTIVE(1.725, "Very Active (Hard exercise 6-7 days/week)"),
        EXTRA_ACTIVE(1.9, "Extremely Active (Athletic/physical job)")
    }

    fun calculateBmi(weightKg: Double, heightCm: Double): CalculationResult {
        if (weightKg <= 0 || heightCm <= 0) {
            return CalculationResult("0.0", "BMI Score", "Enter valid weight and height")
        }

        val heightM = heightCm / 100.0
        val bmi = weightKg / (heightM * heightM)

        val (category, colorHex, advice) = when {
            bmi < 18.5 -> Triple("Underweight", 0xFF3B82F6, "Below standard healthy range. Consider consulting a nutritionist.")
            bmi < 25.0 -> Triple("Normal Weight", 0xFF10B981, "Healthy weight! Maintain with balanced nutrition and activity.")
            bmi < 30.0 -> Triple("Overweight", 0xFFF59E0B, "Above recommended range. Regular cardio and diet adjustments recommended.")
            bmi < 35.0 -> Triple("Obesity Class I", 0xFFEF4444, "Health risk elevated. Lifestyle interventions advised.")
            bmi < 40.0 -> Triple("Obesity Class II", 0xFFDC2626, "High health risk. Medical guidance recommended.")
            else -> Triple("Obesity Class III", 0xFF991B1B, "Severe health risk. Clinical intervention strongly advised.")
        }

        val minHealthyWeight = 18.5 * (heightM * heightM)
        val maxHealthyWeight = 24.9 * (heightM * heightM)

        val bmiF = NumberFormatter.formatNumber(bmi, 1)
        val minWF = NumberFormatter.formatNumber(minHealthyWeight, 1)
        val maxWF = NumberFormatter.formatNumber(maxHealthyWeight, 1)

        return CalculationResult(
            primaryValue = bmiF,
            primaryLabel = "Body Mass Index (BMI)",
            secondarySummary = "Category: $category | Healthy Range: $minWF - $maxWF kg",
            breakdown = listOf(
                "BMI Value" to bmiF,
                "WHO Weight Category" to category,
                "Healthy Weight Range" to "$minWF kg to $maxWF kg",
                "Input Weight" to "${NumberFormatter.formatNumber(weightKg, 1)} kg",
                "Input Height" to "${NumberFormatter.formatNumber(heightCm, 1)} cm (${NumberFormatter.formatNumber(heightM * 3.28084, 1)} ft)"
            ),
            formulaUsed = "BMI = Weight (kg) / [Height (m)]²",
            explanation = "$advice\n\n*Educational Disclaimer: BMI is a screening indicator of body fatness and not a diagnostic tool for individual body composition or clinical health.",
            chartData = ChartData.ScaleGauge(
                currentValue = bmi.toFloat(),
                minValue = 15f,
                maxValue = 40f,
                markers = listOf(
                    18.5f to "Normal",
                    25.0f to "Overweight",
                    30.0f to "Obese"
                ),
                currentLabel = category
            )
        )
    }

    fun calculateBmr(
        gender: Gender,
        weightKg: Double,
        heightCm: Double,
        ageYears: Int
    ): CalculationResult {
        if (weightKg <= 0 || heightCm <= 0 || ageYears <= 0) {
            return CalculationResult("0", "BMR", "Enter positive values")
        }

        // Mifflin-St Jeor Formula
        val bmr = if (gender == Gender.MALE) {
            (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * ageYears) + 5.0
        } else {
            (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * ageYears) - 161.0
        }

        val bmrF = NumberFormatter.formatNumber(bmr, 0)

        return CalculationResult(
            primaryValue = "$bmrF kcal/day",
            primaryLabel = "Basal Metabolic Rate (BMR)",
            secondarySummary = "Calories burned at complete rest to maintain vital life functions",
            breakdown = listOf(
                "BMR Calories" to "$bmrF kcal/day",
                "Formula" to "Mifflin-St Jeor Equation",
                "Gender" to if (gender == Gender.MALE) "Male (+5 offset)" else "Female (-161 offset)",
                "Age" to "$ageYears Years",
                "Weight" to "${NumberFormatter.formatNumber(weightKg, 1)} kg",
                "Height" to "${NumberFormatter.formatNumber(heightCm, 1)} cm"
            ),
            formulaUsed = "Men: BMR = (10 × W) + (6.25 × H) - (5 × A) + 5\nWomen: BMR = (10 × W) + (6.25 × H) - (5 × A) - 161",
            explanation = "Your BMR represents the minimum energy expenditure needed to sustain breathing, blood circulation, cell production, and brain activity at rest."
        )
    }

    fun calculateDailyCalories(
        gender: Gender,
        weightKg: Double,
        heightCm: Double,
        ageYears: Int,
        activityLevel: ActivityLevel
    ): CalculationResult {
        val bmr = if (gender == Gender.MALE) {
            (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * ageYears) + 5.0
        } else {
            (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * ageYears) - 161.0
        }

        val tdee = bmr * activityLevel.multiplier
        val mildLoss = tdee - 250.0
        val weightLoss = tdee - 500.0
        val weightGain = tdee + 400.0

        val tdeeF = NumberFormatter.formatNumber(tdee, 0)

        return CalculationResult(
            primaryValue = "$tdeeF kcal",
            primaryLabel = "Daily Maintenance Calories (TDEE)",
            secondarySummary = "Weight Loss target: ${NumberFormatter.formatNumber(weightLoss, 0)} kcal/day",
            breakdown = listOf(
                "Maintenance (TDEE)" to "$tdeeF kcal/day",
                "Mild Weight Loss (-0.25 kg/wk)" to "${NumberFormatter.formatNumber(mildLoss, 0)} kcal/day",
                "Weight Loss (-0.5 kg/wk)" to "${NumberFormatter.formatNumber(weightLoss, 0)} kcal/day",
                "Muscle / Weight Gain (+0.4 kg/wk)" to "${NumberFormatter.formatNumber(weightGain, 0)} kcal/day",
                "Activity Multiplier" to "${activityLevel.multiplier}x (${activityLevel.label})"
            ),
            formulaUsed = "TDEE = BMR × Activity Level Multiplier",
            explanation = "Total Daily Energy Expenditure (TDEE) accounts for your base metabolism plus calories burned through daily movement, work, and workouts."
        )
    }

    fun calculateWaterIntake(weightKg: Double, exerciseMinutes: Int, isHotClimate: Boolean): CalculationResult {
        if (weightKg <= 0) {
            return CalculationResult("0.0 L", "Daily Water Target", "Enter valid body weight")
        }

        // Base 35ml per kg + 350ml per 30 min workout + 500ml for hot climate
        val baseWaterMl = weightKg * 35.0
        val workoutWaterMl = (exerciseMinutes / 30.0) * 350.0
        val climateWaterMl = if (isHotClimate) 500.0 else 0.0

        val totalMl = baseWaterMl + workoutWaterMl + climateWaterMl
        val totalLiters = totalMl / 1000.0
        val glasses = totalMl / 250.0 // 250ml glass

        val litF = NumberFormatter.formatNumber(totalLiters, 2)
        val glsF = NumberFormatter.formatNumber(glasses, 0)

        return CalculationResult(
            primaryValue = "$litF Liters/day",
            primaryLabel = "Recommended Daily Hydration",
            secondarySummary = "Approximately $glsF standard glasses (250 ml each)",
            breakdown = listOf(
                "Total Daily Volume" to "$litF Liters ($totalMl ml)",
                "Standard 250ml Glasses" to "$glsF Glasses",
                "Base Body Requirement" to "${NumberFormatter.formatNumber(baseWaterMl / 1000.0, 2)} L (35ml/kg)",
                "Workout Replenishment" to "${NumberFormatter.formatNumber(workoutWaterMl / 1000.0, 2)} L ($exerciseMinutes mins)",
                "Climate Compensation" to if (isHotClimate) "+0.5 L (Warm/Tropical)" else "Standard"
            ),
            formulaUsed = "Daily Water = (Weight in kg × 35 ml) + (Exercise Mins / 30 × 350 ml) + Climate Factor",
            explanation = "Adequate hydration supports nutrient transport, joint lubrication, cognitive focus, and body temperature regulation."
        )
    }

    fun calculateIdealWeight(gender: Gender, heightCm: Double): CalculationResult {
        if (heightCm <= 0) {
            return CalculationResult("0 kg", "Ideal Weight", "Enter valid height")
        }

        val heightInches = heightCm / 2.54
        val inchesOver5Feet = (heightInches - 60.0).coerceAtLeast(0.0)

        // Devine Formula (1974)
        val devine = if (gender == Gender.MALE) {
            50.0 + (2.3 * inchesOver5Feet)
        } else {
            45.5 + (2.3 * inchesOver5Feet)
        }

        // Robinson Formula (1983)
        val robinson = if (gender == Gender.MALE) {
            52.0 + (1.9 * inchesOver5Feet)
        } else {
            49.0 + (1.7 * inchesOver5Feet)
        }

        // Miller Formula (1983)
        val miller = if (gender == Gender.MALE) {
            56.2 + (1.41 * inchesOver5Feet)
        } else {
            53.1 + (1.36 * inchesOver5Feet)
        }

        val avgIdeal = (devine + robinson + miller) / 3.0
        val devF = NumberFormatter.formatNumber(devine, 1)
        val avgF = NumberFormatter.formatNumber(avgIdeal, 1)

        return CalculationResult(
            primaryValue = "$avgF kg",
            primaryLabel = "Ideal Body Weight (Average)",
            secondarySummary = "Devine: $devF kg | Robinson: ${NumberFormatter.formatNumber(robinson, 1)} kg",
            breakdown = listOf(
                "Consensus Ideal Weight" to "$avgF kg (${NumberFormatter.formatNumber(avgIdeal * 2.20462, 1)} lbs)",
                "Devine Formula (Clinical standard)" to "$devF kg",
                "Robinson Formula" to "${NumberFormatter.formatNumber(robinson, 1)} kg",
                "Miller Formula" to "${NumberFormatter.formatNumber(miller, 1)} kg",
                "Target Height" to "${NumberFormatter.formatNumber(heightCm, 1)} cm"
            ),
            formulaUsed = "Devine Formula (1974):\nMen: 50 kg + 2.3 kg per inch over 5 ft\nWomen: 45.5 kg + 2.3 kg per inch over 5 ft",
            explanation = "Ideal body weight equations estimate healthy target weights for adults based on statistical epidemiological data and medical dosage protocols."
        )
    }
}
