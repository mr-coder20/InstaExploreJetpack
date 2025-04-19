package mrcoder.instaexplore.jetpackcompose.ui.theme

import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    primaryContainer = PrimaryVariantColor,
    onPrimary = OnPrimaryColor,
    secondary = SecondaryColor,
    secondaryContainer = SecondaryVariantColor,
    onSecondary = OnSecondaryColor,
    background = PrimaryColor
)

@Composable
fun ExploreInstaTheme(
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        dynamicLightColorScheme(context)
    } else {
        LightColorScheme
    }

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true  // since we're using light theme

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = PrimaryColor,
            darkIcons = useDarkIcons
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
