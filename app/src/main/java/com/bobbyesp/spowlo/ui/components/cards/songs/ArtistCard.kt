package com.bobbyesp.spowlo.ui.components.cards.songs

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.ArtistUri
import com.adamratzman.spotify.models.Followers
import com.adamratzman.spotify.models.SpotifyImage
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.images.PlaceholderCreator
import com.bobbyesp.spowlo.ui.components.text.MarqueeText
import com.bobbyesp.spowlo.ui.ext.thirdOrNull
import com.bobbyesp.spowlo.ui.theme.SpowloTheme

@Composable
fun ArtistCard(
    modifier: Modifier = Modifier,
    artist: Artist,
    onClick: () -> Unit
) {
    var showArtwork by remember { mutableStateOf(true) }
    val artistImage = artist.images.thirdOrNull()?.url

    Surface(
        modifier = modifier
            .clip(MaterialTheme.shapes.small),
        onClick = onClick
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (artistImage != null && showArtwork) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AsyncImageImpl(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        model = artistImage,
                        onState = { state ->
                            //if it was successful, don't show the placeholder, else show it
                            showArtwork =
                                state !is AsyncImagePainter.State.Error && state !is AsyncImagePainter.State.Empty
                        },
                        contentDescription = "Song cover",
                        contentScale = ContentScale.FillWidth,
                        isPreview = false
                    )
                }
            } else {
                PlaceholderCreator(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f),
                    icon = Icons.Default.Person,
                    colorful = false,
                    contentDescription = "Song cover"
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                MarqueeText(
                    text = artist.name ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ArtistCardPreview() {
    val externalUrlsString: Map<String, String> = mapOf(
        "spotify" to "https://open.spotify.com/artist/12345678",
        "website" to "https://www.example.com"
    )

    val href: String = "https://api.spotify.com/v1/artists/12345678"

    val id: String = "12345678"

    val uri: ArtistUri = ArtistUri("spotify:artist:12345678")

    val followers: Followers = Followers(total = 1000)

    val genres: List<String> = listOf("Pop", "Rock", "Electronic")

    val images: List<SpotifyImage> = listOf(
        SpotifyImage(640.00, "https://example.com/image1.jpg", 64.00),
        SpotifyImage(300.00, "https://example.com/image2.jpg", 300.00),
        SpotifyImage(64.00, "https://example.com/image3.jpg", 64.00)
    )

    val name: String = "Fake Artist"

    val popularity: Double = 75.00

    val type: String = "artist"

    val artist: Artist = Artist(
        externalUrlsString,
        href,
        id,
        uri,
        followers,
        genres,
        images,
        name,
        popularity,
        type
    )

    SpowloTheme {
        ArtistCard(
            artist = artist,
            onClick = {},
        )
    }
}