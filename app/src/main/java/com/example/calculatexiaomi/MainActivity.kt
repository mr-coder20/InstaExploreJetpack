package com.example.calculatexiaomi

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.calculatexiaomi.ui.theme.CalculateXiaomiTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculateXiaomiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Home(innerPadding)
                }
            }
        }
    }
}

@Composable
fun Home(innerPadding: PaddingValues) {
    val context = LocalContext.current
    val photoViewModel: PhotoViewModel = viewModel()
    val networkViewModel: NetworkStatusViewModel =
        viewModel(factory = NetworkStatusViewModelFactory(context))

    val uiState by photoViewModel.uiState.collectAsState()
    val isConnected by networkViewModel.isConnected.collectAsState()

    val lazyGridState = rememberLazyGridState()

    LaunchedEffect(isConnected) {
        if (isConnected) {
            photoViewModel.fetchPhotos()
        }
    }

    Column(
        modifier = Modifier
            .padding(
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                top = 0.dp,
                bottom = 0.dp
            )            .background(color = Color.White)
    ) {
        if (!isConnected) {
            ErrorScreen("No internet connection. Please check your network.") {
                photoViewModel.fetchPhotos()
            }
        } else {
            val photos = (uiState as? UiState.Success<List<Photo>>)?.data?.distinctBy { it.url }
                ?: emptyList()

            if (photos.isEmpty()) {
                when (uiState) {
                    is UiState.Loading -> LoadingScreen()
                    is UiState.Error -> ErrorScreen("No internet connection. Please check your network.") {
                        photoViewModel.fetchPhotos()
                    }

                    else -> {}
                }
            } else {
                // با استفاده از تعداد بیشتری از تصاویر برای نمایش
                val chunkedPhotos = photos.chunked(8)

                LazyVerticalGrid(
                    state = lazyGridState,
                    columns = GridCells.Fixed(1),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(0.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                )
                {
                itemsIndexed(chunkedPhotos) { index, chunk ->
                        when (chunk.size) {
                            8 -> {
                                TripleColumnLayout(
                                    photos = chunk,
                                    uniquePhoto = chunk.last(),
                                    isLeftAligned = index % 2 != 0,
                                    index = index
                                )
                            }

                            else -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()

                                ) {
                                    chunk.forEach { photo ->
                                        ImageCard(
                                            photo = photo,
                                            height = 160.dp,
                                            modifier = Modifier
                                                .weight(1f)

                                        )
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}


@Composable
fun TripleColumnLayout(
    photos: List<Photo>,
    uniquePhoto: Photo,
    isLeftAligned: Boolean,
    index: Int
) {
    // فیلتر کردن عکس‌ها برای جلوگیری از تکراری بودن
    val distinctPhotos = photos.distinctBy { it.url }

    // اطمینان از اینکه تعداد کافی عکس برای نمایش داریم
    if (distinctPhotos.size < 8) return

    // گرفتن عکس‌های 5 تا 7 (یعنی عکس‌های ۵، ۶ و ۷)
    val photos5To7 = distinctPhotos.slice(4..6)

    // گرفتن عکس‌های 0 تا 4
    val photos0To4 = distinctPhotos.take(5)

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(1.dp)) {
        // اولین Row برای عکس‌های 5 تا 7
        if (index >= 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),

                ) {
                photos5To7.forEach { photo ->
                    ImageCard(photo = photo, height = 150.dp, modifier = Modifier.weight(1f))
                }
            }
        }


        // Row دوم: عکس‌های 0 تا 4
        Row(
            modifier = Modifier.fillMaxWidth(),

            ) {
            // اگر باید عکس منحصر به فرد در سمت چپ قرار بگیرد
            if (isLeftAligned) {
                ImageCard(
                    photo = uniquePhoto,
                    height = 240.dp,
                    modifier = Modifier
                        .weight(1f)

                )
            }

            // Column داخلی برای عکس‌های 0 تا 4
            Column(
                modifier = Modifier
                    .weight(1.5f)


                ) {
                listOf(
                    photos0To4[0] to photos0To4[1],
                    photos0To4[2] to photos0To4[3]
                ).forEach { (photo1, photo2) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        ) {
                        ImageCard(photo = photo1, height = 120.dp, modifier = Modifier.weight(1f))
                        ImageCard(photo = photo2, height = 120.dp, modifier = Modifier.weight(1f))
                    }
                }
            }

            // اگر باید عکس منحصر به فرد در سمت راست قرار بگیرد
            if (!isLeftAligned) {
                ImageCard(
                    photo = uniquePhoto,
                    height = 240.dp,
                    modifier = Modifier
                        .weight(1f)

                )
            }
        }
    }
}


@Composable
fun ImageCard(photo: Photo, height: Dp, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.placeholder))

    Card(
        modifier = modifier
            .fillMaxWidth() // عرض کامل کارت
            .height(height) // ارتفاع متغیر برای هر کارت
            .padding(1.dp), // فاصله داخلی کارت
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // حذف سایه
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(1.dp) // گوشه‌ها صاف

    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(photo.url)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = "Loaded Image",
            modifier = Modifier.fillMaxSize(), // پر کردن فضای کارت
            contentScale = ContentScale.Crop, // مقیاس تصویر برای پر کردن کارت
            loading = {
                // انیمیشن Lottie به عنوان placeholder
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.fillMaxSize(), // انیمیشن تمام فضای کارت را پر می‌کند
                    iterations = LottieConstants.IterateForever // انیمیشن به صورت بی‌پایان تکرار می‌شود
                )
            },
            error = {
                // انیمیشن Lottie در صورتی که خطا رخ دهد
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.fillMaxSize(), // انیمیشن تمام فضای کارت را پر می‌کند
                    iterations = LottieConstants.IterateForever // انیمیشن به صورت بی‌پایان تکرار می‌شود
                )
            }
        )
    }
}


@Composable
fun LoadingScreen() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.load))

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LottieAnimation(
            composition = composition,
            modifier = Modifier
                .size(200.dp) // Adjust the size to your desired value
                .align(Alignment.Center), // Center it within the Box
            iterations = LottieConstants.IterateForever // For infinite loop animation
        )
    }
}


@Composable
fun ErrorScreen(errorMessage: String, retryAction: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.placeholder))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier
                        .size(180.dp)
                        .padding(bottom = 16.dp),
                    iterations = LottieConstants.IterateForever
                )

                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Button(
                    onClick = retryAction,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Try Again")
                }
            }
        }
    }
}



class NetworkStatusViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return NetworkStatusViewModel(context) as T
    }
}

class NetworkStatusViewModel(context: Context) : ViewModel() {

    private val _isConnected = MutableStateFlow(isInternetConnected(context))
    val isConnected: StateFlow<Boolean> = _isConnected

    init {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // ایجاد NetworkCallback برای دریافت تغییرات وضعیت اتصال
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // وقتی اینترنت در دسترس است
                _isConnected.value = true
                Log.d("NetworkStatus", "Internet connected")
            }

            override fun onLost(network: Network) {
                // وقتی اتصال اینترنت از دست می‌رود
                _isConnected.value = false
                Log.d("NetworkStatus", "Internet lost")
            }
        }

        // ثبت callback برای دریافت تغییرات شبکه
        connectivityManager.registerDefaultNetworkCallback(callback)
    }
}


fun isInternetConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val activeNetwork = connectivityManager.activeNetwork
    val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
    return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}


interface ApiService {
    @GET("v1/sample-data/photos/?offset=5&limit=500")
    suspend fun getPhotos(@Query("offset") offset: Int, @Query("limit") limit: Int): ApiResponse
}


val retrofit: ApiService = Retrofit.Builder()
    .baseUrl("https://api.slingacademy.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(ApiService::class.java)

data class Photo(val url: String) {
    // تعریف برابری برای جلوگیری از عکس‌های تکراری
    override fun equals(other: Any?): Boolean {
        return other is Photo && other.url == this.url
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }
}

data class ApiResponse(val photos: List<Photo>)

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class PhotoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<Photo>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Photo>>> = _uiState

    private var currentOffset = 0
    private val limit = 500
    private val allPhotos = mutableListOf<Photo>() // تغییر به List برای حفظ ترتیب

    fun fetchPhotos() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = retrofit.getPhotos(currentOffset, limit)

                // حذف عکس‌های تکراری با استفاده از distinctBy روی URL
                val newPhotos = response.photos.filterNot { it in allPhotos }

                if (newPhotos.isNotEmpty()) {
                    allPhotos.addAll(newPhotos)
                    currentOffset += newPhotos.size
                }

                _uiState.value = UiState.Success(allPhotos)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown Error")
                Log.e("PhotoViewModel", "Error fetching photos: ${e.localizedMessage}")
            }
        }
    }

}
