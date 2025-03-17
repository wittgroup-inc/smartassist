package com.gowittgroup.smartassist.ui.summary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.gowittgroup.smartassist.ui.summary.models.FileItem

@Composable
internal fun FileGridView(
    files: List<FileItem>,
    onDelete: (FileItem) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = files, key = {it.id}) { file ->
            FileThumbnail(file, onDelete)
        }
    }
}

@Preview
@Composable
fun PreviewFileGridView() {
    val sampleFiles = remember {
        mutableStateListOf(
            FileItem("1", "https://via.placeholder.com/150".toUri()),
            FileItem("2", "https://via.placeholder.com/150".toUri()),
            FileItem("3", "https://via.placeholder.com/150".toUri()),
            FileItem("4", "https://via.placeholder.com/150".toUri()),
            FileItem("5", "https://via.placeholder.com/150".toUri()),
            FileItem("6", "https://via.placeholder.com/150".toUri()),
            FileItem("7", "https://via.placeholder.com/150".toUri()),
            FileItem("8", "https://via.placeholder.com/150".toUri()),
            FileItem("9", "https://via.placeholder.com/150".toUri()),
            FileItem("10", "https://via.placeholder.com/150".toUri())
        )
    }

    FileGridView(
        files = sampleFiles,
        onDelete = { file -> sampleFiles.remove(file) }
    )
}