package com.bobbyesp.spowlo.ui.pages.settings.downloader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Cached
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Filter
import androidx.compose.material.icons.outlined.HighQuality
import androidx.compose.material.icons.outlined.Lyrics
import androidx.compose.material.icons.outlined.MoneyOff
import androidx.compose.material.icons.outlined.Output
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShuffleOn
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.intState
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.LargeTopAppBar
import com.bobbyesp.spowlo.ui.components.PreferenceSubtitle
import com.bobbyesp.spowlo.ui.components.settings.ElevatedSettingsCard
import com.bobbyesp.spowlo.ui.components.settings.SettingsItemNew
import com.bobbyesp.spowlo.ui.components.settings.SettingsSwitch
import com.bobbyesp.spowlo.ui.icons.YouTubeMusic
import com.bobbyesp.spowlo.utils.COMMA_ARTISTS_SEPARATOR
import com.bobbyesp.spowlo.utils.DONT_FILTER_RESULTS
import com.bobbyesp.spowlo.utils.DOWNLOAD_LYRICS
import com.bobbyesp.spowlo.utils.GENERATE_LRC
import com.bobbyesp.spowlo.utils.ONLY_VERIFIED_RESULTS
import com.bobbyesp.spowlo.utils.ORIGINAL_AUDIO
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.PreferencesUtil.updateInt
import com.bobbyesp.spowlo.utils.SKIP_ALBUM_ART
import com.bobbyesp.spowlo.utils.SKIP_EXPLICIT
import com.bobbyesp.spowlo.utils.SPLIT_BY_MAIN_ARTIST
import com.bobbyesp.spowlo.utils.SPLIT_BY_PLAYLIST
import com.bobbyesp.spowlo.utils.SPONSORBLOCK
import com.bobbyesp.spowlo.utils.THREADS
import com.bobbyesp.spowlo.utils.USE_CACHING
import com.bobbyesp.spowlo.utils.USE_YT_METADATA
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun DownloaderSettingsPage(
    onBackPressed: () -> Unit,
) {

    val threadsNumber = THREADS.intState

    var useCache by remember {
        mutableStateOf(
            PreferencesUtil.getValue(USE_CACHING)
        )
    }

    var dontFilter by remember {
        mutableStateOf(
            PreferencesUtil.getValue(DONT_FILTER_RESULTS)
        )
    }

    var splitByPlaylist by remember {
        mutableStateOf(
            PreferencesUtil.getValue(SPLIT_BY_PLAYLIST)
        )
    }

    var splitByMainArtist by remember {
        mutableStateOf(
            PreferencesUtil.getValue(SPLIT_BY_MAIN_ARTIST)
        )
    }

    var commaArtistsSeparator by remember {
        mutableStateOf(
            PreferencesUtil.getValue(COMMA_ARTISTS_SEPARATOR)
        )
    }

    var downloadLyrics by remember {
        mutableStateOf(
            PreferencesUtil.getValue(DOWNLOAD_LYRICS)
        )
    }

    var audioFormat by remember { mutableStateOf(PreferencesUtil.getAudioFormatDesc()) }
    var audioQuality by remember { mutableStateOf(PreferencesUtil.getAudioQualityDesc()) }
    var preserveOriginalAudio by remember { mutableStateOf(PreferencesUtil.getValue(ORIGINAL_AUDIO)) }

    var useSponsorBlock by remember {
        mutableStateOf(
            PreferencesUtil.getValue(SPONSORBLOCK)
        )
    }

    var onlyVerifiedResults by remember {
        mutableStateOf(
            PreferencesUtil.getValue(ONLY_VERIFIED_RESULTS)
        )
    }

    var useYTMedata by remember {
        mutableStateOf(
            PreferencesUtil.getValue(USE_YT_METADATA)
        )
    }

    var skipExplicit by remember {
        mutableStateOf(
            PreferencesUtil.getValue(SKIP_EXPLICIT)
        )
    }

    var generateLRC by remember {
        mutableStateOf(
            PreferencesUtil.getValue(GENERATE_LRC)
        )
    }

    var skipAlbumArt by remember {
        mutableStateOf(
            PreferencesUtil.getValue(SKIP_ALBUM_ART)
        )
    }

    var showAudioFormatDialog by remember { mutableStateOf(false) }
    var showAudioQualityDialog by remember { mutableStateOf(false) }
    var showAudioProviderDialog by remember { mutableStateOf(false) }
    var showAudioLyricDialog by remember { mutableStateOf(false) }
    var showOutputFormatDialog by remember { mutableStateOf(false) }

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState(),
            canScroll = { true })

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.downloader),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    BackButton { onBackPressed() }
                },
                scrollBehavior = scrollBehavior
            )
        },
        content = {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                item {
                    PreferenceSubtitle(text = stringResource(id = R.string.general))
                }
                item {
                    SettingsSwitch(
                        onCheckedChange = {
                            useCache = !useCache
                            PreferencesUtil.updateValue(USE_CACHING, useCache)
                        },
                        checked = useCache,
                        title = {
                            Text(
                                text = stringResource(id = R.string.use_cache),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        icon = Icons.Outlined.Cached,
                        description = { Text(text = stringResource(id = R.string.use_cache_desc)) },
                        modifier = Modifier.clip(
                            RoundedCornerShape(
                                topStart = 8.dp, topEnd = 8.dp
                            )
                        ),
                    )

                }

                /*item {
                    SettingsSwitch(
                        onCheckedChange = {
                            splitByPlaylist = !splitByPlaylist
                            PreferencesUtil.updateValue(SPLIT_BY_PLAYLIST, splitByPlaylist)
                        },
                        checked = splitByPlaylist,
                        title = {
                            Text(
                                text = stringResource(id = R.string.split_playlist),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        icon = Icons.Outlined.Splitscreen,
                        description = { Text(text = stringResource(id = R.string.split_playlist_desc)) },
                        clipCorners = false
                    )
                }

                item {
                    SettingsSwitch(
                        onCheckedChange = {
                            splitByMainArtist = !splitByMainArtist
                            PreferencesUtil.updateValue(SPLIT_BY_MAIN_ARTIST, splitByMainArtist)
                        },
                        checked = splitByMainArtist,
                        title = {
                            Text(
                                text = stringResource(id = R.string.split_by_main_artist),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        icon = Artist,
                        description = { Text(text = stringResource(id = R.string.split_by_main_artist_desc)) },
                        clipCorners = false,
                    )
                }*/

                item {
                    SettingsSwitch(
                        onCheckedChange = {
                            commaArtistsSeparator = !commaArtistsSeparator
                            PreferencesUtil.updateValue(COMMA_ARTISTS_SEPARATOR, commaArtistsSeparator)
                        },
                        checked = commaArtistsSeparator,
                        title = {
                            Text(
                                text = stringResource(id = R.string.comma_artists_separator),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        icon = Icons.Outlined.Person    ,
                        description = { Text(text = stringResource(id = R.string.comma_artists_separator_desc)) },
                        clipCorners = false,
                    )
                }

                item {
                    SettingsItemNew(
                        title = {
                            Text(
                                text = stringResource(id = R.string.output_format),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        description = { Text(text = stringResource(id = R.string.output_format_desc)) },
                        icon = Icons.Outlined.Output,
                        onClick = { showOutputFormatDialog = true },
                        modifier = Modifier.clip(
                            RoundedCornerShape(
                                bottomStart = 8.dp, bottomEnd = 8.dp
                            )
                        ),
                    )
                }

                item {
                    PreferenceSubtitle(text = stringResource(id = R.string.audio))
                }
                item {
                    SettingsSwitch(
                        onCheckedChange = {
                            preserveOriginalAudio = !preserveOriginalAudio
                            PreferencesUtil.updateValue(ORIGINAL_AUDIO, preserveOriginalAudio)
                        },
                        checked = preserveOriginalAudio,
                        title = {
                            Text(
                                text = stringResource(id = R.string.preserve_original_audio),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        description = { Text(text = stringResource(id = R.string.preserve_original_audio_desc)) },
                        icon = Icons.Outlined.Audiotrack,
                        modifier = Modifier.clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    )
                }
                item {
                    if (!preserveOriginalAudio) {
                        SettingsItemNew(
                            title = {
                                Text(
                                    text = stringResource(id = R.string.audio_quality),
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            description = { Text(text = audioQuality) },
                            icon = Icons.Outlined.HighQuality,
                            onClick = { showAudioQualityDialog = true },
                            enabled = !preserveOriginalAudio,
                        )
                    }
                }
                item {
                    SettingsItemNew(title = {
                        Text(
                            text = stringResource(id = R.string.audio_format),
                            fontWeight = FontWeight.Bold
                        )
                    },
                        description = { Text(text = audioFormat) },
                        icon = Icons.Outlined.AudioFile,
                        onClick = { showAudioFormatDialog = true })
                }
                item {
                    SettingsItemNew(
                        title = {
                            Text(
                                text = stringResource(id = R.string.audio_provider),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        description = { Text(text = stringResource(id = R.string.audio_provider_desc)) },
                        icon = Icons.Outlined.ShuffleOn,
                        onClick = { showAudioProviderDialog = true },
                    )
                }
                item {
                    SettingsSwitch(
                        onCheckedChange = {
                            downloadLyrics = !downloadLyrics
                            PreferencesUtil.updateValue(DOWNLOAD_LYRICS, downloadLyrics)
                        },
                        checked = downloadLyrics,
                        title = {
                            Text(
                                text = stringResource(id = R.string.download_lyrics),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        description = { Text(text = stringResource(id = R.string.download_lyrics_desc)) },
                        icon = Icons.Outlined.Download,
                        modifier = if (!downloadLyrics) Modifier.clip(
                            RoundedCornerShape(
                                bottomStart = 8.dp, bottomEnd = 8.dp
                            )
                        ) else Modifier
                    )
                }
                item {
                    if (downloadLyrics) {
                        SettingsItemNew(
                            title = {
                                Text(
                                    text = stringResource(id = R.string.lyric_providers),
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            description = { Text(text = stringResource(id = R.string.lyric_providers_desc)) },
                            icon = Icons.Outlined.Lyrics,
                            onClick = { showAudioLyricDialog = true },
                            enabled = downloadLyrics,
                            modifier = Modifier.clip(
                                RoundedCornerShape(
                                    bottomStart = 8.dp, bottomEnd = 8.dp
                                )
                            ),
                        )
                    }
                }
                item {
                    PreferenceSubtitle(text = stringResource(id = R.string.advanced_features))
                }
                //threads number item with a slicer
                item {
                    ElevatedSettingsCard(
                        shape = RoundedCornerShape(
                            bottomStart = 0.dp, bottomEnd = 0.dp,
                            topStart = 8.dp, topEnd = 8.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.threads),
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .padding(start = 16.dp, top = 16.dp)
                                                .weight(1f)
                                        )
                                        Text(
                                            text = stringResource(id = R.string.threads_number) + ": " + threadsNumber.intValue.toString(),
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.6f
                                                )
                                            ),
                                            modifier = Modifier.padding(end = 16.dp, top = 16.dp)
                                        )
                                    }
                                    Text(
                                        text = stringResource(id = R.string.threads_number_desc),
                                        modifier = Modifier.padding(
                                            vertical = 12.dp, horizontal = 16.dp
                                        ),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    )

                                }
                            }
                            Slider(
                                value = threadsNumber.intValue.toFloat(),
                                onValueChange = {
                                    threadsNumber.intValue = it.toInt()
                                    THREADS.updateInt(it.toInt())
                                },
                                valueRange = 1f..10f,
                                steps = 9,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
                item {
                    SettingsSwitch(
                        onCheckedChange = {
                            useSponsorBlock = !useSponsorBlock
                            PreferencesUtil.updateValue(SPONSORBLOCK, useSponsorBlock)
                        },
                        checked = useSponsorBlock,
                        title = {
                            Text(
                                text = stringResource(id = R.string.sponsorblock),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        icon = Icons.Outlined.MoneyOff,
                        description = { Text(text = stringResource(id = R.string.sponsorblock_desc)) },
                        clipCorners = false,
                    )
                }
                item {
                    SettingsSwitch(
                        onCheckedChange = {
                            onlyVerifiedResults = !onlyVerifiedResults
                            PreferencesUtil.updateValue(ONLY_VERIFIED_RESULTS, onlyVerifiedResults)
                        },
                        checked = onlyVerifiedResults,
                        title = {
                            Text(
                                text = stringResource(id = R.string.only_verified_results),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        icon = Icons.Outlined.Verified,
                        description = { Text(text = stringResource(id = R.string.only_verified_results_desc)) },
                        clipCorners = false,
                    )
                }
                item {
                    SettingsSwitch(
                        onCheckedChange = {
                            useYTMedata = !useYTMedata
                            PreferencesUtil.updateValue(USE_YT_METADATA, useYTMedata)
                        },
                        checked = useYTMedata,
                        title = {
                            Text(
                                text = stringResource(id = R.string.use_yt_metadata),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        icon = YouTubeMusic,
                        description = { Text(text = stringResource(id = R.string.use_yt_metadata_desc)) },
                        clipCorners = false,
                    )
                }
                item {
                    SettingsSwitch(
                        onCheckedChange = {
                            skipExplicit = !skipExplicit
                            PreferencesUtil.updateValue(SKIP_EXPLICIT, skipExplicit)
                        },
                        checked = skipExplicit,
                        title = {
                            Text(
                                text = stringResource(id = R.string.skip_explict),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        icon = Icons.Outlined.SkipNext,
                        description = { Text(text = stringResource(id = R.string.skip_explict_desc)) },
                        clipCorners = false,
                    )
                }
                item {
                    SettingsSwitch(
                        onCheckedChange = {
                            generateLRC = !generateLRC
                            PreferencesUtil.updateValue(GENERATE_LRC, generateLRC)
                        },
                        checked = generateLRC,
                        title = {
                            Text(
                                text = stringResource(id = R.string.generate_lrc),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        icon = Icons.AutoMirrored.Outlined.Article,
                        description = { Text(text = stringResource(id = R.string.generate_lrc_desc)) },
                        clipCorners = false,
                    )
                }
                item {
                    SettingsSwitch(
                        onCheckedChange = {
                            skipAlbumArt = !skipAlbumArt
                            PreferencesUtil.updateValue(SKIP_ALBUM_ART, skipAlbumArt)
                        },
                        checked = skipAlbumArt,
                        title = {
                            Text(
                                text = stringResource(id = R.string.skip_album_art),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        icon = Icons.Outlined.Album,
                        description = { Text(text = stringResource(id = R.string.skip_album_art_desc)) },
                        clipCorners = false,
                        modifier = Modifier.clip(
                            RoundedCornerShape(
                                bottomStart = 8.dp, bottomEnd = 8.dp
                            )
                        ),
                    )
                }
                item {
                    PreferenceSubtitle(text = stringResource(id = R.string.experimental_features))
                }
                item {
                    ElevatedSettingsCard {
                        SettingsSwitch(
                            onCheckedChange = {
                                dontFilter = !dontFilter
                                PreferencesUtil.updateValue(DONT_FILTER_RESULTS, dontFilter)
                            },
                            checked = dontFilter,
                            title = {
                                Text(
                                    text = stringResource(id = R.string.dont_filter_results),
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            icon = Icons.Outlined.Filter,
                            description = { Text(text = stringResource(id = R.string.dont_filter_results_desc)) },
                        )
                    }
                }
            }
        }
    )
    if (showAudioFormatDialog) {
        AudioFormatDialog(onDismissRequest = { showAudioFormatDialog = false }) {
            audioFormat = PreferencesUtil.getAudioFormatDesc()
        }
    }
    if (showAudioQualityDialog) {
        AudioQualityDialog(onDismissRequest = { showAudioQualityDialog = false }) {
            audioQuality = PreferencesUtil.getAudioQualityDesc()
        }
    }
    if (showAudioProviderDialog) {
        AudioProviderDialog(onDismissRequest = { showAudioProviderDialog = false })
    }
    if (showAudioLyricDialog) {
        LyricProviderDialog(onDismissRequest = { showAudioLyricDialog = false })
    }
    if (showOutputFormatDialog) {
        OutputFormatDialog(onDismissRequest = { showOutputFormatDialog = false })
    }
}