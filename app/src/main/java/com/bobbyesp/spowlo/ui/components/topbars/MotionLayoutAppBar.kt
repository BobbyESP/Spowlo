package com.bobbyesp.spowlo.ui.components.topbars

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout

@OptIn(ExperimentalMotionApi::class)
@Composable
fun MotionLayoutAppBar(
    title: String,
    subTitle: String,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
    elevation: Dp = 4.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    progress: Float = 0.0f
) {
    MotionLayout(
        modifier = modifier.fillMaxWidth(),
        start = startConstraintSet(), // Not yet available
        end = endConstraintSet(), // Not yet available
        progress = progress
    ) {
        Surface(
            modifier = Modifier.layoutId(MotionLayoutAppBarItem.BACKGROUND_BOX),
            tonalElevation = elevation,
            color = backgroundColor,
            content = {}
        )

        IconButton(
            modifier = Modifier.layoutId(MotionLayoutAppBarItem.BACK_BUTTON),
            onClick = {
                onBackPressed()
            }
        ) {
            Icon(
                Icons.Default.ArrowBack,
                "Back Button",
                tint = contentColor
            )
        }

        Text(
            modifier = Modifier.layoutId(MotionLayoutAppBarItem.TITLE),
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = contentColor
        )

        Text(
            modifier = Modifier.layoutId(MotionLayoutAppBarItem.SUBTITLE),
            text = subTitle,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor
        )
    }
}

@Preview
@Composable
fun FinalStatePreview() {
    MotionLayoutAppBar(
        title = "Title",
        subTitle = "Subtitle",
        backgroundColor = Color(0xFF214561),
        progress = 1.0f
    )
}

private enum class MotionLayoutAppBarItem {
    BACK_BUTTON,
    TITLE,
    SUBTITLE,
    BACKGROUND_BOX;
}

private fun startConstraintSet() = ConstraintSet {
    val backButton = createRefFor(MotionLayoutAppBarItem.BACK_BUTTON)
    val title = createRefFor(MotionLayoutAppBarItem.TITLE)
    val subtitle = createRefFor(MotionLayoutAppBarItem.SUBTITLE)
    val backgroundBox = createRefFor(MotionLayoutAppBarItem.BACKGROUND_BOX)

    constrain(backButton) {
        top.linkTo(parent.top, 16.dp)
        start.linkTo(parent.start, 16.dp)
        bottom.linkTo(parent.bottom, 16.dp)
    }

    constrain(title) {
        top.linkTo(parent.top, 16.dp)
        start.linkTo(backButton.end, 16.dp)
    }

    constrain(subtitle) {
        top.linkTo(title.bottom, 4.dp)
        start.linkTo(title.start)
        bottom.linkTo(parent.bottom, 16.dp)
    }

    constrain(backgroundBox) {
        width = Dimension.matchParent
        height = Dimension.fillToConstraints

        top.linkTo(parent.top)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        bottom.linkTo(parent.bottom)
    }
}

private fun endConstraintSet() = ConstraintSet {
    val backButton = createRefFor(MotionLayoutAppBarItem.BACK_BUTTON)
    val title = createRefFor(MotionLayoutAppBarItem.TITLE)
    val subtitle = createRefFor(MotionLayoutAppBarItem.SUBTITLE)
    val backgroundBox = createRefFor(MotionLayoutAppBarItem.BACKGROUND_BOX)

    constrain(backButton) {
        top.linkTo(parent.top, 16.dp)
        start.linkTo(parent.start, 16.dp)
    }

    constrain(title) {
        top.linkTo(backButton.bottom, 16.dp)
        start.linkTo(backButton.start, 16.dp)
    }

    constrain(subtitle) {
        top.linkTo(title.bottom, 8.dp)
        start.linkTo(title.start)
        bottom.linkTo(parent.bottom, 16.dp)
    }

    constrain(backgroundBox) {
        width = Dimension.matchParent
        height = Dimension.fillToConstraints

        top.linkTo(parent.top)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        bottom.linkTo(parent.bottom)
    }
}