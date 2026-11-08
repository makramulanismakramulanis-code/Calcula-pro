package com.example.domain

import com.example.data.local.UserPreferences
import com.example.data.model.CalculationResult
import com.example.data.model.ChartData
import com.example.data.model.ChartSegment
import com.example.data.model.BarItem
import kotlin.math.ln
import kotlin.math.pow

data class AmortizationRow(
    val month: Int,
    val emi: Double,
    val principalPaid: Double,
    val interestPaid: Double,
    val remainingBalance: Double
)

object FinanceEngine {

    fun calculateEmi(
        principal: Double,
        annualRatePercent: Double,
        tenureMonths: Int,
        prefs: UserPreferences
    ): Pair<CalculationResult, List<AmortizationRow>> {
        if (principal <= 0 || tenureMonths <= 0) {
            return Pair(
                CalculationResult("0", "Monthly EMI", "Enter valid positive loan amount and tenure"),
                emptyList()
            )
        }

        val monthlyRate = (annualRatePercent / 12.0) / 100.0
        val emi = if (monthlyRate > 0) {
            val factor = (1.0 + monthlyRate).pow(tenureMonths.toDouble())
            (principal * monthlyRate * factor) / (factor - 1.0)
        } else {
            principal / tenureMonths
        }

        val totalPayment = emi * tenureMonths
        val totalInterest = (totalPayment - principal).coerceAtLeast(0.0)

        // Generate amortization schedule
        var balance = principal
        val schedule = ArrayList<AmortizationRow>(tenureMonths)
        for (m in 1..tenureMonths) {
            val interestPart = if (monthlyRate > 0) balance * monthlyRate else 0.0
            val principalPart = (emi - interestPart).coerceAtMost(balance)
            balance = (balance - principalPart).coerceAtLeast(0.0)
            schedule.add(
                AmortizationRow(
                    month = m,
                    emi = emi,
                    principalPaid = principalPart,
                    interestPaid = interestPart,
                    remainingBalance = balance
                )
            )
        }

        val principalFormatted = NumberFormatter.formatCurrency(principal, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val emiFormatted = NumberFormatter.formatCurrency(emi, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val interestFormatted = NumberFormatter.formatCurrency(totalInterest, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val totalFormatted = NumberFormatter.formatCurrency(totalPayment, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)

        val principalPercent = if (totalPayment > 0) (principal / totalPayment) * 100 else 100.0
        val interestPercent = if (totalPayment > 0) (totalInterest / totalPayment) * 100 else 0.0

        val result = CalculationResult(
            primaryValue = emiFormatted,
            primaryLabel = "Monthly EMI",
            secondarySummary = "Total interest payable is $interestFormatted (${NumberFormatter.formatPercent(interestPercent)})",
            breakdown = listOf(
                "Principal Loan Amount" to principalFormatted,
                "Total Interest Payable" to interestFormatted,
                "Total Amount Payable" to totalFormatted,
                "Loan Tenure" to "$tenureMonths Months (${tenureMonths / 12} yrs ${tenureMonths % 12} mos)",
                "Annual Interest Rate" to "${NumberFormatter.formatNumber(annualRatePercent, 2)}% p.a."
            ),
            formulaUsed = "EMI = [P × r × (1 + r)^n] / [(1 + r)^n - 1]\nwhere P = Principal, r = Monthly Rate (Annual% / 12 / 100), n = Tenure in Months",
            explanation = "Your monthly payment of $emiFormatted consists of both principal repayment and interest charges. In the initial months, interest constitutes a larger share of the EMI, gradually shifting towards principal repayment as the balance reduces.",
            chartData = ChartData.DonutOrPie(
                listOf(
                    ChartSegment("Principal", principal, 0xFF4338CA), // Indigo
                    ChartSegment("Total Interest", totalInterest, 0xFF06B6D4) // Cyan
                )
            )
        )

        return Pair(result, schedule)
    }

    fun calculateSimpleInterest(
        principal: Double,
        ratePercent: Double,
        timeYears: Double,
        prefs: UserPreferences
    ): CalculationResult {
        if (principal <= 0 || timeYears <= 0) {
            return CalculationResult("0", "Total Interest", "Enter valid positive principal and tenure")
        }

        val interest = (principal * ratePercent * timeYears) / 100.0
        val totalAmount = principal + interest

        val pF = NumberFormatter.formatCurrency(principal, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val iF = NumberFormatter.formatCurrency(interest, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val tF = NumberFormatter.formatCurrency(totalAmount, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)

        return CalculationResult(
            primaryValue = iF,
            primaryLabel = "Total Simple Interest",
            secondarySummary = "Total maturity amount is $tF",
            breakdown = listOf(
                "Principal Amount" to pF,
                "Simple Interest Earned" to iF,
                "Total Maturity Value" to tF,
                "Interest Rate" to "${NumberFormatter.formatNumber(ratePercent, 2)}% per year",
                "Duration" to "${NumberFormatter.formatNumber(timeYears, 1)} Years"
            ),
            formulaUsed = "SI = (Principal × Rate × Time) / 100\nTotal Amount = Principal + SI",
            explanation = "Simple interest is calculated exclusively on the original principal sum. The interest earned remains constant every year.",
            chartData = ChartData.DonutOrPie(
                listOf(
                    ChartSegment("Principal", principal, 0xFF4338CA),
                    ChartSegment("Interest Earned", interest, 0xFF10B981)
                )
            )
        )
    }

    fun calculateCompoundInterest(
        principal: Double,
        ratePercent: Double,
        timeYears: Double,
        compoundingsPerYear: Int, // 1 = yearly, 2 = semi-annually, 4 = quarterly, 12 = monthly
        prefs: UserPreferences
    ): CalculationResult {
        if (principal <= 0 || timeYears <= 0) {
            return CalculationResult("0", "Maturity Amount", "Enter valid positive values")
        }

        val r = ratePercent / 100.0
        val n = compoundingsPerYear.coerceAtLeast(1).toDouble()
        val totalAmount = principal * (1.0 + (r / n)).pow(n * timeYears)
        val compoundInterest = totalAmount - principal

        val pF = NumberFormatter.formatCurrency(principal, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val iF = NumberFormatter.formatCurrency(compoundInterest, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val tF = NumberFormatter.formatCurrency(totalAmount, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)

        val freqName = when (compoundingsPerYear) {
            1 -> "Annually"
            2 -> "Semi-Annually"
            4 -> "Quarterly"
            12 -> "Monthly"
            else -> "$compoundingsPerYear times/yr"
        }

        return CalculationResult(
            primaryValue = tF,
            primaryLabel = "Maturity Value",
            secondarySummary = "Total interest accrued is $iF",
            breakdown = listOf(
                "Principal Deposited" to pF,
                "Total Compound Interest" to iF,
                "Total Accumulated Wealth" to tF,
                "Compounding Frequency" to freqName,
                "Annual Rate" to "${NumberFormatter.formatNumber(ratePercent, 2)}%",
                "Time Period" to "${NumberFormatter.formatNumber(timeYears, 1)} Years"
            ),
            formulaUsed = "A = P × (1 + r/n)^(n×t)\nwhere P = Principal, r = annual interest rate, n = compoundings per year, t = years",
            explanation = "With compound interest, interest earned in earlier periods is reinvested to generate additional interest in subsequent periods, creating exponential exponential growth.",
            chartData = ChartData.DonutOrPie(
                listOf(
                    ChartSegment("Principal", principal, 0xFF4338CA),
                    ChartSegment("Compound Interest", compoundInterest, 0xFF06B6D4)
                )
            )
        )
    }

    fun calculateSip(
        monthlyInvestment: Double,
        expectedReturnRate: Double,
        timeYears: Double,
        prefs: UserPreferences
    ): CalculationResult {
        if (monthlyInvestment <= 0 || timeYears <= 0) {
            return CalculationResult("0", "Expected Total Value", "Enter valid monthly investment and duration")
        }

        val totalMonths = (timeYears * 12).toInt()
        val monthlyRate = (expectedReturnRate / 12.0) / 100.0

        val maturityValue = if (monthlyRate > 0) {
            monthlyInvestment * (((1.0 + monthlyRate).pow(totalMonths.toDouble()) - 1.0) / monthlyRate) * (1.0 + monthlyRate)
        } else {
            monthlyInvestment * totalMonths
        }

        val totalInvested = monthlyInvestment * totalMonths
        val estimatedReturns = (maturityValue - totalInvested).coerceAtLeast(0.0)

        val invF = NumberFormatter.formatCurrency(totalInvested, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val retF = NumberFormatter.formatCurrency(estimatedReturns, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val totF = NumberFormatter.formatCurrency(maturityValue, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)

        return CalculationResult(
            primaryValue = totF,
            primaryLabel = "Estimated Total Value",
            secondarySummary = "Invested: $invF | Wealth Gained: $retF",
            breakdown = listOf(
                "Invested Amount" to invF,
                "Est. Wealth Gain" to retF,
                "Total Corpus Value" to totF,
                "Monthly SIP" to NumberFormatter.formatCurrency(monthlyInvestment, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle),
                "Expected Annual Return" to "${NumberFormatter.formatNumber(expectedReturnRate, 2)}% p.a.",
                "Investment Period" to "${NumberFormatter.formatNumber(timeYears, 1)} Years ($totalMonths months)"
            ),
            formulaUsed = "M = P × [((1 + i)^n - 1) / i] × (1 + i)\nwhere P = Monthly SIP, i = Monthly Rate (Annual% / 12 / 100), n = Total Months",
            explanation = "SIP harnesses rupee-cost averaging and compounding. Regular monthly investments allow you to purchase more units when markets are down and fewer when markets are up, smoothing out market volatility.",
            chartData = ChartData.DonutOrPie(
                listOf(
                    ChartSegment("Total Invested", totalInvested, 0xFF4338CA),
                    ChartSegment("Estimated Return", estimatedReturns, 0xFF10B981)
                )
            )
        )
    }

    fun calculateLumpsum(
        investment: Double,
        annualReturnRate: Double,
        timeYears: Double,
        prefs: UserPreferences
    ): CalculationResult {
        if (investment <= 0 || timeYears <= 0) {
            return CalculationResult("0", "Maturity Value", "Enter valid investment amount and time period")
        }

        val totalValue = investment * (1.0 + (annualReturnRate / 100.0)).pow(timeYears)
        val totalGain = totalValue - investment

        val invF = NumberFormatter.formatCurrency(investment, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val gainF = NumberFormatter.formatCurrency(totalGain, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val totF = NumberFormatter.formatCurrency(totalValue, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)

        return CalculationResult(
            primaryValue = totF,
            primaryLabel = "Estimated Final Value",
            secondarySummary = "Total wealth gain is $gainF",
            breakdown = listOf(
                "Invested Amount" to invF,
                "Est. Wealth Gain" to gainF,
                "Total Maturity Value" to totF,
                "Expected CAGR" to "${NumberFormatter.formatNumber(annualReturnRate, 2)}% p.a.",
                "Investment Horizon" to "${NumberFormatter.formatNumber(timeYears, 1)} Years"
            ),
            formulaUsed = "Total Value = Principal × (1 + r)^t\nwhere r = Expected annual growth rate, t = Time in years",
            explanation = "A one-time lump sum investment grows uninterrupted, allowing compound interest to compound across the full investment duration.",
            chartData = ChartData.DonutOrPie(
                listOf(
                    ChartSegment("Initial Investment", investment, 0xFF4338CA),
                    ChartSegment("Estimated Gains", totalGain, 0xFF3B82F6)
                )
            )
        )
    }

    fun calculateFd(
        depositAmount: Double,
        interestRatePercent: Double,
        timeYears: Double,
        compoundingQuarterly: Boolean,
        prefs: UserPreferences
    ): CalculationResult {
        val freq = if (compoundingQuarterly) 4 else 1
        return calculateCompoundInterest(depositAmount, interestRatePercent, timeYears, freq, prefs)
    }

    fun calculateRd(
        monthlyDeposit: Double,
        annualRate: Double,
        tenureMonths: Int,
        prefs: UserPreferences
    ): CalculationResult {
        if (monthlyDeposit <= 0 || tenureMonths <= 0) {
            return CalculationResult("0", "Maturity Amount", "Enter valid monthly deposit and tenure")
        }

        // Indian Banking RD quarterly compounding formula:
        // A = P * (1+r/400)^n + P * (1+r/400)^(n-1) ...
        var totalMaturity = 0.0
        val r = annualRate / 400.0
        for (i in 1..tenureMonths) {
            val quarters = (tenureMonths - i + 1) / 3.0
            totalMaturity += monthlyDeposit * (1.0 + r).pow(quarters)
        }

        val totalInvested = monthlyDeposit * tenureMonths
        val interestEarned = totalMaturity - totalInvested

        val depF = NumberFormatter.formatCurrency(totalInvested, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val intF = NumberFormatter.formatCurrency(interestEarned, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val matF = NumberFormatter.formatCurrency(totalMaturity, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)

        return CalculationResult(
            primaryValue = matF,
            primaryLabel = "Maturity Value",
            secondarySummary = "Total interest earned: $intF",
            breakdown = listOf(
                "Total Amount Deposited" to depF,
                "Total Interest Earned" to intF,
                "Maturity Proceeds" to matF,
                "Monthly Deposit" to NumberFormatter.formatCurrency(monthlyDeposit, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle),
                "Interest Rate" to "${NumberFormatter.formatNumber(annualRate, 2)}% p.a. (Quarterly compounded)",
                "Tenure" to "$tenureMonths Months (${tenureMonths / 12} yrs)"
            ),
            formulaUsed = "Standard Banking Recurring Deposit quarterly compounding schedule",
            explanation = "Each installment earns interest compounded quarterly for the duration it remains with the bank until the final maturity date.",
            chartData = ChartData.DonutOrPie(
                listOf(
                    ChartSegment("Total Deposit", totalInvested, 0xFF4338CA),
                    ChartSegment("Interest Earned", interestEarned, 0xFF06B6D4)
                )
            )
        )
    }

    fun calculatePpf(
        annualInvestment: Double,
        timeYears: Int = 15,
        currentPpfRate: Double = 7.1,
        prefs: UserPreferences
    ): CalculationResult {
        val years = timeYears.coerceAtLeast(1)
        val rate = currentPpfRate / 100.0

        var balance = 0.0
        var totalDeposited = 0.0

        for (y in 1..years) {
            totalDeposited += annualInvestment
            balance = (balance + annualInvestment) * (1.0 + rate)
        }

        val totalInterest = balance - totalDeposited
        val depF = NumberFormatter.formatCurrency(totalDeposited, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val intF = NumberFormatter.formatCurrency(totalInterest, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val matF = NumberFormatter.formatCurrency(balance, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)

        return CalculationResult(
            primaryValue = matF,
            primaryLabel = "PPF Maturity Value",
            secondarySummary = "Triple tax exempt (EEE): Interest $intF",
            breakdown = listOf(
                "Total Amount Deposited" to depF,
                "Total Interest Earned" to intF,
                "Maturity Value" to matF,
                "Annual Deposit" to NumberFormatter.formatCurrency(annualInvestment, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle),
                "Govt. Interest Rate" to "$currentPpfRate% p.a. (Annual compounding)",
                "Lock-in Period" to "$years Years (Extendable in 5-yr blocks)"
            ),
            formulaUsed = "PPF Annual compounding: Balance = (Previous Balance + Annual Deposit) × (1 + 7.1%)",
            explanation = "Public Provident Fund offers sovereign security with EEE (Exempt-Exempt-Exempt) tax status: investment is tax-deductible under Section 80C, interest earned is tax-free, and maturity corpus is completely tax-free.",
            chartData = ChartData.DonutOrPie(
                listOf(
                    ChartSegment("Deposits", totalDeposited, 0xFF4338CA),
                    ChartSegment("Tax-Free Interest", totalInterest, 0xFF10B981)
                )
            )
        )
    }

    fun calculateInflation(
        currentAmount: Double,
        inflationRatePercent: Double,
        years: Double,
        prefs: UserPreferences
    ): CalculationResult {
        if (currentAmount <= 0 || years <= 0) {
            return CalculationResult("0", "Future Cost", "Enter valid amount and years")
        }

        val i = inflationRatePercent / 100.0
        val futureCost = currentAmount * (1.0 + i).pow(years)
        val purchasingPower = currentAmount / (1.0 + i).pow(years)

        val curF = NumberFormatter.formatCurrency(currentAmount, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val futF = NumberFormatter.formatCurrency(futureCost, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)
        val purF = NumberFormatter.formatCurrency(purchasingPower, prefs.currencySymbol, prefs.decimalPlaces, prefs.numberFormatStyle)

        return CalculationResult(
            primaryValue = futF,
            primaryLabel = "Future Cost of Living",
            secondarySummary = "Today's $curF will only buy what $purF buys in ${NumberFormatter.formatNumber(years, 0)} years",
            breakdown = listOf(
                "Current Value" to curF,
                "Future Required Amount" to futF,
                "Future Purchasing Power" to purF,
                "Inflation Rate" to "${NumberFormatter.formatNumber(inflationRatePercent, 1)}% annually",
                "Time Horizon" to "${NumberFormatter.formatNumber(years, 0)} Years"
            ),
            formulaUsed = "Future Cost = Present Cost × (1 + i)^n\nPurchasing Power = Present Cost / (1 + i)^n",
            explanation = "Inflation erodes the purchasing power of money over time. To purchase the same basket of goods in the future, you will require $futF.",
            chartData = ChartData.ComparisonBars(
                listOf(
                    BarItem("Present Cost", currentAmount, curF, 0xFF3B82F6),
                    BarItem("Future Cost", futureCost, futF, 0xFFEF4444)
                )
            )
        )
    }

    fun calculateCagr(
        beginningValue: Double,
        endingValue: Double,
        years: Double
    ): CalculationResult {
        if (beginningValue <= 0 || endingValue <= 0 || years <= 0) {
            return CalculationResult("0.00%", "CAGR", "Enter positive values for start, end and years")
        }

        val cagr = ((endingValue / beginningValue).pow(1.0 / years) - 1.0) * 100.0
        val absoluteGain = endingValue - beginningValue
        val absoluteReturn = (absoluteGain / beginningValue) * 100.0

        return CalculationResult(
            primaryValue = NumberFormatter.formatPercent(cagr, 2),
            primaryLabel = "Compound Annual Growth Rate",
            secondarySummary = "Absolute return is ${NumberFormatter.formatPercent(absoluteReturn, 2)}",
            breakdown = listOf(
                "CAGR (Annualized)" to NumberFormatter.formatPercent(cagr, 2),
                "Absolute Return" to NumberFormatter.formatPercent(absoluteReturn, 2),
                "Beginning Value" to NumberFormatter.formatNumber(beginningValue, 2),
                "Ending Value" to NumberFormatter.formatNumber(endingValue, 2),
                "Duration" to "${NumberFormatter.formatNumber(years, 1)} Years"
            ),
            formulaUsed = "CAGR = (Ending Value / Beginning Value)^(1 / Years) - 1",
            explanation = "CAGR provides a smoothed annual return rate that discounts year-over-year market volatility and compounding fluctuations."
        )
    }

    fun calculateRoi(
        amountInvested: Double,
        amountReturned: Double
    ): CalculationResult {
        if (amountInvested <= 0) {
            return CalculationResult("0.00%", "ROI", "Enter positive investment amount")
        }

        val netProfit = amountReturned - amountInvested
        val roi = (netProfit / amountInvested) * 100.0

        return CalculationResult(
            primaryValue = NumberFormatter.formatPercent(roi, 2),
            primaryLabel = "Return on Investment (ROI)",
            secondarySummary = "Net Profit/Gain: ${NumberFormatter.formatNumber(netProfit, 2)}",
            breakdown = listOf(
                "ROI Percentage" to NumberFormatter.formatPercent(roi, 2),
                "Net Profit / Loss" to NumberFormatter.formatNumber(netProfit, 2),
                "Total Invested" to NumberFormatter.formatNumber(amountInvested, 2),
                "Total Realized Value" to NumberFormatter.formatNumber(amountReturned, 2)
            ),
            formulaUsed = "ROI = [(Gain from Investment - Cost of Investment) / Cost of Investment] × 100",
            explanation = "Return on Investment measures the efficiency and profitability of an investment relative to its initial cost."
        )
    }
}
