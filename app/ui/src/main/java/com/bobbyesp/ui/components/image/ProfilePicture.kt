package com.bobbyesp.ui.components.image

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.ui.ext.getInitials

@Composable
fun ProfilePicture(
    modifier: Modifier = Modifier,
    size: Int = 40,
    name: String,
    shape: CornerBasedShape = CircleShape,
    surfaceColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit = {}
) {
    val firstLetter = name.getInitials()
    Surface(
        modifier = modifier.size(size.dp),
        shape = shape,
        onClick = onClick,
        color = surfaceColor,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                fontSize = (size / 2).sp,
                text = firstLetter,
                fontWeight = FontWeight.W500,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ProfilePicturePreview() {
    Column {
        ProfilePicture(
            name = "Bobby",
            onClick = {},
            shape = RoundedCornerShape(1f),
            modifier = Modifier,
            size = 40,
        )
        ProfilePicture(
            name = "Gabriel Fontán Rodiño",
            onClick = {},
            shape = RoundedCornerShape(1f),
            modifier = Modifier,
            size = 40,
        )
    }
}