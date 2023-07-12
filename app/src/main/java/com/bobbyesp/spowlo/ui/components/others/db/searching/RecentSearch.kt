package com.bobbyesp.spowlo.ui.components.others.db.searching

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.data.local.db.searching.entity.SearchEntity
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.MediaStoreFilterType
import com.bobbyesp.spowlo.ui.components.others.FilterTag
import com.bobbyesp.spowlo.ui.ext.toDate
import com.bobbyesp.spowlo.ui.theme.SpowloTheme
import com.bobbyesp.spowlo.utils.localAsset

@Composable
fun RecentSearch(
    modifier: Modifier = Modifier,
    searchEntity: SearchEntity,
    onClick: () -> Unit = {},
    onDeleteClick: (SearchEntity) -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { onDeleteClick(searchEntity) },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete recent search"
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = searchEntity.search,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
            )
            Text(
                text = searchEntity.date.toDate(),
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                ),
                modifier = Modifier,
                fontSize = 12.sp
            )
        }
        Box(
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Row {
                if(searchEntity.filter != null) {
                    FilterTag(text = searchEntity.filter.toString(context))
                }
                if(searchEntity.filter != null && searchEntity.spotifySearch) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
                if (searchEntity.spotifySearch) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = localAsset(id = R.drawable.spotify_logo),
                        contentDescription = "Spotify logo"
                    )
                }
            }
        }
    }
}


@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun RecentSearchPreview() {
    SpowloTheme {
        RecentSearch(
            modifier = Modifier.fillMaxWidth(),
            searchEntity = SearchEntity(
                id = 0,
                search = "Alan walker faded",
                spotifySearch = true,
                date = System.currentTimeMillis(),
                filter = MediaStoreFilterType.TITLE
            ),
            onDeleteClick = {

            },
        )
    }
}