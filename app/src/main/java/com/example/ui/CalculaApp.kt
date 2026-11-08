package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AppCategory
import com.example.ui.components.CalculaBottomNav
import com.example.ui.screens.*
import com.example.ui.theme.CalculaProTheme
import com.example.ui.viewmodel.BottomTab
import com.example.ui.viewmodel.CalculaViewModel
import com.example.ui.viewmodel.ScreenDestination

@Composable
fun CalculaApp(
    viewModel: CalculaViewModel = viewModel()
) {
    val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    var categoryFilterForTab by remember { mutableStateOf<AppCategory?>(null) }

    // Android Hardware & Gesture Back Button Handling
    BackHandler(enabled = true) {
        viewModel.navigateBack()
    }

    CalculaProTheme(themeMode = preferences.themeMode) {
        val showBottomNav = currentDestination == ScreenDestination.MainTab

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomNav) {
                    CalculaBottomNav(
                        selectedTab = currentTab,
                        onTabSelected = { tab ->
                            categoryFilterForTab = null
                            viewModel.selectTab(tab)
                        }
                    )
                }
            }
        ) { paddingValues ->
            when (val dest = currentDestination) {
                is ScreenDestination.MainTab -> {
                    when (currentTab) {
                        BottomTab.HOME -> {
                            HomeScreen(
                                favoriteIds = favoriteIds,
                                recentCalculatorIds = preferences.recentCalculators,
                                onOpenCalculator = { viewModel.openCalculator(it) },
                                onToggleFavorite = { viewModel.toggleFavorite(it) },
                                onSearchClick = { viewModel.navigateTo(ScreenDestination.Search) },
                                onSettingsClick = { viewModel.selectTab(BottomTab.SETTINGS) },
                                onSelectCategory = { cat ->
                                    categoryFilterForTab = cat
                                    viewModel.selectTab(BottomTab.CATEGORIES)
                                },
                                modifier = Modifier.padding(paddingValues)
                            )
                        }
                        BottomTab.CATEGORIES -> {
                            CategoriesScreen(
                                initialCategory = categoryFilterForTab,
                                favoriteIds = favoriteIds,
                                onOpenCalculator = { viewModel.openCalculator(it) },
                                onToggleFavorite = { viewModel.toggleFavorite(it) },
                                modifier = Modifier.padding(paddingValues)
                            )
                        }
                        BottomTab.FAVORITES -> {
                            FavoritesScreen(
                                favoriteIds = favoriteIds,
                                onOpenCalculator = { viewModel.openCalculator(it) },
                                onToggleFavorite = { viewModel.toggleFavorite(it) },
                                onExploreClick = { viewModel.selectTab(BottomTab.CATEGORIES) },
                                modifier = Modifier.padding(paddingValues)
                            )
                        }
                        BottomTab.HISTORY -> {
                            HistoryScreen(
                                historyList = historyList,
                                onDeleteEntry = { viewModel.deleteHistoryItem(it) },
                                onClearAll = { viewModel.clearAllHistory() },
                                onReopenCalculation = { viewModel.openCalculator(it) },
                                modifier = Modifier.padding(paddingValues)
                            )
                        }
                        BottomTab.SETTINGS -> {
                            SettingsScreen(
                                preferences = preferences,
                                onSetThemeMode = { viewModel.setThemeMode(it) },
                                onSetCurrency = { viewModel.setCurrency(it) },
                                onSetDecimalPlaces = { viewModel.setDecimalPlaces(it) },
                                onSetNumberFormat = { viewModel.setNumberFormatStyle(it) },
                                modifier = Modifier.padding(paddingValues)
                            )
                        }
                    }
                }
                is ScreenDestination.Search -> {
                    SearchScreen(
                        searchQuery = searchQuery,
                        onQueryChange = { viewModel.setSearchQuery(it) },
                        searchResults = searchResults,
                        favoriteIds = favoriteIds,
                        onOpenCalculator = { viewModel.openCalculator(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is ScreenDestination.Scientific -> {
                    ScientificCalculatorScreen(
                        onBack = { viewModel.navigateBack() },
                        onSaveHistory = { expr, res ->
                            viewModel.saveHistory(
                                "SCIENTIFIC_CALC",
                                "Scientific Calculation",
                                "Scientific",
                                res,
                                expr
                            )
                        }
                    )
                }
                is ScreenDestination.UnitConverter -> {
                    UnitConverterScreen(
                        initialCategory = dest.category,
                        onBack = { viewModel.navigateBack() },
                        onSaveHistory = { title, res, inputs ->
                            viewModel.saveHistory(
                                "UNIT_CONVERTER",
                                title,
                                "Converters",
                                res,
                                inputs
                            )
                        }
                    )
                }
                is ScreenDestination.CalculatorDetail -> {
                    CalculatorDetailScreen(
                        calculatorId = dest.calculatorId,
                        preferences = preferences,
                        isFavorite = favoriteIds.contains(dest.calculatorId.name),
                        onToggleFavorite = { viewModel.toggleFavorite(dest.calculatorId.name) },
                        onBack = { viewModel.navigateBack() },
                        onSaveHistory = { title, res, inputs ->
                            viewModel.saveHistory(
                                dest.calculatorId.name,
                                title,
                                dest.calculatorId.category.title,
                                res,
                                inputs
                            )
                        }
                    )
                }
            }
        }
    }
}
