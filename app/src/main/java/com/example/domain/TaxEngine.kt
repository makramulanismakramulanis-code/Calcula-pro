package com.example.domain

import com.example.data.local.UserPreferences
import com.example.data.model.CalculationResult
import com.example.data.model.ChartData
import com.example.data.model.ChartSegment
import com.example.data.model.BarItem

object TaxEngine {

    enum class GstType {
        EXCLUSIVE, INCLUSIVE
    }

    fun calculateGst(
        amount: Double,
        gstRatePercent: Double,
        type: GstType,
        isInterState: Boolean,
        prefs: UserPreferences
    ): CalculationResult {
        if (amount <= 0) {
            return CalculationResult("0", "Total Amount", "Enter valid positive amount")
        }

        val rate = gstRatePercent.coerceAtLeast(0.0)
        val baseAmount: Double
        val gstAmount: Double
        val totalAmount: Double

        if (type == GstType.EXCLUSIVE) {
            baseAmount = amount
            gstAmount = baseAmount * (rate / 100.0)
            totalAmount = baseAmount + gstAmount
        } else {
            totalAmount = amount
            baseAmount = totalAmount / (1.0 + (rate / 100.0))
            gstAmount = totalAmount - baseAmount
        }

        val cgst = if (isInterState) 0.0 else gstAmount / 2.0
        val sgst = if (isInterState) 0.0 else gstAmount / 2.0
        val igst = if (isInterState) gstAmount else 0.0

        val baseF = NumberFormatter.formatCurrency(baseAmount, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val gstF = NumberFormatter.formatCurrency(gstAmount, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val totF = NumberFormatter.formatCurrency(totalAmount, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)

        val breakdownList = mutableListOf(
            "Net / Base Price" to baseF,
            "Total GST ($rate%)" to gstF,
            "Gross Total" to totF
        )

        if (isInterState) {
            breakdownList.add("IGST (Integrated Tax)" to gstF)
        } else {
            val halfF = NumberFormatter.formatCurrency(cgst, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
            breakdownList.add("CGST (${rate / 2}%)" to halfF)
            breakdownList.add("SGST (${rate / 2}%)" to halfF)
        }

        val formula = if (type == GstType.EXCLUSIVE) {
            "GST Exclusive:\nGST Amount = (Base Amount × GST%) / 100\nTotal Amount = Base Amount + GST Amount"
        } else {
            "GST Inclusive:\nBase Amount = Total / (1 + GST%/100)\nGST Amount = Total - Base Amount"
        }

        return CalculationResult(
            primaryValue = if (type == GstType.EXCLUSIVE) totF else gstF,
            primaryLabel = if (type == GstType.EXCLUSIVE) "Gross Total Amount" else "GST Tax Amount",
            secondarySummary = "Net Price: $baseF | GST ($rate%): $gstF",
            breakdown = breakdownList,
            formulaUsed = formula,
            explanation = "Goods and Services Tax (GST) is calculated either as an addition (Exclusive) or extracted from the retail selling price (Inclusive). For intra-state supply, GST is split equally into CGST and SGST.",
            chartData = ChartData.DonutOrPie(
                listOf(
                    ChartSegment("Base Price", baseAmount, 0xFF4338CA),
                    ChartSegment("GST Tax", gstAmount, 0xFF06B6D4)
                )
            )
        )
    }

    fun calculateIncomeTaxIndia(
        annualGrossSalary: Double,
        deductions80c: Double = 0.0,
        deductions80d: Double = 0.0,
        hraExemption: Double = 0.0,
        otherDeductions: Double = 0.0,
        prefs: UserPreferences
    ): CalculationResult {
        if (annualGrossSalary <= 0) {
            return CalculationResult("0", "Tax Payable", "Enter valid annual gross income")
        }

        // New Tax Regime (FY 2024-25 / 2025-26 as per Union Budget 2024)
        val newStdDeduction = 75000.0
        val newTaxableIncome = (annualGrossSalary - newStdDeduction).coerceAtLeast(0.0)

        var newBaseTax = 0.0
        if (newTaxableIncome > 700000.0) { // Section 87A rebate applies up to 7 Lakh taxable
            var rem = newTaxableIncome
            // 0 - 3L: 0%
            rem -= 300000.0
            if (rem > 0) {
                // 3L - 7L: 5% (max 4L @ 5% = 20,000)
                val slab1 = rem.coerceAtMost(400000.0)
                newBaseTax += slab1 * 0.05
                rem -= slab1
            }
            if (rem > 0) {
                // 7L - 10L: 10% (max 3L @ 10% = 30,000)
                val slab2 = rem.coerceAtMost(300000.0)
                newBaseTax += slab2 * 0.10
                rem -= slab2
            }
            if (rem > 0) {
                // 10L - 12L: 15% (max 2L @ 15% = 30,000)
                val slab3 = rem.coerceAtMost(200000.0)
                newBaseTax += slab3 * 0.15
                rem -= slab3
            }
            if (rem > 0) {
                // 12L - 15L: 20% (max 3L @ 20% = 60,000)
                val slab4 = rem.coerceAtMost(300000.0)
                newBaseTax += slab4 * 0.20
                rem -= slab4
            }
            if (rem > 0) {
                // Above 15L: 30%
                newBaseTax += rem * 0.30
            }
        }
        val newCess = newBaseTax * 0.04
        val newTotalTax = newBaseTax + newCess
        val newTakeHome = annualGrossSalary - newTotalTax

        // Old Tax Regime
        val oldStdDeduction = 50000.0
        val capped80C = deductions80c.coerceAtMost(150000.0)
        val totalOldDeductions = oldStdDeduction + capped80C + deductions80d + hraExemption + otherDeductions
        val oldTaxableIncome = (annualGrossSalary - totalOldDeductions).coerceAtLeast(0.0)

        var oldBaseTax = 0.0
        if (oldTaxableIncome > 500000.0) { // Section 87A rebate applies up to 5 Lakh in Old Regime
            var rem = oldTaxableIncome
            // 0 - 2.5L: 0%
            rem -= 250000.0
            if (rem > 0) {
                // 2.5L - 5L: 5% (max 2.5L @ 5% = 12,500)
                val slab1 = rem.coerceAtMost(250000.0)
                oldBaseTax += slab1 * 0.05
                rem -= slab1
            }
            if (rem > 0) {
                // 5L - 10L: 20% (max 5L @ 20% = 100,000)
                val slab2 = rem.coerceAtMost(500000.0)
                oldBaseTax += slab2 * 0.20
                rem -= slab2
            }
            if (rem > 0) {
                // Above 10L: 30%
                oldBaseTax += rem * 0.30
            }
        }
        val oldCess = oldBaseTax * 0.04
        val oldTotalTax = oldBaseTax + oldCess
        val oldTakeHome = annualGrossSalary - oldTotalTax

        val savings = oldTotalTax - newTotalTax
        val recommendation = if (savings > 0) {
            "New Regime saves ${NumberFormatter.formatCurrency(savings, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)} more tax!"
        } else if (savings < 0) {
            "Old Regime saves ${NumberFormatter.formatCurrency(-savings, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)} with your deductions."
        } else {
            "Both regimes yield identical tax payable."
        }

        val newTaxF = NumberFormatter.formatCurrency(newTotalTax, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val oldTaxF = NumberFormatter.formatCurrency(oldTotalTax, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val newTakeHomeF = NumberFormatter.formatCurrency(newTakeHome, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val oldTakeHomeF = NumberFormatter.formatCurrency(oldTakeHome, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)

        return CalculationResult(
            primaryValue = newTaxF,
            primaryLabel = "Tax Payable (New Regime)",
            secondarySummary = recommendation,
            breakdown = listOf(
                "Gross Income" to NumberFormatter.formatCurrency(annualGrossSalary, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle),
                "New Regime Std Deduction" to NumberFormatter.formatCurrency(newStdDeduction, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle),
                "New Regime Taxable Income" to NumberFormatter.formatCurrency(newTaxableIncome, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle),
                "New Regime Total Tax (incl 4% Cess)" to newTaxF,
                "New Regime Net In-Hand" to newTakeHomeF,
                "--- Old Regime Comparison ---" to "",
                "Old Regime Total Deductions" to NumberFormatter.formatCurrency(totalOldDeductions, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle),
                "Old Regime Taxable Income" to NumberFormatter.formatCurrency(oldTaxableIncome, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle),
                "Old Regime Total Tax (incl 4% Cess)" to oldTaxF,
                "Old Regime Net In-Hand" to oldTakeHomeF
            ),
            formulaUsed = "Finance Act 2024 / FY 2024-25 Slabs\nNew: 0-3L 0%, 3-7L 5%, 7-10L 10%, 10-12L 15%, 12-15L 20%, >15L 30%\nOld: 0-2.5L 0%, 2.5-5L 5%, 5-10L 20%, >10L 30% + 4% Cess",
            explanation = "Under the New Tax Regime, you get a higher standard deduction of ₹75,000 without needing investment proofs. If your total eligible deductions under 80C, 80D, and HRA exceed approx ₹3.75 - ₹4.0 Lakhs, the Old Regime may become beneficial.",
            chartData = ChartData.ComparisonBars(
                listOf(
                    BarItem("New Regime Tax", newTotalTax, newTaxF, 0xFF10B981),
                    BarItem("Old Regime Tax", oldTotalTax, oldTaxF, 0xFF3B82F6),
                    BarItem("New Net Income", newTakeHome, newTakeHomeF, 0xFF4338CA),
                    BarItem("Old Net Income", oldTakeHome, oldTakeHomeF, 0xFF06B6D4)
                )
            )
        )
    }

    fun calculateHra(
        basicSalaryAnnual: Double,
        hraReceivedAnnual: Double,
        rentPaidAnnual: Double,
        isMetroCity: Boolean,
        prefs: UserPreferences
    ): CalculationResult {
        val tenPercentBasic = basicSalaryAnnual * 0.10
        val rentMinusTenPercent = (rentPaidAnnual - tenPercentBasic).coerceAtLeast(0.0)
        val salaryPercentage = if (isMetroCity) 0.50 else 0.40
        val percentSalary = basicSalaryAnnual * salaryPercentage

        val exemptHra = minOf(hraReceivedAnnual, rentMinusTenPercent, percentSalary)
        val taxableHra = (hraReceivedAnnual - exemptHra).coerceAtLeast(0.0)

        val exF = NumberFormatter.formatCurrency(exemptHra, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val txF = NumberFormatter.formatCurrency(taxableHra, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)

        return CalculationResult(
            primaryValue = exF,
            primaryLabel = "Exempt HRA Amount",
            secondarySummary = "Taxable portion of HRA: $txF",
            breakdown = listOf(
                "Total HRA Received" to NumberFormatter.formatCurrency(hraReceivedAnnual, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle),
                "1. Actual HRA Received" to NumberFormatter.formatCurrency(hraReceivedAnnual, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle),
                "2. Rent Paid - 10% of Basic" to NumberFormatter.formatCurrency(rentMinusTenPercent, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle),
                "3. ${if (isMetroCity) "50%" else "40%"} of Basic Salary" to NumberFormatter.formatCurrency(percentSalary, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle),
                "Tax Exempt HRA (Minimum of above)" to exF,
                "Taxable HRA" to txF
            ),
            formulaUsed = "Sec 10(13A) Rule 2A: Least of:\n1. Actual HRA received\n2. Rent paid - 10% of Basic Salary\n3. 50% of Basic (Metro) or 40% (Non-Metro)",
            explanation = "The lowest of the three conditions is exempt from income tax under Section 10(13A) of the Income Tax Act."
        )
    }

    fun calculateProfitLoss(
        costPrice: Double,
        sellingPrice: Double
    ): CalculationResult {
        if (costPrice <= 0) {
            return CalculationResult("0", "Profit / Loss", "Enter positive cost price")
        }

        val diff = sellingPrice - costPrice
        val isProfit = diff >= 0
        val percentage = (diff / costPrice) * 100.0
        val margin = if (sellingPrice > 0) (diff / sellingPrice) * 100.0 else 0.0

        val diffF = NumberFormatter.formatNumber(diff, 2)
        val cpF = NumberFormatter.formatNumber(costPrice, 2)
        val spF = NumberFormatter.formatNumber(sellingPrice, 2)

        return CalculationResult(
            primaryValue = if (isProfit) "+$diffF" else diffF,
            primaryLabel = if (isProfit) "Net Profit" else "Net Loss",
            secondarySummary = "${if (isProfit) "Profit" else "Loss"} Margin: ${NumberFormatter.formatPercent(margin, 2)}",
            breakdown = listOf(
                "Cost Price (CP)" to cpF,
                "Selling Price (SP)" to spF,
                "${if (isProfit) "Profit" else "Loss"} Amount" to diffF,
                "Markup on Cost" to NumberFormatter.formatPercent(percentage, 2),
                "Profit Margin on Sales" to NumberFormatter.formatPercent(margin, 2)
            ),
            formulaUsed = "Profit / Loss = SP - CP\nMarkup % = (Profit / CP) × 100\nProfit Margin % = (Profit / SP) × 100",
            explanation = if (isProfit) "Your sales price exceeds cost price by $diffF, generating a healthy margin of ${NumberFormatter.formatPercent(margin, 2)}."
            else "Your sales price is lower than cost price, resulting in a loss of $diffF."
        )
    }

    fun calculateBreakEven(
        fixedCosts: Double,
        pricePerUnit: Double,
        variableCostPerUnit: Double
    ): CalculationResult {
        val contributionMargin = pricePerUnit - variableCostPerUnit
        if (fixedCosts <= 0 || contributionMargin <= 0) {
            return CalculationResult("0", "Break-Even Units", "Selling price must exceed variable cost per unit")
        }

        val breakEvenUnits = fixedCosts / contributionMargin
        val breakEvenRevenue = breakEvenUnits * pricePerUnit
        val cmRatio = (contributionMargin / pricePerUnit) * 100.0

        return CalculationResult(
            primaryValue = "${NumberFormatter.formatNumber(breakEvenUnits, 0)} Units",
            primaryLabel = "Break-Even Sales Volume",
            secondarySummary = "Break-Even Revenue: ${NumberFormatter.formatNumber(breakEvenRevenue, 2)}",
            breakdown = listOf(
                "Break-Even Units" to "${NumberFormatter.formatNumber(breakEvenUnits, 0)} units",
                "Break-Even Revenue" to NumberFormatter.formatNumber(breakEvenRevenue, 2),
                "Contribution Margin/Unit" to NumberFormatter.formatNumber(contributionMargin, 2),
                "Contribution Margin Ratio" to NumberFormatter.formatPercent(cmRatio, 2),
                "Fixed Overhead Costs" to NumberFormatter.formatNumber(fixedCosts, 2)
            ),
            formulaUsed = "Break-Even Units = Fixed Costs / (Selling Price - Variable Cost per Unit)\nBreak-Even Sales = Break-Even Units × Selling Price",
            explanation = "At this sales volume, total business revenue exactly covers both fixed overheads and variable production costs, yielding zero profit and zero loss."
        )
    }
}
