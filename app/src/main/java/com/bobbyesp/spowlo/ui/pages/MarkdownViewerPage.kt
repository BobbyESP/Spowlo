package com.bobbyesp.spowlo.ui.pages

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.BackButton
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownViewerPage(
    markdownFileName: String,
    onBackPressed: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    //Read markdown file from the "raw" folder following the name of the file
    val markdownText: String

    when (markdownFileName) {
        "index.md" -> markdownText = readMarkdownFile(LocalContext.current, R.raw.index)
        "cli_commands.md" -> markdownText = readMarkdownFile(LocalContext.current, R.raw.cli_commands)
        else -> markdownText = readMarkdownFile(LocalContext.current, R.raw.index)
    }

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(title = {
                Text(
                    text = stringResource(id = R.string.markdown_viewer),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                )
            }, navigationIcon = {
                BackButton { onBackPressed() }
            }, actions = {
            }, scrollBehavior = scrollBehavior
            )
        }) { paddings ->
        MarkdownText(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
                .padding(6.dp),
            markdown = markdownText,
            textAlign = TextAlign.Justify,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

fun readMarkdownFile(context: Context, resourceId: Int): String {
    val inputStream = context.resources.openRawResource(resourceId)
    return inputStream.bufferedReader().use { it.readText() }
}