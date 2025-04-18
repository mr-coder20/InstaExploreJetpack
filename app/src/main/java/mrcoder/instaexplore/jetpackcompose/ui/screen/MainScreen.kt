package mrcoder.instaexplore.jetpackcompose.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import mrcoder.instaexplore.jetpackcompose.ui.theme.StylishNavigationBar
import mrcoder.instaexplore.jetpackcompose.viewmodel.NetworkStatusViewModel
import mrcoder.instaexplore.jetpackcompose.viewmodel.PhotoViewModel

// تعریف صفحات با خصوصیات route, label و icon
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Explore : Screen("explore", "Explore", Icons.Filled.Search)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
}

@Composable
fun MainScreen(
    photoViewModel: PhotoViewModel,
    networkViewModel: NetworkStatusViewModel
) {
    // ایجاد NavController برای مدیریت جابجایی صفحات
    val navController = rememberNavController()

    // لیست صفحات
    val screens = listOf(Screen.Home, Screen.Explore, Screen.Profile)

    // استفاده از Scaffold برای طراحی لایه‌بندی
    Scaffold(
        bottomBar = {
            StylishNavigationBar(
                navController = navController, // ارسال NavController به نوار پایین
                screens = screens
            )
        }
    ) { innerPadding ->
        // استفاده از NavHost برای مدیریت صفحات مختلف
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen() // صفحه خانگی
            }
            composable(Screen.Explore.route) {
                ExploreScreen(
                    innerPadding = PaddingValues(0.dp),
                    photoViewModel = photoViewModel,
                    networkViewModel = networkViewModel
                ) // صفحه جستجو
            }
            composable(Screen.Profile.route) {
                ProfileScreen() // صفحه پروفایل
            }
        }
    }
}
