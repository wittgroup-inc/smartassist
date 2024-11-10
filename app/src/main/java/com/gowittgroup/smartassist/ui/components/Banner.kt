package com.gowittgroup.smartassist.ui.components

import android.renderscript.Sampler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme
import com.gowittgroup.smartassistlib.models.banner.BannerContent

@Composable
fun Banner(banner: BannerContent, onClose: () -> Unit) {
    Surface(
        shadowElevation = 8.dp
    ) {

        Box(modifier = Modifier.fillMaxWidth()) {


            if (banner.imageUrl == null) {
                Column(modifier = Modifier.padding(16.dp)) {
                    banner.title?.let {
                        Text(
                            modifier = Modifier.padding(end = 24.dp),
                            text = it,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    banner.subTitle?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleSmall.copy(color = Color.DarkGray),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    banner.descriptions?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            banner.imageUrl?.let {
                AsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    model = it,
                    contentDescription = null,
                )
            }
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
        }
    }
}

@Preview
@Composable
private fun BannerPreview(@PreviewParameter(LoremIpsum::class) text: String) {
    SmartAssistTheme {
        Banner(
            banner = BannerContent(
                id = "1001",
                title = text,
                subTitle = text,
                descriptions = text
            ),
            onClose = {}
        )
    }

}