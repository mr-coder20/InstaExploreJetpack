package mrcoder.instaexplore.jetpackcompose.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mrcoder.instaexplore.jetpackcompose.ui.theme.StylishNavigationBar
import mrcoder.instaexplore.jetpackcompose.viewmodel.NetworkStatusViewModel
import mrcoder.instaexplore.jetpackcompose.viewmodel.PhotoViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Outlined.Home)
    object Explore : Screen("explore", "Explore", Icons.Filled.Search)
    object Profile : Screen("profile", "Profile", Icons.Outlined.Person)
}

@Composable
fun MainScreen(
    photoViewModel: PhotoViewModel,
    networkViewModel: NetworkStatusViewModel
) {
    val navController = rememberNavController()
    val screens = listOf(Screen.Home, Screen.Explore, Screen.Profile)



    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.primary),
        bottomBar = {
            StylishNavigationBar(
                navController = navController,
                screens = screens
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }

            composable(Screen.Explore.route) {
                ExploreScreen(
                    innerPadding = innerPadding,
                    photoViewModel = photoViewModel,
                    networkViewModel = networkViewModel
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}
