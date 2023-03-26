package com.bobbyesp.spowlo.ui.components.songs.metadata_viewer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.MarqueeText
import com.bobbyesp.spowlo.ui.components.songs.ExplicitIcon
import com.bobbyesp.spowlo.ui.components.songs.LyricsIcon
import com.bobbyesp.spowlo.ui.pages.settings.about.LocalAsset
import com.bobbyesp.spowlo.utils.ChromeCustomTabsUtil


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackComponent(
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    songName: String,
    artists: String,
    spotifyUrl: String,
    hasLyrics: Boolean = false,
    isExplicit: Boolean = false,
    onClick: () -> Unit = { ChromeCustomTabsUtil.openUrl(spotifyUrl) }
) {
    val clipboardManager = LocalClipboardManager.current
    val showDropdown = remember { mutableStateOf(false) }
    Column(
        modifier
            .fillMaxWidth()
            .clickable { onClick() }) {
        Row(
            modifier = contentModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically, //This makes all go to the center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(6.dp)
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
                    Spacer(Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        MarqueeText(
                            text = artists,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 10.sp,
                            basicGradientColor = MaterialTheme.colorScheme.surface.copy(
                                alpha = 0.8f
                            ),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        LyricsIcon(visible = hasLyrics)
                        Spacer(modifier = Modifier.width(6.dp))
                        ExplicitIcon(visible = isExplicit)
                    }
                }
            }
            Column {
                FilledTonalIconButton(onClick = {
                    showDropdown.value = !showDropdown.value
                },
                    modifier = Modifier.size(32.dp),
                    ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "More options button",
                        modifier = Modifier
                            .weight(0.1f)
                            .padding(6.dp)
                    )
                }

                DropdownMenu(
                    expanded = showDropdown.value,
                    onDismissRequest = { showDropdown.value = false },
                    properties = PopupProperties(
                        dismissOnClickOutside = true,
                        dismissOnBackPress = true,
                        focusable = true,
                    ),
                ) {
                    DropdownMenuItem(
                        onClick = onClick,
                        text = {
                            Text(text = stringResource(id = R.string.download))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = "Download icon",
                            )
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = R.string.open_in_spotify))
                        }, onClick = {
                            ChromeCustomTabsUtil.openUrl(spotifyUrl)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = LocalAsset(id = R.drawable.spotify_logo),
                                contentDescription = "Spotify logo",
                            )
                        }
                    )

                    DropdownMenuItem(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(spotifyUrl))
                        },
                        text = {
                            Text(text = stringResource(id = R.string.copy_link))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = "Copy link icon",
                            )
                        }
                    )
                }
            }
        }
    }
}