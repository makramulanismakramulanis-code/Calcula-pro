package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AppCategory
import com.example.data.model.CalculatorId
import com.example.ui.components.CalculatorCard

@Composable
fun CategoriesScreen(
    initialCategory: AppCategory? = null,
    favoriteIds: Set<String>,
    onOpenCalculator: (CalculatorId) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(initialCategory) }

    val filteredCalculators = remember(selectedCategory) {
        if (selectedCategory == null) {
            CalculatorId.values().toList()
        } else {
            CalculatorId.values().filter { it.category == selectedCategory }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("categories_screen")
    ) {
        // Top Title
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp)) {
            Text(
                text = "Calculator Categories",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Browse 40+ specialized smart tools",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { selectedCategory = null },
                label = { Text("All (${CalculatorId.values().size})") }
            )

            for (cat in AppCategory.values()) {
                val count = CalculatorId.values().count { it.category == cat }
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text("${cat.title.replace(" Calculators", "").replace(" Converters", "")} ($count)") }
                )
            }
        }

        // List of Calculators
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredCalculators) { calc ->
                CalculatorCard(
                    calculator = calc,
                    isFavorite = favoriteIds.contains(calc.name),
                    onToggleFavorite = { onToggleFavorite(calc.name) },
                    onClick = { onOpenCalculator(calc) }
                )
            }
        }
    }
}
