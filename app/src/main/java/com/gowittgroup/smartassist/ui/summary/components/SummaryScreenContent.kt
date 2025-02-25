package com.gowittgroup.smartassist.ui.summary.components

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.components.SimpleMarkdown
import com.gowittgroup.smartassist.ui.components.buttons.PrimaryButton
import com.gowittgroup.smartassist.ui.components.buttons.SecondaryOutlinedButton
import com.gowittgroup.smartassist.ui.summary.SummaryUiState
import com.gowittgroup.smartassist.ui.summary.models.FileItem

@Composable
internal fun SummaryScreenContent(
    scrollState: ScrollState,
    showBottomSheet: (Boolean) -> Unit,
    onTypeSelected: (String) -> Unit,
    uiState: SummaryUiState,
    onRemoveFile: (Uri) -> Unit,
    onGoClick: (Context) -> Unit,
    context: Context,
    expandedScreen: Boolean
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        SecondaryOutlinedButton(
            onClick = {
                showBottomSheet(true)
            },
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.select_documents_button_text)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.selectedFiles.isNotEmpty()) {
            Text(text = stringResource(R.string.selected_documents_section_heading), style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            FileGridView(uiState.selectedFiles.map { FileItem(it.toString(), it) }) {
                onRemoveFile(it.thumbnailUrl)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(text = stringResource(R.string.select_document_type), style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))
        DocumentTypeChips(
            onTypeSelected = { type -> onTypeSelected(type) },
            selectedType = uiState.documentType,
            expandedScreen = expandedScreen
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            onClick = {
                if (uiState.selectedFiles.isNotEmpty()) {
                    onGoClick(context)
                }

            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.summarizedButtonEnabled,
            text = stringResource(R.string.summarize_button_text)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            if(uiState.summary.isNotBlank()){
                Text(text = stringResource(R.string.summary_title), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                SimpleMarkdown(uiState.summary)
                if (uiState.showSummaryFooter) {
                    Spacer(modifier = Modifier.height(8.dp))
                    SummaryFooter(item = uiState.summary, context = context, aiTool = uiState.aiTool)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SummaryContentPreview() {
    val dummyUiState = SummaryUiState(
        summary = "Not blank",
        selectedFiles = listOf(
            Uri.parse("content://dummy/file1.jpg"),
            Uri.parse("content://dummy/file2.pdf")
        ),
        documentType = "PDF",
        processingIsInProgress = false,
        notificationState = null,
        showSummaryFooter = true
    )

    SummaryScreenContent(
        uiState = dummyUiState,
        onGoClick = {},
        onRemoveFile = { /* Mock function */ },
        onTypeSelected = { /* Mock function */ },
        scrollState = rememberScrollState(),
        showBottomSheet = {},
        context = LocalContext.current,
        expandedScreen = false
    )
}
