package mrcoder.instaexplore.jetpackcompose.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import mrcoder.instaexplore.jetpackcompose.UiState
import mrcoder.instaexplore.jetpackcompose.model.Photo
import mrcoder.instaexplore.jetpackcompose.ui.components.ErrorScreen
import mrcoder.instaexplore.jetpackcompose.ui.components.LoadingScreen
import mrcoder.instaexplore.jetpackcompose.ui.components.RefreshableGrid
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

    val savedScrollIndex by photoViewModel.scrollIndex.collectAsState()
    val savedScrollOffset by photoViewModel.scrollOffset.collectAsState()

    val lazyGridState = rememberLazyGridState(
        initialFirstVisibleItemIndex = savedScrollIndex,
        initialFirstVisibleItemScrollOffset = savedScrollOffset
    )

    val derivedScrollState = remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex to lazyGridState.firstVisibleItemScrollOffset
        }
    }

    // Fetch photos when connected and not yet loaded
    LaunchedEffect(isConnected) {
        if (isConnected && uiState !is UiState.Success) {
            photoViewModel.fetchPhotos()
        }
    }

    // Save scroll position on scroll changes
    LaunchedEffect(derivedScrollState.value) {
        photoViewModel.saveScrollState(
            index = derivedScrollState.value.first,
            offset = derivedScrollState.value.second
        )
    }

    val layoutModifier = Modifier
        .padding(
            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
        )
        .background(MaterialTheme.colorScheme.primary)

    Column(modifier = layoutModifier) {
        when {
            !isConnected -> {
                ErrorScreen(
                    errorMessage = "No internet connection. Please check your network.",
                    retryAction = { photoViewModel.fetchPhotos() }
                )
            }

            ((uiState as? UiState.Success<List<Photo>>)?.data ?: emptyList()).isEmpty() && uiState is UiState.Loading   -> {
                // Show full-screen loading only if no data loaded yet
                LoadingScreen()
            }

            uiState is UiState.Error -> {
                ErrorScreen(
                    errorMessage = "Error loading photos. Please try again.",
                    retryAction = { photoViewModel.fetchPhotos() }
                )
            }

            else -> {
                val photos = (uiState as? UiState.Success<List<Photo>>)
                    ?.data
                    ?.distinctBy { it.url }
                    .orEmpty()

                val isFetching by photoViewModel.isFetching.collectAsState()

                RefreshableGrid(
                    uiState = uiState,
                    isFetching = isFetching,
                    onRefresh = { photoViewModel.onRefresh() },
                    lazyGridState = lazyGridState,
                    saveScrollPosition = { index, offset ->
                        photoViewModel.saveScrollState(index, offset)
                    }
                )
            }
        }
    }
}