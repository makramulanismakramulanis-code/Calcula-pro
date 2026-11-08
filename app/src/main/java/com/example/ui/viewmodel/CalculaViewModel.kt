package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.model.*
import com.example.domain.UnitCategory
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object MainTab : ScreenDestination()
    data class CalculatorDetail(val calculatorId: CalculatorId) : ScreenDestination()
    object Scientific : ScreenDestination()
    data class UnitConverter(val category: UnitCategory) : ScreenDestination()
    object Search : ScreenDestination()
}

enum class BottomTab {
    HOME, CATEGORIES, FAVORITES, HISTORY, SETTINGS
}

class CalculaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CalculaDatabase.getDatabase(application)
    private val historyDao = db.historyDao()
    private val prefsManager = PreferencesManager(application)

    val userPreferences: StateFlow<UserPreferences> = prefsManager.userPreferencesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    val favoriteIds: StateFlow<Set<String>> = historyDao.getAllFavoriteIds()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val historyList: StateFlow<List<HistoryEntity>> = historyDao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTab = MutableStateFlow(BottomTab.HOME)
    val currentTab: StateFlow<BottomTab> = _currentTab.asStateFlow()

    private val _currentDestination = MutableStateFlow<ScreenDestination>(ScreenDestination.MainTab)
    val currentDestination: StateFlow<ScreenDestination> = _currentDestination.asStateFlow()

    private val screenBackStack = mutableListOf<ScreenDestination>()

    // Global Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<CalculatorId>> = _searchQuery
        .map { query ->
            val clean = query.trim().lowercase()
            if (clean.isBlank()) {
                CalculatorId.values().toList()
            } else {
                CalculatorId.values().filter { item ->
                    item.title.lowercase().contains(clean) ||
                    item.shortDesc.lowercase().contains(clean) ||
                    item.category.title.lowercase().contains(clean) ||
                    item.keywords.any { it.lowercase().contains(clean) }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalculatorId.values().toList())

    fun selectTab(tab: BottomTab) {
        _currentTab.value = tab
        if (_currentDestination.value != ScreenDestination.MainTab) {
            _currentDestination.value = ScreenDestination.MainTab
            screenBackStack.clear()
        }
    }

    fun navigateTo(dest: ScreenDestination) {
        if (_currentDestination.value != dest) {
            screenBackStack.add(_currentDestination.value)
            _currentDestination.value = dest
        }
    }

    fun navigateBack(): Boolean {
        if (screenBackStack.isNotEmpty()) {
            _currentDestination.value = screenBackStack.removeAt(screenBackStack.size - 1)
            return true
        } else if (_currentDestination.value != ScreenDestination.MainTab) {
            _currentDestination.value = ScreenDestination.MainTab
            return true
        } else if (_currentTab.value != BottomTab.HOME) {
            _currentTab.value = BottomTab.HOME
            return true
        }
        return false
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openCalculator(calc: CalculatorId) {
        viewModelScope.launch {
            prefsManager.recordRecentCalculator(calc.name)
        }
        when (calc) {
            CalculatorId.SCIENTIFIC_CALC -> navigateTo(ScreenDestination.Scientific)
            CalculatorId.MASS_WEIGHT_CONVERTER -> navigateTo(ScreenDestination.UnitConverter(UnitCategory.MASS_WEIGHT))
            CalculatorId.LENGTH_CONVERTER -> navigateTo(ScreenDestination.UnitConverter(UnitCategory.LENGTH))
            CalculatorId.AREA_CONVERTER -> navigateTo(ScreenDestination.UnitConverter(UnitCategory.AREA))
            CalculatorId.VOLUME_CONVERTER -> navigateTo(ScreenDestination.UnitConverter(UnitCategory.VOLUME))
            CalculatorId.TEMPERATURE_CONVERTER -> navigateTo(ScreenDestination.UnitConverter(UnitCategory.TEMPERATURE))
            CalculatorId.SPEED_CONVERTER -> navigateTo(ScreenDestination.UnitConverter(UnitCategory.SPEED))
            CalculatorId.TIME_CONVERTER -> navigateTo(ScreenDestination.UnitConverter(UnitCategory.TIME))
            CalculatorId.DATA_STORAGE_CONVERTER -> navigateTo(ScreenDestination.UnitConverter(UnitCategory.DATA_STORAGE))
            CalculatorId.PRESSURE_CONVERTER -> navigateTo(ScreenDestination.UnitConverter(UnitCategory.PRESSURE))
            CalculatorId.ENERGY_POWER_CONVERTER -> navigateTo(ScreenDestination.UnitConverter(UnitCategory.ENERGY))
            else -> navigateTo(ScreenDestination.CalculatorDetail(calc))
        }
    }

    fun toggleFavorite(calculatorId: String) {
        viewModelScope.launch {
            if (favoriteIds.value.contains(calculatorId)) {
                historyDao.removeFavorite(calculatorId)
            } else {
                historyDao.addFavorite(FavoriteEntity(calculatorId))
            }
        }
    }

    fun saveHistory(
        calcId: String,
        title: String,
        category: String,
        resultSummary: String,
        inputsSummary: String
    ) {
        viewModelScope.launch {
            historyDao.insertHistory(
                HistoryEntity(
                    calculatorId = calcId,
                    title = title,
                    categoryName = category,
                    resultSummary = resultSummary,
                    inputsSummary = inputsSummary
                )
            )
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            historyDao.deleteById(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyDao.clearAllHistory()
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            prefsManager.setThemeMode(mode)
        }
    }

    fun setCurrency(symbol: String) {
        viewModelScope.launch {
            prefsManager.setCurrency(symbol)
        }
    }

    fun setDecimalPlaces(places: Int) {
        viewModelScope.launch {
            prefsManager.setDecimalPlaces(places)
        }
    }

    fun setNumberFormatStyle(style: NumberFormatStyle) {
        viewModelScope.launch {
            prefsManager.setNumberFormatStyle(style)
        }
    }
}
