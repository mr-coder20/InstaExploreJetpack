package com.example.calculatexiaomi.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColorDark,
    primaryContainer = PrimaryVariantColorDark,
    onPrimary = OnPrimaryColorDark,
    secondary = SecondaryColorDark,
    secondaryContainer = SecondaryVariantColorDark,
    onSecondary = OnSecondaryColorDark,
    background = PrimaryColorDark
)

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
fun CalculateXiaomiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !darkTheme

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = if (darkTheme) PrimaryColorDark else PrimaryColor,
            darkIcons = useDarkIcons
        )

    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
