package com.bobbyesp.appmodules.hub.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bobbyesp.appmodules.core.objects.ui_components.UiComponent
import com.bobbyesp.appmodules.core.objects.ui_components.UiItem
import com.bobbyesp.appmodules.hub.BuildConfig
import com.bobbyesp.appmodules.hub.ui.components.dac.HomeSectionHeader

@Composable
fun UIBinder(
    item: UiItem,
    isRenderingInGrid: Boolean = false,
) {
    when (item.component) {
        UiComponent.HomeShortSectionHeader -> HomeSectionHeader(item.text!!)
       // UiComponent.HomeLargeSectionHeader -> HomeSectionLargeHeader(item, onUiItemClick)
        /*UiComponent.GlueSectionHeader -> SectionHeader(item.text!!)
        UiComponent.ShortcutsContainer -> ShortcutsContainer(item.children!!)
        UiComponent.ShortcutsCard -> ShortcutsCard(item)
        UiComponent.FindCard -> FindCard(item)

        UiComponent.SingleFocusCard -> SingleFocusCard(item)

        UiComponent.Carousel -> Carousel(item)

        UiComponent.MediumCard -> {
            if (isRenderingInGrid) {
                GridMediumCard(item)
            } else {
                MediumCard(item)
            }
        }

        UiComponent.ArtistLikedSongs -> LikedSongsRow(item)

        UiComponent.AlbumTrackRow -> AlbumTrackRow(item)
        UiComponent.ArtistTrackRow -> ArtistTrackRow(item)
        UiComponent.PlaylistTrackRow -> PlaylistTrackRow(item)

        UiComponent.ArtistPinnedItem -> ArtistPinnedItem(item)
        UiComponent.AlbumHeader -> AlbumHeader(item)
        UiComponent.ArtistHeader -> ArtistHeader(item)
        UiComponent.LargerRow -> LargerRow(item)

        UiComponent.PlaylistHeader -> PlaylistHeader(item)
        UiComponent.LargePlaylistHeader -> LargePlaylistHeader(item)
        UiComponent.CollectionHeader -> CollectionHeader(item)

        UiComponent.TextRow -> TextRow(item.text!!)
        UiComponent.ImageRow -> ImageRow(item)

        UiComponent.ShowHeader -> ShowHeader(item)
        UiComponent.EpisodeListItem -> EpisodeListItem(item)
        UiComponent.PodcastTopics -> PodcastTopicsStrip(item)

        UiComponent.OutlinedButton -> OutlineButton(item)

        UiComponent.HistoryPlaylist -> PlaylistTrackRowLarger(item)*/
        UiComponent.HistoryDivider -> Divider(modifier = Modifier.padding(start = 14.dp, end = 14.dp).height(2.dp))
        else -> {
            if (BuildConfig.DEBUG){
                Text("Unsupported, id = ${item.id}")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}