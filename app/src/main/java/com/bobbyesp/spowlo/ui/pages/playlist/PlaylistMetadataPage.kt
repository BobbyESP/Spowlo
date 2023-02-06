package com.bobbyesp.spowlo.ui.pages.playlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.library.dto.Song
import com.bobbyesp.spowlo.Downloader
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.songs.PlaylistHeaderItem
import com.bobbyesp.spowlo.ui.components.songs.SongMetadataCard
import com.bobbyesp.spowlo.ui.pages.downloader.DownloaderViewModel
import com.bobbyesp.spowlo.utils.DownloaderUtil

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun PlaylistMetadataPage(onBackPressed: () -> Unit) {

    //val songs = downloaderViewModel.songInfoFlow.collectAsStateWithLifecycle()
    val songs = DownloaderUtil.songsState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(title = {
                Text(
                    text = if (songs.value.size == 1) stringResource(id = R.string.song_metadata) else stringResource(
                        R.string.playlist_metadata
                    ),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                )
            }, navigationIcon = {
                BackButton { onBackPressed() }
            }, actions = {
            }, scrollBehavior = scrollBehavior
            )
        }) { paddings ->
        LazyColumn(
            modifier = Modifier.padding(paddings), contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                if(songs.value[0].song_list != null){
                    PlaylistHeaderItem(playlist = songs.value[0], modifier = Modifier.padding())
                }
            }
            items(songs.value.size) { index ->
                SongMetadataCard(song = songs.value[index])
                Spacer(modifier = Modifier.padding(10.dp))
            }

        }
    }
}