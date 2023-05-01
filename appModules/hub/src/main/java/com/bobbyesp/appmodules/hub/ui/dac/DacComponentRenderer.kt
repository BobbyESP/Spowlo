package com.bobbyesp.appmodules.hub.ui.dac

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bobbyesp.appmodules.hub.BuildConfig
import com.bobbyesp.appmodules.hub.ui.components.PlaceholderType
import com.bobbyesp.appmodules.hub.ui.components.dac.SongsShortcutsGrid
import com.bobbyesp.appmodules.hub.ui.components.dac.actionCards.MediumActionCardBinder
import com.bobbyesp.appmodules.hub.ui.components.dac.actionCards.SmallActionCardBinder
import com.bobbyesp.appmodules.hub.ui.components.dac.recsplanation.RecsplanationHeadingComponentBinder
import com.bobbyesp.appmodules.hub.ui.components.dac.toolbars.ToolbarComponentV1Binder
import com.bobbyesp.appmodules.hub.ui.components.dac.toolbars.ToolbarComponentV2Binder
import com.google.protobuf.Message
import com.spotify.home.dac.component.v1.proto.AlbumCardActionsMediumComponent
import com.spotify.home.dac.component.v1.proto.AlbumCardActionsSmallComponent
import com.spotify.home.dac.component.v1.proto.ArtistCardActionsMediumComponent
import com.spotify.home.dac.component.v1.proto.ArtistCardActionsSmallComponent
import com.spotify.home.dac.component.v1.proto.PlaylistCardActionsMediumComponent
import com.spotify.home.dac.component.v1.proto.PlaylistCardActionsSmallComponent
import com.spotify.home.dac.component.v1.proto.RecsplanationHeadingComponent
import com.spotify.home.dac.component.v1.proto.ShortcutsSectionComponent
import com.spotify.home.dac.component.v1.proto.ToolbarComponent
import com.spotify.home.dac.component.v2.proto.ToolbarComponentV2

@Composable
fun DacComponentRenderer(
    item: Message,
    onNavigateToRequested: (String) -> Unit
) {
    when (item) {

        //////*Home page*//////
        //Top bars
        is ToolbarComponent -> ToolbarComponentV1Binder(item = item, onNavigateToRequested)
        is ToolbarComponentV2 -> ToolbarComponentV2Binder(item = item, onNavigateToRequested)
        //Song shortcuts
        is ShortcutsSectionComponent -> SongsShortcutsGrid(item)

        //New content by an artist
        is RecsplanationHeadingComponent -> RecsplanationHeadingComponentBinder(
            item,
            onNavigateToRequested
        )

        is AlbumCardActionsSmallComponent -> SmallActionCardBinder(
            title = item.title,
            subtitle = item.subtitle,
            navigateUri = item.navigateUri,
            likeUri = item.likeUri,
            imageUri = item.imageUri,
            imagePlaceholder = PlaceholderType.Album,
            playCommand = item.playCommand,
            onNavigateToUri = onNavigateToRequested
        )

        is ArtistCardActionsSmallComponent -> SmallActionCardBinder(
            title = item.title,
            subtitle = item.subtitle,
            navigateUri = item.navigateUri,
            likeUri = item.followUri,
            imageUri = item.imageUri,
            imagePlaceholder = PlaceholderType.Artist,
            playCommand = item.playCommand,
            onNavigateToUri = onNavigateToRequested
        )

        is PlaylistCardActionsSmallComponent -> SmallActionCardBinder(
            title = item.title,
            subtitle = item.subtitle,
            navigateUri = item.navigateUri,
            likeUri = item.likeUri,
            imageUri = item.imageUri,
            imagePlaceholder = PlaceholderType.Playlist,
            playCommand = item.playCommand,
            onNavigateToUri = onNavigateToRequested
        )

        is AlbumCardActionsMediumComponent -> MediumActionCardBinder(
            title = item.title,
            subtitle = item.description,
            navigateUri = item.navigateUri,
            likeUri = item.likeUri,
            imageUri = item.imageUri,
            imagePlaceholder = PlaceholderType.Album,
            playCommand = item.playCommand,
            contentType = item.contentType,
            fact = item.conciseFact,
            gradientColor = item.gradientColor,
            onNavigateToUri = onNavigateToRequested
        )

        is ArtistCardActionsMediumComponent -> MediumActionCardBinder(
            title = item.title,
            subtitle = item.description,
            navigateUri = item.navigateUri,
            likeUri = item.followUri,
            imageUri = item.imageUri,
            imagePlaceholder = PlaceholderType.Artist,
            playCommand = item.playCommand,
            contentType = item.contentType,
            fact = item.conciseFact,
            gradientColor = item.gradientColor,
            onNavigateToUri = onNavigateToRequested
        )

        is PlaylistCardActionsMediumComponent -> MediumActionCardBinder(
            title = item.title,
            subtitle = item.description,
            navigateUri = item.navigateUri,
            likeUri = item.likeUri,
            imageUri = item.imageUri,
            imagePlaceholder = PlaceholderType.Playlist,
            playCommand = item.playCommand,
            contentType = item.contentType,
            fact = item.conciseFact,
            gradientColor = item.gradientColor,
            onNavigateToUri = onNavigateToRequested
        )


        else -> {
            if (BuildConfig.DEBUG) {
                Text(
                    "DAC proto-known, but UI-unknown component: ${item::class.java.simpleName}\n\n${item}",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}