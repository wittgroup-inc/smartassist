package com.gowittgroup.smartassist.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme
import com.gowittgroup.smartassistlib.models.banner.BannerContent

@Composable
fun Banner(banner: BannerContent, onClose: () -> Unit) {
    Surface(
        shadowElevation = 8.dp
    ) {

        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = {
                onClose()
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            banner.imageUrl?.let {
                // Image(painter = , contentDescription = )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                banner.title?.let {
                    Text(text = it, style = MaterialTheme.typography.titleMedium)
                }
                banner.subTitle?.let {
                    Text(text = it, style = MaterialTheme.typography.titleSmall)
                }
                banner.descriptions?.let {
                    Text(text = it, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
@Preview
@Composable
private fun BannerPreview() {
    SmartAssistTheme {
        Banner(
            banner = BannerContent(
                id = "1001",
                title = "Title",
                subTitle = "Sub Title",
                descriptions = "Description"
            ),
            onClose = {}
        )
    }

}