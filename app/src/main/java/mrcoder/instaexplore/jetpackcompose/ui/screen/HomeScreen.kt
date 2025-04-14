package mrcoder.instaexplore.jetpackcompose.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import mrcoder.instaexplore.jetpackcompose.UiState
import mrcoder.instaexplore.jetpackcompose.model.Photo
import mrcoder.instaexplore.jetpackcompose.ui.components.ErrorScreen
import mrcoder.instaexplore.jetpackcompose.ui.components.ImageCard
import mrcoder.instaexplore.jetpackcompose.ui.components.LoadingScreen
import mrcoder.instaexplore.jetpackcompose.viewmodel.NetworkStatusViewModel
import mrcoder.instaexplore.jetpackcompose.viewmodel.PhotoViewModel

@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    photoViewModel: PhotoViewModel,
    networkViewModel: NetworkStatusViewModel
){

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

