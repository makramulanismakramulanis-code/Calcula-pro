package com.example.domain

import com.example.data.model.CalculationResult
import java.math.BigInteger
import java.util.Locale
import kotlin.math.*

object MathEngine {

    fun calculatePercentage(
        mode: Int, // 0: X% of Y, 1: X is what % of Y, 2: % change from X to Y, 3: Add X% to Y
        val1: Double,
        val2: Double
    ): CalculationResult {
        return when (mode) {
            0 -> { // val1% of val2
                val result = (val1 * val2) / 100.0
                CalculationResult(
                    primaryValue = NumberFormatter.formatNumber(result, 2),
                    primaryLabel = "${NumberFormatter.formatNumber(val1, 2)}% of ${NumberFormatter.formatNumber(val2, 2)}",
                    secondarySummary = "($val1 / 100) × $val2 = ${NumberFormatter.formatNumber(result, 2)}",
                    breakdown = listOf(
                        "Percentage" to "${NumberFormatter.formatNumber(val1, 2)}%",
                        "Base Number" to NumberFormatter.formatNumber(val2, 2),
                        "Result" to NumberFormatter.formatNumber(result, 2)
                    ),
                    formulaUsed = "Result = (X × Y) / 100",
                    explanation = "Calculating $val1 percent of $val2 yields ${NumberFormatter.formatNumber(result, 2)}."
                )
            }
            1 -> { // val1 is what % of val2
                if (val2 == 0.0) {
                    return CalculationResult("Error", "Division by zero", "Base number cannot be zero")
                }
                val pct = (val1 / val2) * 100.0
                CalculationResult(
                    primaryValue = NumberFormatter.formatPercent(pct, 2),
                    primaryLabel = "${NumberFormatter.formatNumber(val1, 2)} is what % of ${NumberFormatter.formatNumber(val2, 2)}",
                    secondarySummary = "($val1 / $val2) × 100 = ${NumberFormatter.formatPercent(pct, 2)}",
                    breakdown = listOf(
                        "Part" to NumberFormatter.formatNumber(val1, 2),
                        "Whole" to NumberFormatter.formatNumber(val2, 2),
                        "Percentage Ratio" to NumberFormatter.formatPercent(pct, 2)
                    ),
                    formulaUsed = "Percentage = (X / Y) × 100",
                    explanation = "$val1 constitutes ${NumberFormatter.formatPercent(pct, 2)} of $val2."
                )
            }
            2 -> { // % change from val1 to val2
                if (val1 == 0.0) {
                    return CalculationResult("Error", "Division by zero", "Initial value cannot be zero")
                }
                val diff = val2 - val1
                val changePct = (diff / val1) * 100.0
                val isIncrease = changePct >= 0
                CalculationResult(
                    primaryValue = (if (isIncrease) "+" else "") + NumberFormatter.formatPercent(changePct, 2),
                    primaryLabel = if (isIncrease) "Percentage Increase" else "Percentage Decrease",
                    secondarySummary = "Absolute difference is ${NumberFormatter.formatNumber(diff, 2)}",
                    breakdown = listOf(
                        "Initial Value" to NumberFormatter.formatNumber(val1, 2),
                        "Final Value" to NumberFormatter.formatNumber(val2, 2),
                        "Absolute Change" to NumberFormatter.formatNumber(diff, 2),
                        "Percentage Change" to NumberFormatter.formatPercent(changePct, 2)
                    ),
                    formulaUsed = "Percentage Change = [(Final - Initial) / Initial] × 100",
                    explanation = if (isIncrease) "The value increased by ${NumberFormatter.formatPercent(changePct, 2)}." else "The value decreased by ${NumberFormatter.formatPercent(abs(changePct), 2)}."
                )
            }
            else -> { // Add val1% to val2
                val addAmount = (val1 / 100.0) * val2
                val finalVal = val2 + addAmount
                CalculationResult(
                    primaryValue = NumberFormatter.formatNumber(finalVal, 2),
                    primaryLabel = "Result after adding ${NumberFormatter.formatNumber(val1, 2)}%",
                    secondarySummary = "Increment amount is ${NumberFormatter.formatNumber(addAmount, 2)}",
                    breakdown = listOf(
                        "Original Value" to NumberFormatter.formatNumber(val2, 2),
                        "Percentage to Add" to "${NumberFormatter.formatNumber(val1, 2)}%",
                        "Added Amount" to NumberFormatter.formatNumber(addAmount, 2),
                        "Final Total" to NumberFormatter.formatNumber(finalVal, 2)
                    ),
                    formulaUsed = "Final = Base + (Base × Percentage / 100)"
                )
            }
        }
    }

    fun calculateFractions(
        num1: Long, den1: Long,
        op: String,
        num2: Long, den2: Long
    ): CalculationResult {
        if (den1 == 0L || den2 == 0L) {
            return CalculationResult("Error", "Invalid Denominator", "Denominator cannot be zero")
        }

        var resNum: Long
        var resDen: Long

        when (op) {
            "+" -> {
                resNum = num1 * den2 + num2 * den1
                resDen = den1 * den2
            }
            "-" -> {
                resNum = num1 * den2 - num2 * den1
                resDen = den1 * den2
            }
            "×", "*" -> {
                resNum = num1 * num2
                resDen = den1 * den2
            }
            "÷", "/" -> {
                if (num2 == 0L) return CalculationResult("Error", "Division by zero", "Cannot divide by 0")
                resNum = num1 * den2
                resDen = den1 * num2
            }
            else -> {
                resNum = 0
                resDen = 1
            }
        }

        if (resDen < 0) {
            resNum = -resNum
            resDen = -resDen
        }

        val gcdVal = gcd(abs(resNum), abs(resDen))
        val simpNum = resNum / gcdVal
        val simpDen = resDen / gcdVal
        val decimalVal = resNum.toDouble() / resDen.toDouble()

        val mixed = if (simpDen != 1L && abs(simpNum) > simpDen) {
            val whole = simpNum / simpDen
            val rem = abs(simpNum % simpDen)
            "$whole $rem/$simpDen"
        } else null

        val fractionString = if (simpDen == 1L) "$simpNum" else "$simpNum/$simpDen"

        return CalculationResult(
            primaryValue = fractionString,
            primaryLabel = "Simplified Fraction",
            secondarySummary = "Decimal: ${NumberFormatter.formatNumber(decimalVal, 4)}" + (if (mixed != null) " | Mixed: $mixed" else ""),
            breakdown = listOf(
                "Expression" to "$num1/$den1 $op $num2/$den2",
                "Unreduced Fraction" to "$resNum/$resDen",
                "Simplified Fraction" to fractionString,
                "Decimal Value" to NumberFormatter.formatNumber(decimalVal, 4),
                "Greatest Common Divisor (GCD)" to "$gcdVal"
            ),
            formulaUsed = "Cross multiplication of denominators and numerator addition/subtraction"
        )
    }

    private fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

    fun calculateQuadratic(a: Double, b: Double, c: Double): CalculationResult {
        if (a == 0.0) {
            // Linear equation bx + c = 0 -> x = -c/b
            if (b == 0.0) {
                return CalculationResult("No solution", "Degenerate Equation", "Both a and b are zero")
            }
            val root = -c / b
            return CalculationResult(
                primaryValue = "x = ${NumberFormatter.formatNumber(root, 4)}",
                primaryLabel = "Linear Root (a = 0)",
                secondarySummary = "Equation simplifies to linear: $b x + $c = 0"
            )
        }

        val discriminant = b * b - 4 * a * c
        val vertexX = -b / (2 * a)
        val vertexY = -discriminant / (4 * a)

        val (primary, summary, breakdown) = when {
            discriminant > 0 -> {
                val r1 = (-b + sqrt(discriminant)) / (2 * a)
                val r2 = (-b - sqrt(discriminant)) / (2 * a)
                Triple(
                    "x₁ = ${NumberFormatter.formatNumber(r1, 4)}, x₂ = ${NumberFormatter.formatNumber(r2, 4)}",
                    "Two distinct real roots (Δ = ${NumberFormatter.formatNumber(discriminant, 2)} > 0)",
                    listOf(
                        "Discriminant (b² - 4ac)" to NumberFormatter.formatNumber(discriminant, 4),
                        "Root 1 (x₁)" to NumberFormatter.formatNumber(r1, 4),
                        "Root 2 (x₂)" to NumberFormatter.formatNumber(r2, 4),
                        "Vertex (h, k)" to "(${NumberFormatter.formatNumber(vertexX, 2)}, ${NumberFormatter.formatNumber(vertexY, 2)})"
                    )
                )
            }
            discriminant == 0.0 -> {
                val r = -b / (2 * a)
                Triple(
                    "x = ${NumberFormatter.formatNumber(r, 4)}",
                    "One repeated real root (Δ = 0)",
                    listOf(
                        "Discriminant" to "0.0",
                        "Double Root" to NumberFormatter.formatNumber(r, 4),
                        "Vertex" to "(${NumberFormatter.formatNumber(vertexX, 2)}, 0.0)"
                    )
                )
            }
            else -> {
                val realPart = -b / (2 * a)
                val imagPart = sqrt(abs(discriminant)) / (2 * a)
                val realF = NumberFormatter.formatNumber(realPart, 3)
                val imagF = NumberFormatter.formatNumber(abs(imagPart), 3)
                Triple(
                    "$realF ± ${imagF}i",
                    "Two complex conjugate roots (Δ = ${NumberFormatter.formatNumber(discriminant, 2)} < 0)",
                    listOf(
                        "Discriminant" to NumberFormatter.formatNumber(discriminant, 4),
                        "Root 1" to "$realF + ${imagF}i",
                        "Root 2" to "$realF - ${imagF}i",
                        "Vertex" to "(${NumberFormatter.formatNumber(vertexX, 2)}, ${NumberFormatter.formatNumber(vertexY, 2)})"
                    )
                )
            }
        }

        return CalculationResult(
            primaryValue = primary,
            primaryLabel = "Roots of ax² + bx + c = 0",
            secondarySummary = summary,
            breakdown = breakdown,
            formulaUsed = "Quadratic Formula:\nx = (-b ± √(b² - 4ac)) / (2a)",
            explanation = "The discriminant Δ = b² - 4ac determines the nature of the roots. If Δ > 0, roots are real and unequal. If Δ = 0, roots are real and equal. If Δ < 0, roots are imaginary complex conjugates."
        )
    }

    fun calculateStatistics(numbers: List<Double>): CalculationResult {
        if (numbers.isEmpty()) {
            return CalculationResult("0", "Mean", "Enter at least one number")
        }

        val sorted = numbers.sorted()
        val count = sorted.size
        val sum = sorted.sum()
        val mean = sum / count

        val median = if (count % 2 == 1) {
            sorted[count / 2]
        } else {
            (sorted[count / 2 - 1] + sorted[count / 2]) / 2.0
        }

        val min = sorted.first()
        val max = sorted.last()
        val range = max - min

        // Variance & standard deviation
        val variance = sorted.sumOf { (it - mean).pow(2.0) } / (if (count > 1) (count - 1) else 1)
        val stdDev = sqrt(variance)

        // Mode
        val frequencyMap = numbers.groupingBy { it }.eachCount()
        val maxFreq = frequencyMap.values.maxOrNull() ?: 1
        val modes = frequencyMap.filter { it.value == maxFreq && maxFreq > 1 }.keys

        val modeStr = if (modes.isEmpty()) "None" else modes.joinToString(", ") { NumberFormatter.formatNumber(it, 2) }

        return CalculationResult(
            primaryValue = NumberFormatter.formatNumber(mean, 2),
            primaryLabel = "Mean (Average)",
            secondarySummary = "Median: ${NumberFormatter.formatNumber(median, 2)} | Std Dev: ${NumberFormatter.formatNumber(stdDev, 2)}",
            breakdown = listOf(
                "Sample Count (n)" to "$count",
                "Total Sum (Σx)" to NumberFormatter.formatNumber(sum, 2),
                "Mean (Average)" to NumberFormatter.formatNumber(mean, 2),
                "Median" to NumberFormatter.formatNumber(median, 2),
                "Mode" to modeStr,
                "Sample Std Dev (s)" to NumberFormatter.formatNumber(stdDev, 2),
                "Variance (s²)" to NumberFormatter.formatNumber(variance, 2),
                "Minimum" to NumberFormatter.formatNumber(min, 2),
                "Maximum" to NumberFormatter.formatNumber(max, 2),
                "Range" to NumberFormatter.formatNumber(range, 2)
            ),
            formulaUsed = "Mean = Σx / n\nSample Std Dev = √[Σ(x - x̄)² / (n - 1)]",
            explanation = "Statistical summary measures both central tendency (mean, median, mode) and dispersion (range, variance, standard deviation) across your data set."
        )
    }

    fun calculatePermutationCombination(n: Long, r: Long): CalculationResult {
        if (n < 0 || r < 0 || r > n) {
            return CalculationResult("Invalid", "nPr & nCr", "Condition: 0 ≤ r ≤ n")
        }

        val nFact = factorial(n)
        val rFact = factorial(r)
        val nMinusRFact = factorial(n - r)

        val nPr = nFact / nMinusRFact
        val nCr = nPr / rFact

        return CalculationResult(
            primaryValue = "nCr = $nCr | nPr = $nPr",
            primaryLabel = "Combinations & Permutations",
            secondarySummary = "n = $n items, r = $r selections",
            breakdown = listOf(
                "Combinations nCr (Order doesn't matter)" to "$nCr",
                "Permutations nPr (Order matters)" to "$nPr",
                "n! ($n factorial)" to "$nFact",
                "r! ($r factorial)" to "$rFact",
                "(n - r)!" to "$nMinusRFact"
            ),
            formulaUsed = "nPr = n! / (n - r)!\nnCr = n! / [r! × (n - r)!]",
            explanation = "Permutations count arrangements where sequence/order is significant. Combinations count selections where order does not matter."
        )
    }

    private fun factorial(n: Long): BigInteger {
        var result = BigInteger.ONE
        for (i in 2..n) {
            result = result.multiply(BigInteger.valueOf(i))
        }
        return result
    }

    // Scientific Calculator Evaluator
    fun evaluateScientificExpression(expression: String, isRadians: Boolean = true): Double {
        val sanitized = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("π", Math.PI.toString())
            .replace("e", Math.E.toString())

        return ExpressionParser(sanitized, isRadians).parse()
    }

    private class ExpressionParser(private val str: String, private val isRadians: Boolean) {
        private var pos = -1
        private var ch = ' '

        private fun nextChar() {
            ch = if (++pos < str.length) str[pos] else '\u0000'
        }

        private fun eat(charToEat: Char): Boolean {
            while (ch == ' ') nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < str.length) throw RuntimeException("Unexpected: $ch")
            return x
        }

        private fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+') -> x += parseTerm()
                    eat('-') -> x -= parseTerm()
                    else -> return x
                }
            }
        }

        private fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*') -> x *= parseFactor()
                    eat('/') -> {
                        val d = parseFactor()
                        if (d == 0.0) throw ArithmeticException("Division by zero")
                        x /= d
                    }
                    eat('%') -> x %= parseFactor()
                    else -> return x
                }
            }
        }

        private fun parseFactor(): Double {
            if (eat('+')) return parseFactor()
            if (eat('-')) return -parseFactor()

            var x: Double
            val startPos = pos
            if (eat('(')) {
                x = parseExpression()
                eat(')')
            } else if ((ch in '0'..'9') || ch == '.') {
                while ((ch in '0'..'9') || ch == '.') nextChar()
                x = str.substring(startPos, pos).toDouble()
            } else if (ch in 'a'..'z') {
                while (ch in 'a'..'z') nextChar()
                val func = str.substring(startPos, pos)
                x = parseFactor()
                x = when (func) {
                    "sqrt" -> sqrt(x)
                    "sin" -> sin(if (isRadians) x else Math.toRadians(x))
                    "cos" -> cos(if (isRadians) x else Math.toRadians(x))
                    "tan" -> tan(if (isRadians) x else Math.toRadians(x))
                    "asin" -> if (isRadians) asin(x) else Math.toDegrees(asin(x))
                    "acos" -> if (isRadians) acos(x) else Math.toDegrees(acos(x))
                    "atan" -> if (isRadians) atan(x) else Math.toDegrees(atan(x))
                    "log" -> log10(x)
                    "ln" -> ln(x)
                    "abs" -> abs(x)
                    else -> throw RuntimeException("Unknown function: $func")
                }
            } else {
                throw RuntimeException("Unexpected: $ch")
            }

            if (eat('^')) x = x.pow(parseFactor())
            return x
        }
    }
}
