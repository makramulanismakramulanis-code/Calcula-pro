package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.BottomTab

private data class NavItem(
    val tab: BottomTab,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun CalculaBottomNav(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(BottomTab.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
        NavItem(BottomTab.CATEGORIES, "Categories", Icons.Filled.GridView, Icons.Outlined.GridView),
        NavItem(BottomTab.FAVORITES, "Favorites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
        NavItem(BottomTab.HISTORY, "History", Icons.Filled.History, Icons.Outlined.History),
        NavItem(BottomTab.SETTINGS, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    NavigationBar(
        modifier = modifier.testTag("calcula_bottom_navigation"),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        windowInsets = WindowInsets.navigationBars,
        tonalElevation = 6.dp
    ) {
        for (item in items) {
            val isSelected = selectedTab == item.tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_tab_${item.tab.name.lowercase()}")
            )
        }
    }
}
