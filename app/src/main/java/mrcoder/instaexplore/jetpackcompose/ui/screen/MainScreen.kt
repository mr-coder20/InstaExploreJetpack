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
    val navController = rememberNavController()
    val screens = listOf(Screen.Home, Screen.Explore, Screen.Profile)

    Scaffold(
        bottomBar = {
            StylishNavigationBar(navController, screens)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    innerPadding = PaddingValues(0.dp),
                    photoViewModel = photoViewModel,
                    networkViewModel = networkViewModel
                )
            }
            composable(Screen.Explore.route) {
                ExploreScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}
