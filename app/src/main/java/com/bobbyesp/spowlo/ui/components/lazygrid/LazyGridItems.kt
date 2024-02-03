package com.bobbyesp.spowlo.ui.components.lazygrid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R

val GridMenuItemHeight = 96.dp

@Composable
fun VerticalGridMenu(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    minSize: Dp = 120.dp,
    content: LazyGridScope.() -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = minSize),
        modifier = modifier,
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
fun HorizontalGridMenu(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    minSize: Dp = 120.dp,
    content: LazyGridScope.() -> Unit,
) {
    LazyHorizontalGrid(
        rows = GridCells.Adaptive(minSize = minSize),
        modifier = modifier,
        contentPadding = contentPadding,
        content = content
    )
}

fun LazyGridScope.GridMenuItem(
    modifier: Modifier = Modifier,
    icon: @Composable () -> ImageVector,
    title: @Composable () -> String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    item {
        Surface(
            modifier = modifier
                .clip(ShapeDefaults.Large)
                .height(GridMenuItemHeight)
                .alpha(if (enabled) 1f else 0.5f)
                .padding(12.dp),
            shape = MaterialTheme.shapes.small,
            onClick = onClick,
            enabled = enabled,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = icon(),
                    contentDescription = stringResource(id = R.string.icon)
                )
                Text(
                    text = title(),
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

fun LazyGridScope.GridMenuItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    GridMenuItem(
        modifier = modifier,
        icon = { icon },
        title = { title },
        enabled = enabled,
        onClick = onClick
    )
}

fun LazyGridScope.GridMenuItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: @Composable () -> String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    GridMenuItem(
        modifier = modifier,
        icon = { icon },
        title = title,
        enabled = enabled,
        onClick = onClick
    )
}


fun LazyGridScope.PlayPauseDynamicItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    playing: Boolean = false,
    time: String = "00:00",
) {
    item {
        Surface(
            modifier = modifier
                .clip(ShapeDefaults.Large)
                .height(GridMenuItemHeight)
                .alpha(if (enabled) 1f else 0.5f)
                .padding(12.dp),
            shape = MaterialTheme.shapes.small,
            onClick = onClick,
            enabled = enabled
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    6.dp,
                    Alignment.CenterVertically
                )
            ) {
                if (playing) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = stringResource(id = R.string.icon)
                    )
                    Text(
                        text = if (time.contains("-")) "00:00/00:30" else "$time/00:30",
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(id = R.string.icon)
                    )
                    Text(
                        text = stringResource(id = R.string.listen_preview),
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}