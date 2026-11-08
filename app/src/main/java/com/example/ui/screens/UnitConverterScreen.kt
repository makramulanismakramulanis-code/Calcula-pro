package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalculationResult
import com.example.domain.ConverterEngine
import com.example.domain.UnitCategory
import com.example.domain.UnitDefinition
import com.example.ui.components.ResultDisplayCard
import com.example.ui.theme.AccentCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(
    initialCategory: UnitCategory = UnitCategory.MASS_WEIGHT,
    onBack: () -> Unit,
    onSaveHistory: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var inputValueStr by remember { mutableStateOf("1") }

    // Units for current category
    val currentUnits = remember(selectedCategory) {
        ConverterEngine.getUnitsForCategory(selectedCategory)
    }

    // Selected From and To units
    var fromIndex by remember(selectedCategory) { mutableStateOf(0) }
    var toIndex by remember(selectedCategory) {
        mutableStateOf(if (currentUnits.size > 1) 1 else 0)
    }

    // Temperature scale states
    var fromTempScale by remember { mutableStateOf("C") }
    var toTempScale by remember { mutableStateOf("F") }

    val result: CalculationResult = remember(selectedCategory, inputValueStr, fromIndex, toIndex, fromTempScale, toTempScale) {
        val inputVal = inputValueStr.toDoubleOrNull() ?: 0.0
        if (selectedCategory == UnitCategory.TEMPERATURE) {
            ConverterEngine.convertTemperature(inputVal, fromTempScale, toTempScale)
        } else if (currentUnits.isNotEmpty()) {
            val fromUnit = currentUnits.getOrElse(fromIndex) { currentUnits[0] }
            val toUnit = currentUnits.getOrElse(toIndex) { currentUnits[0] }
            ConverterEngine.convertStandard(inputVal, fromUnit, toUnit)
        } else {
            CalculationResult("0", "Converter", "Select units")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedCategory.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .testTag("unit_converter_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category selector scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (cat in UnitCategory.values()) {
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = {
                            selectedCategory = cat
                            fromIndex = 0
                            toIndex = 1
                        },
                        label = { Text(cat.title) }
                    )
                }
            }

            // Input Value Field
            OutlinedTextField(
                value = inputValueStr,
                onValueChange = { inputValueStr = it.filter { c -> c.isDigit() || c == '.' || c == '-' } },
                label = { Text("Input Value") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("converter_value_input"),
                shape = RoundedCornerShape(14.dp),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Result", result.primaryValue)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Result copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy result")
                    }
                }
            )

            // From / To Selector with Swap Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // From unit selector
                Box(modifier = Modifier.weight(1f)) {
                    if (selectedCategory == UnitCategory.TEMPERATURE) {
                        TempScaleDropdown("From Scale", fromTempScale) { fromTempScale = it }
                    } else if (currentUnits.isNotEmpty()) {
                        UnitDropdown(
                            label = "From Unit",
                            units = currentUnits,
                            selectedIndex = fromIndex,
                            onSelect = { fromIndex = it }
                        )
                    }
                }

                // Swap Button
                IconButton(
                    onClick = {
                        if (selectedCategory == UnitCategory.TEMPERATURE) {
                            val temp = fromTempScale
                            fromTempScale = toTempScale
                            toTempScale = temp
                        } else {
                            val temp = fromIndex
                            fromIndex = toIndex
                            toIndex = temp
                        }
                    },
                    modifier = Modifier.testTag("swap_units_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Swap units",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // To unit selector
                Box(modifier = Modifier.weight(1f)) {
                    if (selectedCategory == UnitCategory.TEMPERATURE) {
                        TempScaleDropdown("To Scale", toTempScale) { toTempScale = it }
                    } else if (currentUnits.isNotEmpty()) {
                        UnitDropdown(
                            label = "To Unit",
                            units = currentUnits,
                            selectedIndex = toIndex,
                            onSelect = { toIndex = it }
                        )
                    }
                }
            }

            // Converted Result Display
            ResultDisplayCard(
                result = result,
                title = selectedCategory.title,
                onReset = { inputValueStr = "1" },
                onSaveHistory = {
                    onSaveHistory(
                        selectedCategory.title,
                        "${result.primaryLabel}: ${result.primaryValue}",
                        "Input: $inputValueStr"
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdown(
    label: String,
    units: List<UnitDefinition>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedUnit = units.getOrElse(selectedIndex) { units[0] }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = "${selectedUnit.symbol} (${selectedUnit.name})",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            shape = RoundedCornerShape(14.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            units.forEachIndexed { index, u ->
                DropdownMenuItem(
                    text = { Text("${u.symbol} — ${u.name}") },
                    onClick = {
                        onSelect(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TempScaleDropdown(
    label: String,
    selectedScale: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val scales = listOf(
        "C" to "Celsius (°C)",
        "F" to "Fahrenheit (°F)",
        "K" to "Kelvin (K)"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = scales.find { it.first == selectedScale }?.second ?: selectedScale,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            shape = RoundedCornerShape(14.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            scales.forEach { (code, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onSelect(code)
                        expanded = false
                    }
                )
            }
        }
    }
}
