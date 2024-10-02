package com.bobbyesp.spowlo.ui.icons

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview
@Composable
private fun VectorPreview() {
    Image(YouTubeMusic, null)
}

private var _YouTubeMusic: ImageVector? = null

public val YouTubeMusic: ImageVector
    get() {
        if (_YouTubeMusic != null) {
            return _YouTubeMusic!!
        }
        _YouTubeMusic = ImageVector.Builder(
            name = "YouTubeMusic",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 50f,
            viewportHeight = 50f
        ).apply {
            group {
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(25f, 13f)
                    curveToRelative(-6.617f, 0f, -12f, 5.383f, -12f, 12f)
                    reflectiveCurveToRelative(5.383f, 12f, 12f, 12f)
                    reflectiveCurveToRelative(12f, -5.383f, 12f, -12f)
                    reflectiveCurveTo(31.617f, 13f, 25f, 13f)
                    close()
                    moveTo(31.521f, 25.854f)
                    lineToRelative(-9f, 5.5f)
                    curveTo(22.361f, 31.451f, 22.181f, 31.5f, 22f, 31.5f)
                    curveToRelative(-0.168f, 0f, -0.337f, -0.043f, -0.489f, -0.128f)
                    curveTo(21.195f, 31.195f, 21f, 30.861f, 21f, 30.5f)
                    verticalLineToRelative(-11f)
                    curveToRelative(0f, -0.361f, 0.195f, -0.695f, 0.511f, -0.872f)
                    curveToRelative(0.317f, -0.176f, 0.702f, -0.169f, 1.011f, 0.019f)
                    lineToRelative(9f, 5.5f)
                    curveTo(31.818f, 24.328f, 32f, 24.651f, 32f, 25f)
                    reflectiveCurveTo(31.818f, 25.672f, 31.521f, 25.854f)
                    close()
                }
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(25f, 3f)
                    curveTo(12.85f, 3f, 3f, 12.85f, 3f, 25f)
                    curveToRelative(0f, 12.15f, 9.85f, 22f, 22f, 22f)
                    reflectiveCurveToRelative(22f, -9.85f, 22f, -22f)
                    curveTo(47f, 12.85f, 37.15f, 3f, 25f, 3f)
                    close()
                    moveTo(25f, 39f)
                    curveToRelative(-7.72f, 0f, -14f, -6.28f, -14f, -14f)
                    reflectiveCurveToRelative(6.28f, -14f, 14f, -14f)
                    reflectiveCurveToRelative(14f, 6.28f, 14f, 14f)
                    reflectiveCurveTo(32.72f, 39f, 25f, 39f)
                    close()
                }
            }
        }.build()
        return _YouTubeMusic!!
    }


// https://icons8.com/icon/Mw6P3tmWMfOB/youtube-music