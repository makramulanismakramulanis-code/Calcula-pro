package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserPreferences
import com.example.data.model.CalculationResult
import com.example.data.model.CalculatorId
import com.example.domain.*
import com.example.ui.components.ResultDisplayCard
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorDetailScreen(
    calculatorId: CalculatorId,
    preferences: UserPreferences,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBack: () -> Unit,
    onSaveHistory: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Input States
    var num1 by remember(calculatorId) {
        mutableStateOf(
            when (calculatorId) {
                CalculatorId.EMI_CALCULATOR, CalculatorId.HOME_LOAN -> "1000000"
                CalculatorId.CAR_LOAN -> "800000"
                CalculatorId.PERSONAL_LOAN -> "300000"
                CalculatorId.BIKE_LOAN -> "100000"
                CalculatorId.SIMPLE_INTEREST, CalculatorId.COMPOUND_INTEREST -> "100000"
                CalculatorId.SIP_CALCULATOR -> "5000"
                CalculatorId.LUMPSUM_CALCULATOR, CalculatorId.FD_CALCULATOR -> "100000"
                CalculatorId.RD_CALCULATOR -> "5000"
                CalculatorId.PPF_CALCULATOR -> "150000"
                CalculatorId.INFLATION_CALCULATOR -> "50000"
                CalculatorId.CAGR_CALCULATOR -> "100000"
                CalculatorId.ROI_CALCULATOR -> "50000"
                CalculatorId.GST_CALCULATOR -> "10000"
                CalculatorId.INCOME_TAX_INDIA -> "1200000"
                CalculatorId.HRA_CALCULATOR -> "600000"
                CalculatorId.PROFIT_LOSS -> "500"
                CalculatorId.DISCOUNT_CALCULATOR -> "2000"
                CalculatorId.BREAK_EVEN -> "50000"
                CalculatorId.PERCENTAGE_CALCULATOR -> "20"
                CalculatorId.QUADRATIC_SOLVER -> "1"
                CalculatorId.BMI_CALCULATOR -> "70"
                CalculatorId.BMR_CALCULATOR, CalculatorId.CALORIE_REQUIREMENT -> "70"
                CalculatorId.DAILY_WATER_INTAKE -> "70"
                CalculatorId.IDEAL_WEIGHT -> "175"
                CalculatorId.CONCRETE_CEMENT -> "5"
                CalculatorId.BRICK_CALCULATOR -> "4"
                CalculatorId.TILE_CALCULATOR -> "5"
                CalculatorId.PAINT_CALCULATOR -> "5"
                CalculatorId.STEEL_WEIGHT -> "12"
                CalculatorId.PROBABILITY_STATS -> "10"
                else -> "100"
            }
        )
    }

    var num2 by remember(calculatorId) {
        mutableStateOf(
            when (calculatorId) {
                CalculatorId.EMI_CALCULATOR, CalculatorId.HOME_LOAN -> "8.5"
                CalculatorId.CAR_LOAN -> "9.0"
                CalculatorId.PERSONAL_LOAN -> "12.0"
                CalculatorId.BIKE_LOAN -> "11.0"
                CalculatorId.SIMPLE_INTEREST, CalculatorId.COMPOUND_INTEREST -> "7.5"
                CalculatorId.SIP_CALCULATOR, CalculatorId.LUMPSUM_CALCULATOR -> "12.0"
                CalculatorId.FD_CALCULATOR -> "7.0"
                CalculatorId.RD_CALCULATOR -> "7.1"
                CalculatorId.INFLATION_CALCULATOR -> "6.0"
                CalculatorId.CAGR_CALCULATOR -> "250000"
                CalculatorId.ROI_CALCULATOR -> "75000"
                CalculatorId.GST_CALCULATOR -> "18"
                CalculatorId.INCOME_TAX_INDIA -> "150000" // 80C
                CalculatorId.HRA_CALCULATOR -> "240000" // HRA received
                CalculatorId.PROFIT_LOSS -> "650" // SP
                CalculatorId.DISCOUNT_CALCULATOR -> "15" // %
                CalculatorId.BREAK_EVEN -> "100" // Price per unit
                CalculatorId.PERCENTAGE_CALCULATOR -> "500"
                CalculatorId.QUADRATIC_SOLVER -> "-5"
                CalculatorId.BMI_CALCULATOR, CalculatorId.BMR_CALCULATOR, CalculatorId.CALORIE_REQUIREMENT -> "175" // Height
                CalculatorId.DAILY_WATER_INTAKE -> "30" // Exercise mins
                CalculatorId.CONCRETE_CEMENT -> "4" // Width
                CalculatorId.BRICK_CALCULATOR -> "3" // Height
                CalculatorId.TILE_CALCULATOR -> "4" // Width
                CalculatorId.PAINT_CALCULATOR -> "4" // Width
                CalculatorId.STEEL_WEIGHT -> "12" // Length m
                CalculatorId.PROBABILITY_STATS -> "3" // r
                else -> "10"
            }
        )
    }

    var num3 by remember(calculatorId) {
        mutableStateOf(
            when (calculatorId) {
                CalculatorId.EMI_CALCULATOR, CalculatorId.HOME_LOAN -> "20" // Years
                CalculatorId.CAR_LOAN -> "5" // Years
                CalculatorId.PERSONAL_LOAN, CalculatorId.BIKE_LOAN -> "3" // Years
                CalculatorId.SIMPLE_INTEREST, CalculatorId.COMPOUND_INTEREST, CalculatorId.SIP_CALCULATOR, CalculatorId.LUMPSUM_CALCULATOR -> "5"
                CalculatorId.FD_CALCULATOR -> "3"
                CalculatorId.RD_CALCULATOR -> "36" // Months
                CalculatorId.INFLATION_CALCULATOR -> "10"
                CalculatorId.CAGR_CALCULATOR -> "5"
                CalculatorId.INCOME_TAX_INDIA -> "25000" // 80D
                CalculatorId.HRA_CALCULATOR -> "180000" // Rent paid
                CalculatorId.BREAK_EVEN -> "40" // Variable cost per unit
                CalculatorId.QUADRATIC_SOLVER -> "6"
                CalculatorId.BMR_CALCULATOR, CalculatorId.CALORIE_REQUIREMENT -> "28" // Age
                CalculatorId.CONCRETE_CEMENT -> "0.15" // Depth
                CalculatorId.PAINT_CALCULATOR -> "3" // Height
                else -> "0"
            }
        )
    }

    // Secondary toggles
    var toggleStateA by remember { mutableStateOf(false) } // e.g. Inclusive / Exclusive, Metro / Non-metro, Deg / Rad, Hot climate
    var toggleStateB by remember { mutableStateOf(true) }  // e.g. Show schedule
    var selectedOptionIndex by remember { mutableStateOf(0) } // For dropdown / segment

    val n1 = num1.toDoubleOrNull() ?: 0.0
    val n2 = num2.toDoubleOrNull() ?: 0.0
    val n3 = num3.toDoubleOrNull() ?: 0.0

    // Compute Result dynamically
    var emiSchedule = remember { listOf<AmortizationRow>() }

    val result: CalculationResult = remember(calculatorId, n1, n2, n3, toggleStateA, selectedOptionIndex, preferences) {
        when (calculatorId) {
            CalculatorId.EMI_CALCULATOR, CalculatorId.HOME_LOAN, CalculatorId.CAR_LOAN, CalculatorId.PERSONAL_LOAN, CalculatorId.BIKE_LOAN -> {
                val months = (n3 * 12).toInt().coerceAtLeast(1)
                val (res, sched) = FinanceEngine.calculateEmi(n1, n2, months, preferences)
                emiSchedule = sched
                res
            }
            CalculatorId.SIMPLE_INTEREST -> FinanceEngine.calculateSimpleInterest(n1, n2, n3, preferences)
            CalculatorId.COMPOUND_INTEREST -> {
                val freq = when (selectedOptionIndex) { 0 -> 1; 1 -> 2; 2 -> 4; else -> 12 }
                FinanceEngine.calculateCompoundInterest(n1, n2, n3, freq, preferences)
            }
            CalculatorId.SIP_CALCULATOR -> FinanceEngine.calculateSip(n1, n2, n3, preferences)
            CalculatorId.LUMPSUM_CALCULATOR -> FinanceEngine.calculateLumpsum(n1, n2, n3, preferences)
            CalculatorId.FD_CALCULATOR -> FinanceEngine.calculateFd(n1, n2, n3, toggleStateA, preferences)
            CalculatorId.RD_CALCULATOR -> FinanceEngine.calculateRd(n1, n2, n3.toInt().coerceAtLeast(1), preferences)
            CalculatorId.PPF_CALCULATOR -> FinanceEngine.calculatePpf(n1, 15, 7.1, preferences)
            CalculatorId.INFLATION_CALCULATOR -> FinanceEngine.calculateInflation(n1, n2, n3, preferences)
            CalculatorId.CAGR_CALCULATOR -> FinanceEngine.calculateCagr(n1, n2, n3)
            CalculatorId.ROI_CALCULATOR -> FinanceEngine.calculateRoi(n1, n2)

            // Tax & Business
            CalculatorId.GST_CALCULATOR -> {
                val type = if (toggleStateA) TaxEngine.GstType.INCLUSIVE else TaxEngine.GstType.EXCLUSIVE
                TaxEngine.calculateGst(n1, n2, type, isInterState = false, preferences)
            }
            CalculatorId.INCOME_TAX_INDIA -> FinanceEngineTaxAdapter(n1, n2, n3, preferences)
            CalculatorId.HRA_CALCULATOR -> TaxEngine.calculateHra(n1, n2, n3, isMetroCity = toggleStateA, preferences)
            CalculatorId.PROFIT_LOSS -> TaxEngine.calculateProfitLoss(n1, n2)
            CalculatorId.BREAK_EVEN -> TaxEngine.calculateBreakEven(n1, n2, n3)
            CalculatorId.DISCOUNT_CALCULATOR -> {
                val savings = n1 * (n2 / 100.0)
                val finalPrice = (n1 - savings).coerceAtLeast(0.0)
                CalculationResult(
                    primaryValue = NumberFormatter.formatCurrency(finalPrice, preferences.currencySymbol, preferences.decimalPlaces),
                    primaryLabel = "Final Discounted Price",
                    secondarySummary = "You save: ${NumberFormatter.formatCurrency(savings, preferences.currencySymbol, preferences.decimalPlaces)} ($n2% OFF)",
                    breakdown = listOf(
                        "Original Price" to NumberFormatter.formatCurrency(n1, preferences.currencySymbol),
                        "Discount Rate" to "$n2%",
                        "Total Savings" to NumberFormatter.formatCurrency(savings, preferences.currencySymbol),
                        "Final Price" to NumberFormatter.formatCurrency(finalPrice, preferences.currencySymbol)
                    ),
                    formulaUsed = "Savings = Price × (Discount / 100)\nFinal = Price - Savings"
                )
            }

            // Math
            CalculatorId.PERCENTAGE_CALCULATOR -> MathEngine.calculatePercentage(selectedOptionIndex, n1, n2)
            CalculatorId.QUADRATIC_SOLVER -> MathEngine.calculateQuadratic(n1, n2, n3)
            CalculatorId.PROBABILITY_STATS -> MathEngine.calculatePermutationCombination(n1.toLong(), n2.toLong())

            // Health
            CalculatorId.BMI_CALCULATOR -> HealthEngine.calculateBmi(n1, n2)
            CalculatorId.BMR_CALCULATOR -> {
                val gender = if (toggleStateA) HealthEngine.Gender.FEMALE else HealthEngine.Gender.MALE
                HealthEngine.calculateBmr(gender, n1, n2, n3.toInt().coerceAtLeast(1))
            }
            CalculatorId.CALORIE_REQUIREMENT -> {
                val gender = if (toggleStateA) HealthEngine.Gender.FEMALE else HealthEngine.Gender.MALE
                val act = HealthEngine.ActivityLevel.values().getOrElse(selectedOptionIndex) { HealthEngine.ActivityLevel.MODERATE }
                HealthEngine.calculateDailyCalories(gender, n1, n2, n3.toInt().coerceAtLeast(1), act)
            }
            CalculatorId.DAILY_WATER_INTAKE -> HealthEngine.calculateWaterIntake(n1, n2.toInt(), isHotClimate = toggleStateA)
            CalculatorId.IDEAL_WEIGHT -> {
                val gender = if (toggleStateA) HealthEngine.Gender.FEMALE else HealthEngine.Gender.MALE
                HealthEngine.calculateIdealWeight(gender, n1)
            }

            // Construction
            CalculatorId.CONCRETE_CEMENT -> {
                val grade = when (selectedOptionIndex) {
                    0 -> ConstructionEngine.ConcreteGrade.M15
                    1 -> ConstructionEngine.ConcreteGrade.M20
                    else -> ConstructionEngine.ConcreteGrade.M25
                }
                ConstructionEngine.calculateConcrete(n1, n2, n3, grade)
            }
            CalculatorId.BRICK_CALCULATOR -> ConstructionEngine.calculateBricks(n1, n2, 230.0, 0.0)
            CalculatorId.TILE_CALCULATOR -> ConstructionEngine.calculateTiles(n1, n2, 600.0, 600.0)
            CalculatorId.PAINT_CALCULATOR -> ConstructionEngine.calculatePaint(n1, n2, n3, coats = 2)
            CalculatorId.STEEL_WEIGHT -> ConstructionEngine.calculateSteelWeight(n1, n2)

            else -> CalculationResult("0", "Result", "Ready to calculate")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = calculatorId.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Toggle favorite",
                            tint = if (isFavorite) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .testTag("calculator_detail_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Inputs Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Input Parameters",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Input 1
                        InputFieldWithLabel(
                            label = getInput1Label(calculatorId, preferences),
                            value = num1,
                            onValueChange = { num1 = it }
                        )

                        // Input 2
                        if (hasInput2(calculatorId)) {
                            InputFieldWithLabel(
                                label = getInput2Label(calculatorId),
                                value = num2,
                                onValueChange = { num2 = it }
                            )
                        }

                        // Input 3
                        if (hasInput3(calculatorId)) {
                            InputFieldWithLabel(
                                label = getInput3Label(calculatorId),
                                value = num3,
                                onValueChange = { num3 = it }
                            )
                        }

                        // Calculator-specific switches/toggles
                        RenderSpecializedToggles(
                            calcId = calculatorId,
                            toggleA = toggleStateA,
                            onToggleA = { toggleStateA = it },
                            selectedIndex = selectedOptionIndex,
                            onSelectIndex = { selectedOptionIndex = it }
                        )
                    }
                }
            }

            // Result Display Card
            item {
                ResultDisplayCard(
                    result = result,
                    title = calculatorId.title,
                    onReset = {
                        num1 = "0"
                        num2 = "0"
                        num3 = "0"
                    },
                    onSaveHistory = {
                        val inputs = "${getInput1Label(calculatorId, preferences)}: $num1" +
                                (if (hasInput2(calculatorId)) ", ${getInput2Label(calculatorId)}: $num2" else "")
                        onSaveHistory(
                            calculatorId.title,
                            "${result.primaryLabel}: ${result.primaryValue}",
                            inputs
                        )
                    }
                )
            }

            // Amortization Schedule (for EMI calculators)
            if (emiSchedule.isNotEmpty() && (calculatorId == CalculatorId.EMI_CALCULATOR || calculatorId == CalculatorId.HOME_LOAN || calculatorId == CalculatorId.CAR_LOAN)) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Amortization Schedule (First 12 Months)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                TextButton(onClick = { toggleStateB = !toggleStateB }) {
                                    Text(if (toggleStateB) "Hide" else "Show")
                                }
                            }

                            AnimatedVisibility(visible = toggleStateB) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Mo", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp))
                                        Text("Principal", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                        Text("Interest", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                        Text("Balance", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    }

                                    for (row in emiSchedule.take(12)) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("${row.month}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(28.dp))
                                            Text(NumberFormatter.formatNumber(row.principalPaid, 0), style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                            Text(NumberFormatter.formatNumber(row.interestPaid, 0), style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                            Text(NumberFormatter.formatNumber(row.remainingBalance, 0), style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                        }
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

private fun FinanceEngineTaxAdapter(income: Double, d80c: Double, d80d: Double, prefs: UserPreferences): CalculationResult {
    return TaxEngine.calculateIncomeTaxIndia(
        annualGrossSalary = income,
        deductions80c = d80c,
        deductions80d = d80d,
        hraExemption = 0.0,
        otherDeductions = 0.0,
        prefs = prefs
    )
}

@Composable
fun InputFieldWithLabel(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it.filter { c -> c.isDigit() || c == '.' || c == '-' }) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun RenderSpecializedToggles(
    calcId: CalculatorId,
    toggleA: Boolean,
    onToggleA: (Boolean) -> Unit,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit
) {
    when (calcId) {
        CalculatorId.GST_CALCULATOR -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tax Calculation Mode", style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !toggleA,
                        onClick = { onToggleA(false) },
                        label = { Text("Exclusive") }
                    )
                    FilterChip(
                        selected = toggleA,
                        onClick = { onToggleA(true) },
                        label = { Text("Inclusive") }
                    )
                }
            }
        }
        CalculatorId.PERCENTAGE_CALCULATOR -> {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Select Calculation Type", style = MaterialTheme.typography.labelMedium)
                val modes = listOf("X% of Y", "X is what % of Y", "% Change", "Add X% to Y")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    modes.forEachIndexed { index, title ->
                        FilterChip(
                            selected = selectedIndex == index,
                            onClick = { onSelectIndex(index) },
                            label = { Text(title, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }
        CalculatorId.BMR_CALCULATOR, CalculatorId.CALORIE_REQUIREMENT, CalculatorId.IDEAL_WEIGHT -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Gender", style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !toggleA,
                        onClick = { onToggleA(false) },
                        label = { Text("Male") }
                    )
                    FilterChip(
                        selected = toggleA,
                        onClick = { onToggleA(true) },
                        label = { Text("Female") }
                    )
                }
            }
        }
        CalculatorId.HRA_CALCULATOR -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Metro City (Delhi/Mum/Kol/Chen)?", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = toggleA, onCheckedChange = onToggleA)
            }
        }
        CalculatorId.DAILY_WATER_INTAKE -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tropical / Hot Climate (+500ml)?", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = toggleA, onCheckedChange = onToggleA)
            }
        }
        CalculatorId.COMPOUND_INTEREST -> {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Compounding Frequency", style = MaterialTheme.typography.labelMedium)
                val freqs = listOf("Yearly", "Half-Yearly", "Quarterly", "Monthly")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    freqs.forEachIndexed { index, title ->
                        FilterChip(
                            selected = selectedIndex == index,
                            onClick = { onSelectIndex(index) },
                            label = { Text(title, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }
        CalculatorId.CONCRETE_CEMENT -> {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Mix Grade Ratio", style = MaterialTheme.typography.labelMedium)
                val grades = listOf("M15 (1:2:4)", "M20 (1:1.5:3)", "M25 (1:1:2)")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    grades.forEachIndexed { index, title ->
                        FilterChip(
                            selected = selectedIndex == index,
                            onClick = { onSelectIndex(index) },
                            label = { Text(title, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }
        else -> {}
    }
}

private fun getInput1Label(id: CalculatorId, prefs: UserPreferences): String {
    return when (id) {
        CalculatorId.EMI_CALCULATOR, CalculatorId.HOME_LOAN, CalculatorId.CAR_LOAN, CalculatorId.PERSONAL_LOAN, CalculatorId.BIKE_LOAN,
        CalculatorId.SIMPLE_INTEREST, CalculatorId.COMPOUND_INTEREST -> "Loan / Principal Amount (${prefs.currencySymbol})"
        CalculatorId.SIP_CALCULATOR -> "Monthly SIP Investment (${prefs.currencySymbol})"
        CalculatorId.LUMPSUM_CALCULATOR, CalculatorId.FD_CALCULATOR -> "Deposit / Investment Amount (${prefs.currencySymbol})"
        CalculatorId.RD_CALCULATOR -> "Monthly Recurring Deposit (${prefs.currencySymbol})"
        CalculatorId.PPF_CALCULATOR -> "Annual Deposit Amount (${prefs.currencySymbol})"
        CalculatorId.INFLATION_CALCULATOR -> "Current Price / Expense (${prefs.currencySymbol})"
        CalculatorId.CAGR_CALCULATOR -> "Beginning Value (${prefs.currencySymbol})"
        CalculatorId.ROI_CALCULATOR -> "Amount Invested (${prefs.currencySymbol})"
        CalculatorId.GST_CALCULATOR -> "Amount (${prefs.currencySymbol})"
        CalculatorId.INCOME_TAX_INDIA -> "Annual Gross Income / CTC (${prefs.currencySymbol})"
        CalculatorId.HRA_CALCULATOR -> "Annual Basic Salary (${prefs.currencySymbol})"
        CalculatorId.PROFIT_LOSS -> "Cost Price (CP)"
        CalculatorId.DISCOUNT_CALCULATOR -> "Original Price (${prefs.currencySymbol})"
        CalculatorId.BREAK_EVEN -> "Total Fixed Overhead Costs (${prefs.currencySymbol})"
        CalculatorId.PERCENTAGE_CALCULATOR -> "Percentage Value (X)"
        CalculatorId.QUADRATIC_SOLVER -> "Coefficient a"
        CalculatorId.BMI_CALCULATOR, CalculatorId.BMR_CALCULATOR, CalculatorId.CALORIE_REQUIREMENT, CalculatorId.DAILY_WATER_INTAKE -> "Body Weight (kg)"
        CalculatorId.IDEAL_WEIGHT -> "Height (cm)"
        CalculatorId.CONCRETE_CEMENT, CalculatorId.TILE_CALCULATOR, CalculatorId.PAINT_CALCULATOR -> "Length (Meters)"
        CalculatorId.BRICK_CALCULATOR -> "Wall Length (Meters)"
        CalculatorId.STEEL_WEIGHT -> "TMT Rebar Diameter (mm)"
        CalculatorId.PROBABILITY_STATS -> "Total Items (n)"
        else -> "Parameter 1"
    }
}

private fun getInput2Label(id: CalculatorId): String {
    return when (id) {
        CalculatorId.EMI_CALCULATOR, CalculatorId.HOME_LOAN, CalculatorId.CAR_LOAN, CalculatorId.PERSONAL_LOAN, CalculatorId.BIKE_LOAN,
        CalculatorId.SIMPLE_INTEREST, CalculatorId.COMPOUND_INTEREST, CalculatorId.SIP_CALCULATOR, CalculatorId.LUMPSUM_CALCULATOR,
        CalculatorId.FD_CALCULATOR, CalculatorId.RD_CALCULATOR -> "Annual Interest Rate (%)"
        CalculatorId.INFLATION_CALCULATOR -> "Annual Inflation Rate (%)"
        CalculatorId.CAGR_CALCULATOR -> "Ending / Realized Value"
        CalculatorId.ROI_CALCULATOR -> "Amount Returned / Value"
        CalculatorId.GST_CALCULATOR -> "GST Rate (%)"
        CalculatorId.INCOME_TAX_INDIA -> "Section 80C Deductions (Max 1.5L)"
        CalculatorId.HRA_CALCULATOR -> "Annual HRA Received"
        CalculatorId.PROFIT_LOSS -> "Selling Price (SP)"
        CalculatorId.DISCOUNT_CALCULATOR -> "Discount Percentage (%)"
        CalculatorId.BREAK_EVEN -> "Selling Price per Unit"
        CalculatorId.PERCENTAGE_CALCULATOR -> "Base Number (Y)"
        CalculatorId.QUADRATIC_SOLVER -> "Coefficient b"
        CalculatorId.BMI_CALCULATOR, CalculatorId.BMR_CALCULATOR, CalculatorId.CALORIE_REQUIREMENT -> "Height (cm)"
        CalculatorId.DAILY_WATER_INTAKE -> "Daily Workout Duration (Minutes)"
        CalculatorId.CONCRETE_CEMENT, CalculatorId.TILE_CALCULATOR, CalculatorId.PAINT_CALCULATOR -> "Width (Meters)"
        CalculatorId.BRICK_CALCULATOR -> "Wall Height (Meters)"
        CalculatorId.STEEL_WEIGHT -> "Total Length (Meters)"
        CalculatorId.PROBABILITY_STATS -> "Selections (r)"
        else -> "Parameter 2"
    }
}

private fun getInput3Label(id: CalculatorId): String {
    return when (id) {
        CalculatorId.EMI_CALCULATOR, CalculatorId.HOME_LOAN, CalculatorId.CAR_LOAN, CalculatorId.PERSONAL_LOAN, CalculatorId.BIKE_LOAN,
        CalculatorId.SIMPLE_INTEREST, CalculatorId.COMPOUND_INTEREST, CalculatorId.SIP_CALCULATOR, CalculatorId.LUMPSUM_CALCULATOR,
        CalculatorId.FD_CALCULATOR, CalculatorId.INFLATION_CALCULATOR, CalculatorId.CAGR_CALCULATOR -> "Duration / Tenure (Years)"
        CalculatorId.RD_CALCULATOR -> "Tenure (Total Months)"
        CalculatorId.INCOME_TAX_INDIA -> "Section 80D Health Insurance"
        CalculatorId.HRA_CALCULATOR -> "Annual Actual Rent Paid"
        CalculatorId.BREAK_EVEN -> "Variable Production Cost per Unit"
        CalculatorId.QUADRATIC_SOLVER -> "Coefficient c"
        CalculatorId.BMR_CALCULATOR, CalculatorId.CALORIE_REQUIREMENT -> "Age (Years)"
        CalculatorId.CONCRETE_CEMENT -> "Depth / Thickness (Meters)"
        CalculatorId.PAINT_CALCULATOR -> "Wall Height (Meters)"
        else -> "Parameter 3"
    }
}

private fun hasInput2(id: CalculatorId): Boolean {
    return id != CalculatorId.PPF_CALCULATOR && id != CalculatorId.IDEAL_WEIGHT
}

private fun hasInput3(id: CalculatorId): Boolean {
    return id != CalculatorId.PPF_CALCULATOR &&
            id != CalculatorId.IDEAL_WEIGHT &&
            id != CalculatorId.GST_CALCULATOR &&
            id != CalculatorId.PROFIT_LOSS &&
            id != CalculatorId.DISCOUNT_CALCULATOR &&
            id != CalculatorId.PERCENTAGE_CALCULATOR &&
            id != CalculatorId.BMI_CALCULATOR &&
            id != CalculatorId.DAILY_WATER_INTAKE &&
            id != CalculatorId.BRICK_CALCULATOR &&
            id != CalculatorId.TILE_CALCULATOR &&
            id != CalculatorId.STEEL_WEIGHT &&
            id != CalculatorId.PROBABILITY_STATS &&
            id != CalculatorId.ROI_CALCULATOR
}
