package com.bobbyesp.spowlo.ui.components.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.TextButtonWithIcon
import com.bobbyesp.spowlo.ui.theme.Shapes
import com.bobbyesp.spowlo.utils.ChromeCustomTabsUtil

@Composable
fun HeadDeveloperComponent(
    modifier: Modifier = Modifier,
    name: String,
    description: String,
    logoUrl: String,
    githubUrl: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImageImpl(
            model = logoUrl,
            contentDescription = stringResource(id = R.string.github_logo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(128.dp)
        )
        Text(
            text = name,
            modifier = Modifier.padding(top = 8.dp,bottom = 6.dp),
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize
        )
        Text(
            text = description,
            modifier = Modifier.padding(bottom = 6.dp).alpha(0.7f),
        )
        Row(modifier = Modifier) {
            GithubLinkButton(
                modifier = Modifier,
                githubUrl = githubUrl
            )
        }
    }
}

@Composable
fun GithubLinkButton(modifier: Modifier, githubUrl: String) {
    TextButtonWithIcon(
        modifier = modifier.clip(Shapes.extraSmall),
        onClick = { ChromeCustomTabsUtil.openUrl(githubUrl) },
        icon = ImageVector.vectorResource(id = R.drawable.github_mark),
        text = stringResource(
            id = R.string.github
        )
    )

}
