package com.bobbyesp.spowlo.ui.components.cards.songs.horizontal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.others.tags.ExplicitTag
import com.bobbyesp.spowlo.ui.components.others.tags.LyricsTag
import com.bobbyesp.spowlo.ui.components.text.MarqueeText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetadataEntityItem(
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    songName: String,
    artists: String,
    hasLyrics: Boolean = false,
    isExplicit: Boolean = false,
    isPlaylist: Boolean = false,
    imageUrl: String = "",
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    listIndex: Int? = null,
    onClick: () -> Unit = { }
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = surfaceColor,
        onClick = { onClick() },
    ) {
        Row(
            modifier = contentModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if(listIndex != null) {
                Text(
                    text = "${listIndex + 1}.",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isPlaylist && imageUrl.isNotEmpty()) {
                    AsyncImageImpl(
                        modifier = Modifier
                            .size(40.dp)
                            .aspectRatio(
                                1f, matchHeightConstraintsFirst = true
                            )
                            .clip(MaterialTheme.shapes.extraSmall),
                        model = imageUrl,
                        contentDescription = stringResource(id = R.string.track_artwork),
                        contentScale = ContentScale.Crop,

                        )
                }
                Column(
                    modifier = Modifier
                        .padding(start = if (isPlaylist) 6.dp else 0.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        MarqueeText(
                            text = songName,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            basicGradientColor = MaterialTheme.colorScheme.surface.copy(
                                alpha = 0.8f
                            ),
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (hasLyrics) LyricsTag(
                            modifier = Modifier.padding(
                                start = 6.dp,
                                end = if (isExplicit) 6.dp else 0.dp
                            )
                        )
                        if (isExplicit) ExplicitTag(if (hasLyrics) Modifier.padding(start = 6.dp) else Modifier.padding(end = 6.dp))
                        MarqueeText(
                            text = artists,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 12.sp,
                            basicGradientColor = MaterialTheme.colorScheme.surface.copy(
                                alpha = 0.8f
                            ),
                        )
                    }
                }
            }
            Box(modifier = Modifier) {
                IconButton(
                    onClick = onClick,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(id = R.string.more_options),
                        modifier = Modifier
                            .padding(6.dp)
                    )
                }
            }
        }
    }
}