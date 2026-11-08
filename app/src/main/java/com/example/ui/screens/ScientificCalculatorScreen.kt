package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.MathEngine
import com.example.domain.NumberFormatter
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScientificCalculatorScreen(
    onBack: () -> Unit,
    onSaveHistory: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expression by remember { mutableStateOf("") }
    var evaluatedResult by remember { mutableStateOf("0") }
    var isRadians by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showExtraFunctions by remember { mutableStateOf(true) }

    fun evaluateCurrent() {
        if (expression.isBlank()) {
            evaluatedResult = "0"
            errorMessage = null
            return
        }
        try {
            val res = MathEngine.evaluateScientificExpression(expression, isRadians)
            evaluatedResult = NumberFormatter.formatNumber(res, 6)
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = e.message ?: "Invalid Syntax"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scientific Calculator", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = { isRadians = !isRadians; evaluateCurrent() },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(if (isRadians) "RAD" else "DEG", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("scientific_screen"),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Display Area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.32f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isRadians) "Radians (RAD)" else "Degrees (DEG)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    // Expression input
                    Text(
                        text = if (expression.isEmpty()) "0" else expression,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        textAlign = TextAlign.End,
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    )

                    // Evaluated Result
                    Text(
                        text = "= $evaluatedResult",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = AccentCyan
                        ),
                        maxLines = 1,
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Keypad Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.68f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Row 1: Sci Functions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SciKey("sin", Modifier.weight(1f)) { expression += "sin("; evaluateCurrent() }
                    SciKey("cos", Modifier.weight(1f)) { expression += "cos("; evaluateCurrent() }
                    SciKey("tan", Modifier.weight(1f)) { expression += "tan("; evaluateCurrent() }
                    SciKey("log", Modifier.weight(1f)) { expression += "log("; evaluateCurrent() }
                    SciKey("ln", Modifier.weight(1f)) { expression += "ln("; evaluateCurrent() }
                }

                // Row 2: Secondary Functions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SciKey("√", Modifier.weight(1f)) { expression += "sqrt("; evaluateCurrent() }
                    SciKey("^", Modifier.weight(1f)) { expression += "^"; evaluateCurrent() }
                    SciKey("π", Modifier.weight(1f)) { expression += "π"; evaluateCurrent() }
                    SciKey("e", Modifier.weight(1f)) { expression += "e"; evaluateCurrent() }
                    SciKey("(", Modifier.weight(1f)) { expression += "("; evaluateCurrent() }
                    SciKey(")", Modifier.weight(1f)) { expression += ")"; evaluateCurrent() }
                }

                // Row 3: AC, DEL, %, ÷
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NumKey("AC", Modifier.weight(1f), isAction = true, color = MaterialTheme.colorScheme.error) {
                        expression = ""
                        evaluatedResult = "0"
                        errorMessage = null
                    }
                    NumKey("DEL", Modifier.weight(1f), isAction = true) {
                        if (expression.isNotEmpty()) {
                            expression = expression.dropLast(1)
                            evaluateCurrent()
                        }
                    }
                    NumKey("%", Modifier.weight(1f), isAction = true) { expression += "%"; evaluateCurrent() }
                    NumKey("÷", Modifier.weight(1f), isAction = true, color = SecondaryBlue) { expression += "÷"; evaluateCurrent() }
                }

                // Row 4: 7, 8, 9, ×
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NumKey("7", Modifier.weight(1f)) { expression += "7"; evaluateCurrent() }
                    NumKey("8", Modifier.weight(1f)) { expression += "8"; evaluateCurrent() }
                    NumKey("9", Modifier.weight(1f)) { expression += "9"; evaluateCurrent() }
                    NumKey("×", Modifier.weight(1f), isAction = true, color = SecondaryBlue) { expression += "×"; evaluateCurrent() }
                }

                // Row 5: 4, 5, 6, -
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NumKey("4", Modifier.weight(1f)) { expression += "4"; evaluateCurrent() }
                    NumKey("5", Modifier.weight(1f)) { expression += "5"; evaluateCurrent() }
                    NumKey("6", Modifier.weight(1f)) { expression += "6"; evaluateCurrent() }
                    NumKey("-", Modifier.weight(1f), isAction = true, color = SecondaryBlue) { expression += "-"; evaluateCurrent() }
                }

                // Row 6: 1, 2, 3, +
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NumKey("1", Modifier.weight(1f)) { expression += "1"; evaluateCurrent() }
                    NumKey("2", Modifier.weight(1f)) { expression += "2"; evaluateCurrent() }
                    NumKey("3", Modifier.weight(1f)) { expression += "3"; evaluateCurrent() }
                    NumKey("+", Modifier.weight(1f), isAction = true, color = SecondaryBlue) { expression += "+"; evaluateCurrent() }
                }

                // Row 7: 0, ., =
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NumKey("0", Modifier.weight(2f)) { expression += "0"; evaluateCurrent() }
                    NumKey(".", Modifier.weight(1f)) { expression += "."; evaluateCurrent() }
                    NumKey("=", Modifier.weight(1f), isAction = true, color = AccentCyan) {
                        evaluateCurrent()
                        if (errorMessage == null && expression.isNotBlank()) {
                            onSaveHistory("Scientific: $expression", "= $evaluatedResult")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SciKey(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun NumKey(
    label: String,
    modifier: Modifier = Modifier,
    isAction: Boolean = false,
    color: Color? = null,
    onClick: () -> Unit
) {
    val bgColor = when {
        color != null -> color.copy(alpha = 0.2f)
        isAction -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.surface
    }
    val textColor = color ?: if (isAction) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    Surface(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = bgColor,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 1.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
