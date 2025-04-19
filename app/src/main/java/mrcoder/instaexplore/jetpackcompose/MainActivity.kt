package mrcoder.instaexplore.jetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import mrcoder.instaexplore.jetpackcompose.data.AppContainer
import mrcoder.instaexplore.jetpackcompose.ui.components.LoadingScreen
import mrcoder.instaexplore.jetpackcompose.ui.screen.MainScreen
import mrcoder.instaexplore.jetpackcompose.ui.theme.ExploreInstaTheme
import mrcoder.instaexplore.jetpackcompose.viewmodel.AppStateViewModel
import mrcoder.instaexplore.jetpackcompose.viewmodel.AppStateViewModelFactory
import mrcoder.instaexplore.jetpackcompose.viewmodel.NetworkStatusViewModel
import mrcoder.instaexplore.jetpackcompose.viewmodel.NetworkStatusViewModelFactory
import mrcoder.instaexplore.jetpackcompose.viewmodel.PhotoViewModel
import mrcoder.instaexplore.jetpackcompose.viewmodel.PhotoViewModelFactory
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import mrcoder.instaexplore.jetpackcompose.ui.components.ErrorScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExploreInstaTheme {

                val context = LocalContext.current
                val systemUiController: SystemUiController = rememberSystemUiController()

                // تغییر رنگ نوار وضعیت و نوار ناوبری
                systemUiController.setSystemBarsColor(color = MaterialTheme.colorScheme.primary)
                // ساخت PhotoViewModel با استفاده از Factory
                val photoViewModel: PhotoViewModel = viewModel(
                    factory = PhotoViewModelFactory(AppContainer.photoRepository)
                )

                // ساخت AppStateViewModel با استفاده از Factory
                val appStateViewModel: AppStateViewModel = ViewModelProvider(
                    this,
                    AppStateViewModelFactory(photoViewModel)
                )[AppStateViewModel::class.java]

                // گرفتن وضعیت UI از ViewModel با استفاده از collectAsStateWithLifecycle()
                val uiState = appStateViewModel.uiState.collectAsState().value

                // ساخت NetworkStatusViewModel با استفاده از Factory
                val networkViewModel: NetworkStatusViewModel =
                    viewModel(factory = NetworkStatusViewModelFactory(context))

                // بررسی وضعیت عکس‌ها و مدیریت لودینگ یا خطا
                when (uiState) {
                    is UiState.Loading -> {
                        // صفحه لودینگ
                        LoadingScreen()
                    }

                    is UiState.Error -> {
                        // نمایش صفحه خطا
                        ErrorScreen(
                            "No internet connection. Please check your network."
                        ) {
                            photoViewModel.fetchPhotos()
                        }
                    }

                    is UiState.Success -> {
                        // نمایش صفحه اصلی پس از موفقیت در بارگذاری
                        MainScreen(
                            photoViewModel = photoViewModel,
                            networkViewModel = networkViewModel

                        )
                    }
                }
            }
        }
    }
}