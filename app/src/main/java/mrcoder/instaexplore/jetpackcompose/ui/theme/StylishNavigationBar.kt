package mrcoder.instaexplore.jetpackcompose.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import mrcoder.instaexplore.jetpackcompose.ui.screen.Screen

@Composable
fun StylishNavigationBar(
    navController: NavHostController,
    screens: List<Screen>
) {
    val currentBackStack = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack.value?.destination?.route

    // استفاده از NavigationBar بدون متن
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 4.dp)
    ) {
        screens.forEach { screen ->
            val isSelected = currentRoute == screen.route
            val interactionSource = remember { MutableInteractionSource() }

            // نمایش فقط آیکون بدون متن
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationRoute!!) {
                                saveState = true
                                inclusive = false
                            }
                        }
                    }
                },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.label,
                            tint = if (isSelected) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .height(3.dp)
                                    .width(20.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.secondary)
                            )
                        }
                    }
                },
                label = { },
                alwaysShowLabel = false,
                interactionSource = interactionSource
            )
        }
    }
}


