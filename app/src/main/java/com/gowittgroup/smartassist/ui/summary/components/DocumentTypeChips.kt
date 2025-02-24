package com.gowittgroup.smartassist.ui.summary.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DocumentTypeChips(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    val documentTypes = listOf(
        "", "Resume", "Business Report", "Research Paper", "Legal Contract", "Project Proposal",
        "Technical Document", "Financial Statement", "Marketing Plan", "News Article",
        "Meeting Minutes", "Product Manual", "Property Document", "Medical Document"
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                val firstRow = documentTypes.filterIndexed { index, _ -> index % 2 == 0 }
                val secondRow = documentTypes.filterIndexed { index, _ -> index % 2 != 0 }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier) {
                    firstRow.forEach { type ->
                        Chip(type, selectedType, onTypeSelected)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 0.dp) ) {
                    secondRow.forEach { type ->
                        Chip(type, selectedType, onTypeSelected)
                    }
                }
            }
        }
    }
}

@Composable
private fun Chip(
    type: String,
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    FilterChip(
        modifier = Modifier.height(32.dp),
        border = BorderStroke(0.dp, color = Color.Transparent),
        selected = type == selectedType,
        onClick = { onTypeSelected(type) },
        label = { Text(type.ifBlank { "Other" }) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Preview
@Composable
private fun DocumentTypeChipsPreview() {
    SmartAssistTheme {
        DocumentTypeChips(selectedType = "Resume", onTypeSelected = {})
    }
}
