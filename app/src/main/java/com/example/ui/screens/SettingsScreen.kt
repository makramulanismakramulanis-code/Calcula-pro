package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.NumberFormatStyle
import com.example.data.local.UserPreferences
import com.example.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    preferences: UserPreferences,
    onSetThemeMode: (ThemeMode) -> Unit,
    onSetCurrency: (String) -> Unit,
    onSetDecimalPlaces: (Int) -> Unit,
    onSetNumberFormat: (NumberFormatStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showDecimalsDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    val currencies = listOf(
        "₹" to "Indian Rupee (₹)",
        "$" to "US Dollar ($)",
        "€" to "Euro (€)",
        "£" to "British Pound (£)",
        "¥" to "Japanese Yen (¥)",
        "C$" to "Canadian Dollar (C$)",
        "A$" to "Australian Dollar (A$)",
        "AED" to "UAE Dirham (AED)",
        "kr" to "Nordic Krona (kr)",
        "CHF" to "Swiss Franc (CHF)"
    )

    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Select Primary Currency") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    for ((symbol, label) in currencies) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onSetCurrency(symbol)
                                    showCurrencyDialog = false
                                }
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = preferences.currencySymbol == symbol,
                                onClick = {
                                    onSetCurrency(symbol)
                                    showCurrencyDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) { Text("Close") }
            }
        )
    }

    if (showDecimalsDialog) {
        AlertDialog(
            onDismissRequest = { showDecimalsDialog = false },
            title = { Text("Decimal Precision") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (d in 0..4) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onSetDecimalPlaces(d)
                                    showDecimalsDialog = false
                                }
                                .padding(vertical = 8.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = preferences.decimalPlaces == d,
                                onClick = {
                                    onSetDecimalPlaces(d)
                                    showDecimalsDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("$d decimal places (e.g. ${if (d == 0) "100" else "100." + "0".repeat(d)})")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDecimalsDialog = false }) { Text("Close") }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Calcula Pro") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Calcula Pro v1.0.0",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Calcula Pro is an all-in-one smart calculator suite designed with high mathematical precision, comprehensive financial planning tools, Indian and global taxation formulas, unit converters, and civil engineering calculators.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "• 100% Offline Capability\n• Zero Tracking or Server Storage\n• Material 3 Modern Design System\n• Accurate Financial & Construction Formulas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text("Done") }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Your Privacy is Guaranteed",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Calcula Pro runs completely on your Android device. All calculations, personal financial inputs, and history records are saved in your local device SQLite Room database and are never transmitted to any external server or third party.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) { Text("I Understand") }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = "Settings & Preferences",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Personalize numbers, theme & currency formats",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Appearance Section
        item {
            SettingsSectionHeader(title = "Appearance & Theme")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Theme Mode",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (mode in ThemeMode.values()) {
                            val isSel = preferences.themeMode == mode
                            FilterChip(
                                selected = isSel,
                                onClick = { onSetThemeMode(mode) },
                                label = {
                                    Text(
                                        when (mode) {
                                            ThemeMode.SYSTEM -> "System"
                                            ThemeMode.DARK -> "Dark"
                                            ThemeMode.LIGHT -> "Light"
                                        }
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Calculation & Number Formatting Section
        item {
            SettingsSectionHeader(title = "Calculation & Formatting")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    SettingsRowItem(
                        icon = Icons.Outlined.Payments,
                        title = "Default Currency",
                        subtitle = currencies.find { it.first == preferences.currencySymbol }?.second ?: preferences.currencySymbol,
                        onClick = { showCurrencyDialog = true }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    SettingsRowItem(
                        icon = Icons.Outlined.Pin,
                        title = "Decimal Precision",
                        subtitle = "${preferences.decimalPlaces} decimal places",
                        onClick = { showDecimalsDialog = true }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    SettingsRowItem(
                        icon = Icons.Outlined.FormatListNumbered,
                        title = "Number Grouping Style",
                        subtitle = if (preferences.numberFormatStyle == NumberFormatStyle.INDIAN) "Indian (12,34,567)" else "International (1,234,567)",
                        onClick = {
                            val newStyle = if (preferences.numberFormatStyle == NumberFormatStyle.INDIAN) {
                                NumberFormatStyle.INTERNATIONAL
                            } else {
                                NumberFormatStyle.INDIAN
                            }
                            onSetNumberFormat(newStyle)
                        }
                    )
                }
            }
        }

        // About & Legal Section
        item {
            SettingsSectionHeader(title = "About & Support")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    SettingsRowItem(
                        icon = Icons.Outlined.Info,
                        title = "About Calcula Pro",
                        subtitle = "Version 1.0.0 — Production Build",
                        onClick = { showAboutDialog = true }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    SettingsRowItem(
                        icon = Icons.Outlined.Security,
                        title = "Privacy Policy",
                        subtitle = "Device-local offline data guarantee",
                        onClick = { showPrivacyDialog = true }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    SettingsRowItem(
                        icon = Icons.Outlined.Share,
                        title = "Share Calcula Pro",
                        subtitle = "Recommend to colleagues & friends",
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Check out Calcula Pro — The Ultimate All-in-One Smart Calculator for Android with Finance, GST, Tax, Math, Health and Converters!"
                                )
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Calcula Pro"))
                        }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    SettingsRowItem(
                        icon = Icons.Outlined.Star,
                        title = "Rate App",
                        subtitle = "Support future calculator additions",
                        onClick = {
                            Toast.makeText(context, "Thank you for supporting Calcula Pro!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Open",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
    }
}
