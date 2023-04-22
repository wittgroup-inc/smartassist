package com.gowittgroup.smartassist.ui.promptscreen

import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.LoadingScreen
import com.gowittgroup.smartassistlib.models.Prompts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptsScreen(
    viewModel: PromptsViewModel,
    isExpanded: Boolean,
    openDrawer: () -> Unit,
    smartAnalytics: SmartAnalytics,
    navigateToHome: (id: Long?, prompt: String?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    logUserEntersEvent(smartAnalytics)

    ErrorView(uiState.error).also { viewModel.resetErrorMessage() }

    Scaffold(topBar = {
        AppBar(
            title = stringResource(R.string.prompts_screen_title),
            openDrawer = openDrawer,
            isExpanded = isExpanded
        )
    }, content = { padding ->
        if (uiState.loading) {
            LoadingScreen(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxHeight()
            ) {

                items(uiState.prompts) {
                    val isExpandMore = remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
                            .background(color = Color.Transparent),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        HeaderItem(it, isExpandMore.value) { isExpandMore.value = !isExpandMore.value }
                        if(isExpandMore.value){
                            Divider()
                        }
                        ContentItem(it, isExpandMore.value) { prompt -> navigateToHome(null, prompt) }
                    }
                }
            }

        }

    })
}

@Composable
fun ContentItem(prompts: Prompts, isExpanded: Boolean, onClick: (prompt: String) -> Unit) {
    if (isExpanded) {
        Column {
            prompts.prompts.forEachIndexed { index, prompt, ->
                Text(
                    text = prompt,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable(onClick = { onClick(prompt) })
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                )
                if(index != prompts.prompts.lastIndex){
                    Divider()
                }
            }
        }
    }
}

@Composable
fun HeaderItem(prompts: Prompts, isExpanded: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = { onClick() })
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(text = prompts.category.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(end = 8.dp))
            Text(
                text = prompts.category.descriptions,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 8.dp, top = 4.dp)
            )
        }
        IconButton(onClick = { onClick() }, modifier = Modifier.size(24.dp)) {
            Icon(
                if (isExpanded) Icons.Outlined.ExpandMore else Icons.Outlined.ExpandLess,
                if (isExpanded) stringResource(R.string.ic_drop_up_dec) else stringResource(R.string.ic_drop_down_desc)
            )
        }
    }
}


private fun logUserEntersEvent(smartAnalytics: SmartAnalytics) {
    val bundle = Bundle()
    bundle.putString(SmartAnalytics.Param.SCREEN_NAME, "prompt_screen")
    smartAnalytics.logEvent(SmartAnalytics.Event.USER_ON_SCREEN, bundle)
}

@Composable
fun ErrorView(message: String) {
    var showError by remember { mutableStateOf(false) }
    showError = message.isNotEmpty()
    val context = LocalContext.current
    if (showError) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

