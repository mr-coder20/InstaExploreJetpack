package mrcoder.instaexplore.jetpackcompose.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.airbnb.lottie.compose.*
import mrcoder.instaexplore.jetpackcompose.R
import mrcoder.instaexplore.jetpackcompose.model.Photo

@Composable
fun ImageCard(photo: Photo, height: Dp, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.placeholder))

    Card(
        modifier = modifier
            .fillMaxWidth() // Full width for the card
            .height(height) // Variable height for each card
            .padding(1.dp), // Padding inside the card
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), // Remove shadow
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(4.dp) // Slightly round corners
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(photo.url)
                .crossfade(true) // Smooth transition
                .diskCachePolicy(CachePolicy.ENABLED) // Enable disk cache
                .memoryCachePolicy(CachePolicy.ENABLED) // Enable memory cache
                .build(),
            contentDescription = "Loaded Image",
            modifier = Modifier.fillMaxSize(), // Fill the entire card space
            contentScale = ContentScale.Crop, // Image scaling for filling the card
            loading = {
                // Show Lottie animation as placeholder during loading
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.fillMaxSize(), // Animation fills the entire card
                    iterations = LottieConstants.IterateForever // Infinite loop of animation
                )
            },
            error = {
                // Show Lottie animation in case of an error
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.fillMaxSize(), // Animation fills the entire card
                    iterations = LottieConstants.IterateForever // Infinite loop of animation
                )
            }
        )
    }
}
