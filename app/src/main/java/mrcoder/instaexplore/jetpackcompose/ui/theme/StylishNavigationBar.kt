package mrcoder.instaexplore.jetpackcompose.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import mrcoder.instaexplore.jetpackcompose.ui.screen.Screen

@Composable
fun StylishNavigationBar(
    navController: NavHostController,
    screens: List<Screen>
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        tonalElevation = 8.dp,
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
        modifier = Modifier
            .height(56.dp)
            .shadow(0.5f.dp, shape = RectangleShape) // Add shadow to NavigationBar
    ) {
        screens.forEach { screen ->
            // بررسی اینکه آیا صفحه انتخاب شده است
            val selected = currentRoute == screen.route
            val interactionSource = remember { MutableInteractionSource() }

            // آیتم نوار پایین
            NavigationBarItem(
                selected = selected,
                onClick = {
                    // جلوگیری از انتخاب دوباره همان صفحه
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            launchSingleTop = true  // جلوگیری از بارگذاری دوباره صفحه قبلی
                            popUpTo(navController.graph.startDestinationRoute!!) {
                                inclusive = false // صفحه قبلی حذف نمی‌شود
                            }
                        }
                    }
                },
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.label,
                            tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(if (selected) 28.dp else 24.dp)  // اندازه آیکون‌ها
                        )
                        if (selected) {
                            Spacer(modifier = Modifier.height(2.dp))  // فاصله زیر آیکون
                            Box(
                                modifier = Modifier
                                    .height(3.dp)
                                    .width(20.dp)
                                    .clip(RoundedCornerShape(50)) // گوشه‌های گرد
                                    .background(MaterialTheme.colorScheme.onPrimary)  // رنگ خط زیر آیکون
                            )
                        }
                    }
                },
                label = {
                    Text(
                        text = screen.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                alwaysShowLabel = false, // نمایش برچسب فقط در هنگام انتخاب آیتم
                interactionSource = interactionSource
            )
        }
    }
}
