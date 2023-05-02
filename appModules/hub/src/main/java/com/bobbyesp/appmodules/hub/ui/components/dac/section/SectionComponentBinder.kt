package com.bobbyesp.appmodules.hub.ui.components.dac.section

import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.appmodules.core.ext.dynamicUnpack
import com.bobbyesp.appmodules.hub.ui.components.PlaceholderType
import com.bobbyesp.appmodules.hub.ui.components.PreviewableAsyncImage
import com.spotify.home.dac.component.v1.proto.AlbumCardMediumComponent
import com.spotify.home.dac.component.v1.proto.ArtistCardMediumComponent
import com.spotify.home.dac.component.v1.proto.EpisodeCardMediumComponent
import com.spotify.home.dac.component.v1.proto.PlaylistCardMediumComponent
import com.spotify.home.dac.component.v1.proto.SectionComponent
import com.spotify.home.dac.component.v1.proto.ShowCardMediumComponent
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.SnapOffsets
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun SectionComponentBinder(
    item: SectionComponent,
    onNavigateToUri: (String) -> Unit
) {
    val list = item.componentsList.map { it.dynamicUnpack() }
    val lazyListState = rememberLazyListState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                3.dp
            )
        ),
        shape = MaterialTheme.shapes.medium.copy(
            bottomStart = MaterialTheme.shapes.medium.bottomStart,
            bottomEnd = MaterialTheme.shapes.medium.bottomEnd,
            topEnd = CornerSize(0.dp),
            topStart = CornerSize(0.dp)
        ),
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            state = lazyListState,
            flingBehavior = rememberSnapperFlingBehavior(
                lazyListState,
                snapOffsetForItem = SnapOffsets.Start,
                decayAnimationSpec = rememberSplineBasedDecay(),
                springAnimationSpec = spring(0.2f, 20f)
            )
        ) {
            items(list) { listItem ->
                when (listItem) {
                    is AlbumCardMediumComponent -> MediumCard(
                        title = listItem.title,
                        subtitle = listItem.subtitle,
                        navigateUri = listItem.navigateUri,
                        imageUri = listItem.imageUri,
                        imagePlaceholder = PlaceholderType.Album,
                        onNavigateToUri
                    )

                    is PlaylistCardMediumComponent -> MediumCard(
                        title = listItem.title,
                        subtitle = listItem.subtitle,
                        navigateUri = listItem.navigateUri,
                        imageUri = listItem.imageUri,
                        imagePlaceholder = PlaceholderType.Playlist,
                        onNavigateToUri
                    )

                    is ArtistCardMediumComponent -> MediumArtistCard(
                        title = listItem.title,
                        subtitle = listItem.subtitle,
                        navigateUri = listItem.navigateUri,
                        imageUri = listItem.imageUri,
                        imagePlaceholder = PlaceholderType.Artist,
                        onNavigateToUri
                    )

                    is EpisodeCardMediumComponent -> MediumCard(
                        title = listItem.title,
                        subtitle = listItem.subtitle,
                        navigateUri = listItem.navigateUri,
                        imageUri = listItem.imageUri,
                        imagePlaceholder = PlaceholderType.Podcasts,
                        onNavigateToUri
                    )

                    is ShowCardMediumComponent -> MediumCard(
                        title = listItem.title,
                        subtitle = listItem.subtitle,
                        navigateUri = listItem.navigateUri,
                        imageUri = listItem.imageUri,
                        imagePlaceholder = PlaceholderType.Podcasts,
                        onNavigateToUri
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MediumCard(
    title: String,
    subtitle: String,
    navigateUri: String,
    imageUri: String,
    imagePlaceholder: PlaceholderType,
    onNavigateToUri: (String) -> Unit
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                3.dp
            )
        ), modifier = Modifier
            .height(256.dp)
            .width(172.dp)
            .padding(vertical = 12.dp),
        onClick = {
            onNavigateToUri(navigateUri)
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PreviewableAsyncImage(
                    imageUrl = imageUri,
                    placeholderType = imagePlaceholder,
                    modifier = Modifier
                        .size(142.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            }
            Spacer(modifier = Modifier.padding(bottom = 8.dp))
            Column(
                modifier = Modifier.padding(start = 6.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                if (title.isNotEmpty()) {
                    Column(
                        Modifier
                    ) {
                        Text(
                            title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 20.sp,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                if (subtitle.isNotEmpty()) {
                    Text(
                        subtitle,
                        modifier = Modifier
                            .padding(top = if (title.isNotEmpty()) 6.dp else 0.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.bodySmall,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}

@Composable
fun MediumArtistCard(
    title: String,
    subtitle: String,
    navigateUri: String,
    imageUri: String,
    imagePlaceholder: PlaceholderType,
    onNavigateToUri: (String) -> Unit
) {
    Box(
        Modifier
            .clip(RoundedCornerShape(28.dp))
            .clickable { onNavigateToUri(navigateUri) }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .width(172.dp)
                .height(258.dp)
                .padding(bottom = 4.dp)
        ) {
            // Had to wrap the image in another composable due to weird padding when
            // image couldn't be retrieved
            Box(
                Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) {
                PreviewableAsyncImage(
                    imageUrl = imageUri,
                    placeholderType = imagePlaceholder,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                Modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (title.isNotEmpty()) {
                    Text(
                        title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp,
                        style = TextStyle().plus(MaterialTheme.typography.labelLarge),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}