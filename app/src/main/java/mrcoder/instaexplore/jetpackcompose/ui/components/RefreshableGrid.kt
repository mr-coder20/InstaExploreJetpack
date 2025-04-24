package mrcoder.instaexplore.jetpackcompose.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import mrcoder.instaexplore.jetpackcompose.R
import mrcoder.instaexplore.jetpackcompose.UiState
import mrcoder.instaexplore.jetpackcompose.model.Photo

@Composable
fun RefreshableGrid(
    uiState: UiState<List<Photo>>,
    isFetching: Boolean,
    onRefresh: () -> Unit,
    lazyGridState: LazyGridState,
    saveScrollPosition: (index: Int, offset: Int) -> Unit
) {
    val photos = (uiState as? UiState.Success)?.data?.distinctBy { it.url } ?: emptyList()
    val isLoading = uiState is UiState.Loading
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isFetching)
    val chunkedPhotos = remember(photos) { photos.chunked(8) }
    val derivedScrollState = remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex to lazyGridState.firstVisibleItemScrollOffset
        }
    }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = onRefresh,
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary,
                backgroundColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f),
                modifier = Modifier,
                scale = true
            )
        }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            state = lazyGridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(0.dp)
        ) {
            // Inline loading indicator at bottom if already have data
            if ( photos.isNotEmpty() && isFetching) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.load))
                        LottieAnimation(
                            composition = composition,
                            modifier = Modifier.size(100.dp),
                            iterations = LottieConstants.IterateForever
                        )
                    }
                }
            }

            itemsIndexed(
                chunkedPhotos,
                key = { index, chunk -> chunk.firstOrNull()?.url ?: index }) { index, chunk ->
                if (chunk.size == 8) {
                    TripleColumnLayout(
                        photos = chunk,
                        uniquePhoto = chunk.last(),
                        isLeftAligned = index % 2 != 0,
                        index = index
                    )
                } else {
                    androidx.compose.foundation.layout.Row(modifier = Modifier.fillMaxSize()) {
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

    // Save scroll position
    LaunchedEffect(derivedScrollState.value) {
        saveScrollPosition(
            derivedScrollState.value.first,
            derivedScrollState.value.second
        )
    }
}