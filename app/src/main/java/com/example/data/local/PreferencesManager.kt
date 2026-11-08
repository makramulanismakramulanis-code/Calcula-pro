package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "calcula_pro_prefs")

enum class NumberFormatStyle {
    INDIAN, INTERNATIONAL
}

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val currencySymbol: String = "₹",
    val decimalPlaces: Int = 2,
    val numberFormatStyle: NumberFormatStyle = NumberFormatStyle.INDIAN,
    val recentCalculators: List<String> = emptyList()
)

class PreferencesManager(private val context: Context) {
    companion object {
        val KEY_THEME = stringPreferencesKey("theme_mode")
        val KEY_CURRENCY = stringPreferencesKey("currency_symbol")
        val KEY_DECIMALS = intPreferencesKey("decimal_places")
        val KEY_NUMBER_STYLE = stringPreferencesKey("number_format_style")
        val KEY_RECENTS = stringPreferencesKey("recent_calculators")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        val themeStr = prefs[KEY_THEME] ?: ThemeMode.DARK.name
        val themeMode = try { ThemeMode.valueOf(themeStr) } catch (_: Exception) { ThemeMode.DARK }

        val currency = prefs[KEY_CURRENCY] ?: "₹"
        val decimals = prefs[KEY_DECIMALS] ?: 2
        val styleStr = prefs[KEY_NUMBER_STYLE] ?: NumberFormatStyle.INDIAN.name
        val style = try { NumberFormatStyle.valueOf(styleStr) } catch (_: Exception) { NumberFormatStyle.INDIAN }

        val recentsStr = prefs[KEY_RECENTS] ?: ""
        val recents = if (recentsStr.isBlank()) emptyList() else recentsStr.split(",").filter { it.isNotBlank() }

        UserPreferences(
            themeMode = themeMode,
            currencySymbol = currency,
            decimalPlaces = decimals,
            numberFormatStyle = style,
            recentCalculators = recents
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME] = mode.name
        }
    }

    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_CURRENCY] = currency
        }
    }

    suspend fun setDecimalPlaces(decimals: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DECIMALS] = decimals.coerceIn(0, 4)
        }
    }

    suspend fun setNumberFormatStyle(style: NumberFormatStyle) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NUMBER_STYLE] = style.name
        }
    }

    suspend fun recordRecentCalculator(calculatorId: String) {
        context.dataStore.edit { prefs ->
            val existing = prefs[KEY_RECENTS]?.split(",")?.filter { it.isNotBlank() }?.toMutableList() ?: mutableListOf()
            existing.remove(calculatorId)
            existing.add(0, calculatorId)
            val trimmed = existing.take(8)
            prefs[KEY_RECENTS] = trimmed.joinToString(",")
        }
    }
}
