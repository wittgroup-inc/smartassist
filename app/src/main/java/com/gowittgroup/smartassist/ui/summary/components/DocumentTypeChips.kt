package com.gowittgroup.smartassist.ui.summary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val firstRow = documentTypes.filterIndexed { index, _ -> index % 2 == 0 }
                val secondRow = documentTypes.filterIndexed { index, _ -> index % 2 != 0 }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    firstRow.forEach { type ->
                        FilterChip(
                            selected = type == selectedType,
                            onClick = { onTypeSelected(type) },
                            label = { Text(type.ifBlank { "Other" }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = Color.Black
                            )
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    secondRow.forEach { type ->
                        FilterChip(
                            selected = type == selectedType,
                            onClick = { onTypeSelected(type) },
                            label = { Text(type.ifBlank { "Other" }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = Color.Black
                            )
                        )
                    }
                }
            }
        }
    }
}
