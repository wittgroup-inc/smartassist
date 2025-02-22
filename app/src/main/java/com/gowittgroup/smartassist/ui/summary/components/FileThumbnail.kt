package com.gowittgroup.smartassist.ui.summary.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.gowittgroup.smartassist.ui.summary.models.FileItem
import com.gowittgroup.smartassist.util.isPdf

@Composable
fun FileThumbnail(file: FileItem, onDelete: (FileItem) -> Unit) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .padding(4.dp)
            .size(80.dp),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp,
    ) {
        Box {
            if (file.thumbnailUrl.isPdf(context = LocalContext.current)) {
                val pdfBitmap = remember { generatePdfPreview(file.thumbnailUrl, context) }
                pdfBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "File Thumbnail",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            } else {
                Image(
                    painter = rememberAsyncImagePainter(file.thumbnailUrl),
                    contentDescription = "File Thumbnail",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd)
                    .clickable { onDelete(file) }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val size = size
                    drawPath(
                        path = Path().apply {
                            moveTo(size.width, 0f)  // Top-right corner
                            lineTo(
                                size.width - size.width * 1.5f,
                                0f
                            ) // Move left (controls triangle width)
                            lineTo(
                                size.width,
                                size.height * 1.5f
                            ) // Move down (controls triangle height)
                            close()
                        },
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                }

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.TopEnd)
                        .offset((-4).dp, (4).dp) // Adjust position inside the folded corner
                )
            }
        }
    }

}

fun generatePdfPreview(uri: Uri, context: Context): Bitmap? {
    return try {
        context.contentResolver.openFileDescriptor(uri, "r")?.use { parcelFileDescriptor ->
            val pdfRenderer = PdfRenderer(parcelFileDescriptor)
            val page = pdfRenderer.openPage(0) // First page

            val bitmap = Bitmap.createBitmap(
                page.width, page.height, Bitmap.Config.ARGB_8888
            )
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            pdfRenderer.close()
            bitmap
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}