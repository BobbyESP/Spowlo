package com.bobbyesp.spowlo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDirection
import com.kyant.monet.dynamicColorScheme

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun SpowloTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isHighContrastModeEnabled: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme =
        dynamicColorScheme(!darkTheme).run {
            if (isHighContrastModeEnabled && darkTheme) copy(
                surface = Color.Black,
                background = Color.Black,
            )
            else this
        }
    ProvideTextStyle(
        value = LocalTextStyle.current.copy(
            lineBreak = LineBreak.Paragraph,
            textDirection = TextDirection.Content
        )
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}