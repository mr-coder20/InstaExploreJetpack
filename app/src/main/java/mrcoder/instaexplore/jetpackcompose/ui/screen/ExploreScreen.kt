package mrcoder.instaexplore.jetpackcompose.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import mrcoder.instaexplore.jetpackcompose.UiState
import mrcoder.instaexplore.jetpackcompose.model.Photo
import mrcoder.instaexplore.jetpackcompose.ui.components.ErrorScreen
import mrcoder.instaexplore.jetpackcompose.ui.components.ImageCard
import mrcoder.instaexplore.jetpackcompose.ui.components.LoadingScreen
import mrcoder.instaexplore.jetpackcompose.viewmodel.NetworkStatusViewModel
import mrcoder.instaexplore.jetpackcompose.viewmodel.PhotoViewModel

@Composable
fun ExploreScreen(
    innerPadding: PaddingValues,
    photoViewModel: PhotoViewModel,
    networkViewModel: NetworkStatusViewModel
) {
    val uiState by photoViewModel.uiState.collectAsState()
    val isConnected by networkViewModel.isConnected.collectAsState()

    val lazyGridState = rememberLazyGridState()

    // SwipeRefresh state
    var isRefreshing by remember { mutableStateOf(false) }

    // Fetch photos when there's internet
    LaunchedEffect(isConnected) {
        if (isConnected && uiState !is UiState.Success) {
            photoViewModel.fetchPhotos() // Load photos if internet is available
        }
    }

    // Swipe to refresh
    fun onRefresh() {
        isRefreshing = true
        photoViewModel.fetchPhotos() // Fetch photos again
        isRefreshing = false
    }

    // Layout for the screen
    Column(
        modifier = Modifier
            .padding(
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                top = 0.dp,
                bottom = 0.dp
            )
            .background(color = Color.White)
    ) {
        // If there's no internet, show an error screen
        if (!isConnected) {
            ErrorScreen("No internet connection. Please check your network.") {
                photoViewModel.fetchPhotos() // Retry loading photos
            }
        } else {
            val photos = (uiState as? UiState.Success<List<Photo>>)?.data?.distinctBy { it.url } ?: emptyList()

            if (photos.isEmpty()) {
                when (uiState) {
                    is UiState.Loading -> LoadingScreen()
                    is UiState.Error -> ErrorScreen("Error loading photos. Please try again.") {
                        photoViewModel.fetchPhotos() // Retry loading photos
                    }
                    else -> {}
                }
            } else {
                val chunkedPhotos = photos.chunked(8)

                SwipeRefresh(
                    state = SwipeRefreshState(isRefreshing),
                    onRefresh = { onRefresh() },
                    indicator = { state, trigger ->
                        // Custom refresh indicator
                        if (state.isRefreshing) {
                            CircularProgressIndicator(modifier = Modifier.size(40.dp))
                        }
                    }
                ) {
                    LazyVerticalGrid(
                        state = lazyGridState,
                        columns = GridCells.Fixed(1),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(0.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
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
                                                modifier = Modifier.weight(1f)
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
}

@Composable
fun TripleColumnLayout(
    photos: List<Photo>,
    uniquePhoto: Photo,
    isLeftAligned: Boolean,
    index: Int
) {
    val distinctPhotos = photos.distinctBy { it.url }

    if (distinctPhotos.size < 8) return

    val photos5To7 = distinctPhotos.slice(4..6)
    val photos0To4 = distinctPhotos.take(5)

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(1.dp)) {
        if (index >= 2) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                photos5To7.forEach { photo ->
                    ImageCard(photo = photo, height = 150.dp, modifier = Modifier.weight(1f))
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            if (isLeftAligned) {
                ImageCard(
                    photo = uniquePhoto,
                    height = 240.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            Column(modifier = Modifier.weight(1.5f)) {
                listOf(
                    photos0To4[0] to photos0To4[1],
                    photos0To4[2] to photos0To4[3]
                ).forEach { (photo1, photo2) ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ImageCard(photo = photo1, height = 120.dp, modifier = Modifier.weight(1f))
                        ImageCard(photo = photo2, height = 120.dp, modifier = Modifier.weight(1f))
                    }
                }
            }

            if (!isLeftAligned) {
                ImageCard(
                    photo = uniquePhoto,
                    height = 240.dp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
