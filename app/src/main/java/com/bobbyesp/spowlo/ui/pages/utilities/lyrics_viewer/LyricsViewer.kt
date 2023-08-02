package com.bobbyesp.spowlo.ui.pages.utilities.lyrics_viewer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.bobbyesp.spowlo.utils.lyrics.LyricsUtil.parseTimeFromLine
import kotlinx.coroutines.delay

@Composable
fun LyricsViewer(
    lyrics: List<String>,
    startTime: Long, // Starting time in milliseconds (current song progress)
    synchronized: Boolean, // Indicates whether lyrics should be synchronized or not
    onLyricClick: (String) -> Unit // Action to perform when a lyric is clicked (only if synchronized)
) {
    var currentLineIndex by remember { mutableIntStateOf(0) }

    // LaunchedEffect that runs whenever the currentLineIndex changes
    LaunchedEffect(currentLineIndex, synchronized) {
        if (synchronized) {
            while (currentLineIndex < lyrics.size - 1) {
                // Get the time of the current and next lyric
                val currentTime = parseTimeFromLine(lyrics[currentLineIndex])
                val nextTime = parseTimeFromLine(lyrics[currentLineIndex + 1])

                // Calculate the current progress time in milliseconds based on the start time
                val progress = System.currentTimeMillis() - startTime

                // Check if the current progress time is between the time of the current and next lyric
                if (progress in currentTime until nextTime) {
                    // Update the currentLineIndex
                    currentLineIndex++
                }

                // Wait for a short period before checking the time again
                delay(200)
            }
        }
    }

    LazyColumn {
        itemsIndexed(lyrics) { index, line ->
            val isActive = index == currentLineIndex
            val textColor = if (isActive) Color.Red else Color.Black

            // Add the clickable modifier if lyrics are synchronized
            val modifier = if (synchronized) {
                Modifier.clickable {
                    onLyricClick(line)
                }
            } else {
                Modifier
            }

            Text(
                text = line,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(modifier)
            )
        }
    }
}

