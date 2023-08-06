package com.bobbyesp.spowlo.ui.components.cards.songs.horizontal

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import com.adamratzman.spotify.models.Artist
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.images.PlaceholderCreator

@Composable
fun ArtistHorizontalCard(
    modifier: Modifier = Modifier,
    artist: Artist,
    backgroundColor: Color = Color.Transparent,
    onClick: () -> Unit
) {
    val imageSize = 52.dp
    val profilePic = artist.images.firstOrNull()?.url ?: ""

    var showImage by remember {
        mutableStateOf(true)
    }

    Surface(
        modifier = modifier,
        onClick = onClick,
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showImage) {
                AsyncImageImpl(
                    modifier = Modifier
                        .size(imageSize)
                        .aspectRatio(
                            1f, matchHeightConstraintsFirst = true
                        )
                        .clip(CircleShape),
                    model = profilePic,
                    contentDescription = stringResource(id = R.string.track_artwork),
                    onState = { state ->
                        //if it was successful, don't show the placeholder, else show it
                        showImage =
                            state !is AsyncImagePainter.State.Error && state !is AsyncImagePainter.State.Empty
                    },
                    contentScale = ContentScale.FillBounds,
                )
            } else {
                PlaceholderCreator(
                    modifier = Modifier
                        .size(imageSize)
                        .clip(CircleShape),
                    icon = Icons.Default.Person,
                    colorful = false
                )
            }
            Text(
                text = artist.name,
                modifier = Modifier.padding(start = if(showImage) 12.dp else 16.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}