package com.bobbyesp.spowlo.ui.components.songs.search_feat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.MarqueeText
import com.bobbyesp.spowlo.utils.ChromeCustomTabsUtil

@Composable
fun SearchingSongComponent(
    artworkUrl: String,
    songName: String,
    artists: String,
    spotifyUrl: String,
    type : String = App.context.getString(R.string.single)
) {
    Column(Modifier.fillMaxWidth().clickable { ChromeCustomTabsUtil.openUrl(spotifyUrl) }.padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically, //This makes all go to the center
        ) {
            AsyncImageImpl(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .size(45.dp)
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                    .clip(MaterialTheme.shapes.small),
                model = artworkUrl,
                contentDescription = "Song cover",
                contentScale = ContentScale.Crop,
                isPreview = false
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(6.dp)
                        .weight(1f), //Weight is to make the time not go away from the screen
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
                    MarqueeText(
                        text = "$artists • $type",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 10.sp,
                        basicGradientColor = MaterialTheme.colorScheme.surface.copy(
                            alpha = 0.8f
                        ),
                    )
                }
            }
        }
    }
}