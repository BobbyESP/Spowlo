package com.bobbyesp.appmodules.hub.ui.screens.dynamic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.bobbyesp.appmodules.core.utils.Log
import com.bobbyesp.appmodules.hub.ui.screens.AlbumPage
import com.bobbyesp.appmodules.hub.ui.screens.BlendCreateInvitationScreen
import com.bobbyesp.appmodules.hub.ui.screens.BrowseRadioPage
import com.bobbyesp.appmodules.hub.ui.screens.CollectionPage
import com.bobbyesp.appmodules.hub.ui.screens.GenrePage
import com.bobbyesp.appmodules.hub.ui.screens.LikedSongsPage
import com.bobbyesp.appmodules.hub.ui.screens.ListeningHistoryPage
import com.bobbyesp.appmodules.hub.ui.screens.PlaylistPage
import com.bobbyesp.appmodules.hub.ui.screens.PodcastShowPage
import com.bobbyesp.appmodules.hub.ui.screens.core.HubScreen
import com.bobbyesp.appmodules.hub.ui.screens.settings.ConfigPage

@Composable
fun DynamicSpotifyUriScreen(
    uri: String,
    fullUri: String,
    navController: NavHostController,
    onBackPressed : () -> Unit,
) {
    var uriSplits = uri.split(":")
    if (uriSplits[0] == "user" && uriSplits.size > 2) uriSplits = uriSplits.drop(2)
    val id = uriSplits.getOrElse(1) { "" }
    val argument = uriSplits.getOrElse(2) { "" }

    LaunchedEffect(key1 = true) {
        Log.d("DynamicSpotifyUriScreen", "uri: $uri")
        Log.d("DynamicSpotifyUriScreen", "fullUri: $fullUri")
        Log.d("DynamicSpotifyUriScreen", "id: $id")
        Log.d("DynamicSpotifyUriScreen", "argument: $argument")
    }

    when (uriSplits[0]) {
        uriCaseToString(UriCases.Genre) -> GenrePage(id, onBackPressed)

        uriCaseToString(UriCases.Artist) -> HubScreen(
            needContentPadding = false,
            loader = {
                if (argument == "releases") {
                    getReleasesView(id)
                } else {
                    getArtistView(id)
                }
            }
        )

        uriCaseToString(UriCases.Show) -> PodcastShowPage(id)
        uriCaseToString(UriCases.Album) -> AlbumPage(id)
        uriCaseToString(UriCases.Playlist) -> PlaylistPage(id)
        uriCaseToString(UriCases.Config) -> ConfigPage()
        uriCaseToString(UriCases.Radio) -> BrowseRadioPage()

        uriCaseToString(UriCases.Collection) -> when (id) {
            "artist" -> LikedSongsPage(
                id = argument,
                fullUri = fullUri
            )

            "" -> CollectionPage()
        }

        uriCaseToString(UriCases.Internal) -> when (id) {
            "listeninghistory" -> ListeningHistoryPage()
        }

        uriCaseToString(UriCases.Blend) -> when (id) {
            "invitation" -> BlendCreateInvitationScreen()
        }

        else -> {
            Box(Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    Text(fullUri)
                    Text(uriSplits.joinToString(":"))
                }
            }
        }

    }
}

fun uriCaseToString(uriCase: UriCases): String {
    return when (uriCase) {
        is UriCases.Genre -> "genre"
        is UriCases.Artist -> "artist"
        is UriCases.Show -> "show"
        is UriCases.Album -> "album"
        is UriCases.Playlist -> "playlist"
        is UriCases.Config -> "config"
        is UriCases.Radio -> "radio"
        is UriCases.Collection -> "collection"
        is UriCases.Internal -> "internal"
        is UriCases.Blend -> "blend"
    }
}

sealed class UriCases {
    object Genre : UriCases()
    object Artist : UriCases()
    object Show : UriCases()
    object Album : UriCases()
    object Playlist : UriCases()
    object Config : UriCases()
    object Radio : UriCases()
    object Collection : UriCases()
    object Internal : UriCases()
    object Blend : UriCases()
}