package com.gowittgroup.smartassist.ui.splashscreen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R

@Composable
internal fun AppLogoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val logo = painterResource(id = R.drawable.ic_bot_square)
        val logoAspectRatio = logo.intrinsicSize.width / logo.intrinsicSize.height
        Image(
            painter = painterResource(id = R.drawable.ic_bot_square),
            contentDescription = stringResource(R.string.logo_content_desc),
            modifier = Modifier
                .height(80.dp)
                .aspectRatio(logoAspectRatio),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        Image(
            painter = painterResource(id = R.drawable.ic_app_title),
            contentDescription = stringResource(R.string.title_logo_content_desc),
            modifier = Modifier.padding(8.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
    }
}