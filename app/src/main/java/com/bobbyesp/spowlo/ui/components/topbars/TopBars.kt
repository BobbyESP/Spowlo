package com.bobbyesp.spowlo.ui.components.topbars

import android.graphics.Path
import android.view.animation.PathInterpolator
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bobbyesp.spowlo.ui.theme.SpowloTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LargeTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    androidx.compose.material3.LargeTopAppBar(
        modifier = modifier,
        title = title,
        navigationIcon = navigationIcon,
        actions = actions,
        scrollBehavior = scrollBehavior,
    )

}

private val path = Path().apply {
    moveTo(0f, 0f)
    lineTo(0.7f, 0.1f)
    cubicTo(0.7f, 0.1f, .95F, .5F, 1F, 1F)
    moveTo(1f, 1f)
}

val fraction: (Float) -> Float = { PathInterpolator(path).getInterpolation(it) }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBar(
    modifier: Modifier = Modifier,
    titleText: String = "",
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    title: @Composable () -> Unit = {
        Text(
            text = titleText,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = fraction(scrollBehavior.state.overlappedFraction)),
            maxLines = 1
        )
    },
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        title = title,
        navigationIcon = navigationIcon,
        actions = actions,
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SmallTopAppBarPreview() {
    SpowloTheme {
        SmallTopAppBar(titleText = "Title")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LargeTopAppBarPreview() {
    SpowloTheme {
        LargeTopAppBar(title = { Text(text = "Title") })
    }
}