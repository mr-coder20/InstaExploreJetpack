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
