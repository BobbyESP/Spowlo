package com.bobbyesp.appmodules.hub.ui.components.dac

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.appmodules.core.ext.dynamicUnpack
import com.bobbyesp.appmodules.hub.ui.components.PlaceholderType
import com.bobbyesp.appmodules.hub.ui.components.PreviewableAsyncImage
import com.spotify.home.dac.component.v1.proto.AlbumCardShortcutComponent
import com.spotify.home.dac.component.v1.proto.ArtistCardShortcutComponent
import com.spotify.home.dac.component.v1.proto.EpisodeCardShortcutComponent
import com.spotify.home.dac.component.v1.proto.PlaylistCardShortcutComponent
import com.spotify.home.dac.component.v1.proto.ShortcutsSectionComponent
import com.spotify.home.dac.component.v1.proto.ShowCardShortcutComponent

@Composable
fun SongsShortcutsGrid(
    item: ShortcutsSectionComponent
) {
    item.shortcutsList.map { it.dynamicUnpack() }.chunked(2).forEachIndexed { idx, pairs ->
        Row(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = if (idx != item.shortcutsList.lastIndex / 2) 8.dp else 0.dp)
        ) {
            pairs.forEachIndexed { xIdx, xItem ->
                Box(
                    Modifier
                        .weight(1f)
                        .padding(end = if (xIdx == 0) 8.dp else 0.dp)
                ) {
                    when (xItem) {
                        is AlbumCardShortcutComponent -> ShortcutComponentBinder(
                            xItem.navigateUri, xItem.imageUri, PlaceholderType.Album, xItem.title
                        )

                        is PlaylistCardShortcutComponent -> ShortcutComponentBinder(
                            xItem.navigateUri, xItem.imageUri, PlaceholderType.Playlist, xItem.title
                        )

                        is ShowCardShortcutComponent -> ShortcutComponentBinder(
                            xItem.navigateUri, xItem.imageUri, PlaceholderType.Podcasts, xItem.title
                        )

                        is ArtistCardShortcutComponent -> ShortcutComponentBinder(
                            xItem.navigateUri, xItem.imageUri, PlaceholderType.Artist, xItem.title
                        )

                        is EpisodeCardShortcutComponent -> ShortcutComponentBinder(
                            xItem.navigateUri, xItem.imageUri, PlaceholderType.Podcasts, xItem.title
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShortcutComponentBinder(
    navigateUri: String, imageUrl: String, imagePlaceholder: PlaceholderType, title: String
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                6.dp
            )
        ),
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp, pressedElevation = 4.dp, disabledElevation = 0.dp
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Row(modifier = Modifier.clickable {
/*TODO: Make it clickable to navigate*/
        }) {
            PreviewableAsyncImage(
                imageUrl = imageUrl,
                placeholderType = imagePlaceholder,
                modifier = Modifier.size(56.dp)
            )
            Text(
                title,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(
                        Alignment.CenterVertically
                    )
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                fontWeight =  FontWeight.SemiBold
            )
        }
    }
}