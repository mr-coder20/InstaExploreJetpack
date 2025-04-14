package mrcoder.instaexplore.jetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import mrcoder.instaexplore.jetpackcompose.data.AppContainer
import mrcoder.instaexplore.jetpackcompose.ui.screen.HomeScreen
import mrcoder.instaexplore.jetpackcompose.ui.theme.ExploreInstaTheme
import mrcoder.instaexplore.jetpackcompose.viewmodel.NetworkStatusViewModel
import mrcoder.instaexplore.jetpackcompose.viewmodel.NetworkStatusViewModelFactory
import mrcoder.instaexplore.jetpackcompose.viewmodel.PhotoViewModel
import mrcoder.instaexplore.jetpackcompose.viewmodel.PhotoViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExploreInstaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val photoViewModel: PhotoViewModel = viewModel(
                        factory = PhotoViewModelFactory(AppContainer.photoRepository)
                    )
                    val networkViewModel: NetworkStatusViewModel =
                        viewModel(factory = NetworkStatusViewModelFactory(LocalContext.current))

                    HomeScreen(innerPadding, photoViewModel, networkViewModel)
                }
            }
        }
    }
}





