package com.gowittgroup.smartassist.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.gowittgroup.smartassist.R

// Set of Material typography styles to start with

val OpenSans =
    FontFamily(
        Font(R.font.open_sans_regular),
        Font(R.font.open_sans_italic, style = FontStyle.Italic),
        Font(R.font.open_sans_medium, FontWeight.Medium),
        Font(R.font.open_sans_medium_italic, FontWeight.Medium, style = FontStyle.Italic),
        Font(R.font.open_sans_bold, FontWeight.Bold),
        Font(R.font.open_sans_bold_italic, FontWeight.Bold, style = FontStyle.Italic),
        Font(R.font.open_sans_light, FontWeight.Light),
        Font(R.font.open_sans_light_italic, FontWeight.Light, style = FontStyle.Italic),
        Font(R.font.open_sans_extra_bold, FontWeight.ExtraBold),
        Font(R.font.open_sans_extra_bold, FontWeight.ExtraBold, style = FontStyle.Italic)
    )


val Typography = Typography(
    displayLarge = Typography().displayLarge.copy(fontFamily = OpenSans),
    displayMedium = Typography().displayMedium.copy(fontFamily = OpenSans),
    displaySmall = Typography().displaySmall.copy(fontFamily = OpenSans),
    headlineLarge = Typography().headlineLarge.copy(fontFamily = OpenSans),
    headlineMedium = Typography().headlineMedium.copy(fontFamily = OpenSans),
    headlineSmall = Typography().headlineSmall.copy(fontFamily = OpenSans),
    titleLarge = Typography().titleLarge.copy(fontFamily = OpenSans),
    titleMedium = Typography().titleMedium.copy(fontFamily = OpenSans),
    titleSmall = Typography().titleSmall.copy(fontFamily = OpenSans),
    bodyLarge = Typography().bodyLarge.copy(fontFamily = OpenSans),
    bodyMedium = Typography().bodyMedium.copy(fontFamily = OpenSans),
    bodySmall = Typography().bodySmall.copy(fontFamily = OpenSans),
    labelLarge = Typography().labelLarge.copy(fontFamily = OpenSans),
    labelMedium = Typography().labelMedium.copy(fontFamily = OpenSans),
    labelSmall = Typography().labelSmall.copy(fontFamily = OpenSans),
)
