package com.bobbyesp.spowlo.ui.components.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.AsyncImageImpl
import com.bobbyesp.spowlo.ui.theme.Shapes
import com.bobbyesp.spowlo.utils.ChromeCustomTabsUtil

@Composable
fun ContributorComponent(
    name: String,
    description: String,
    socialUrl: String? = null,
    avatarUrl: String? = null,
    socialNetworkImage: ImageVector? = null
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
        if (avatarUrl != null) {
            Box(modifier = Modifier.padding(horizontal = 16.dp)){
                AsyncImageImpl(
                    model = avatarUrl,
                    contentDescription = stringResource(id = R.string.contributor_avatar),
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .padding(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
        Column(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(if(avatarUrl == null) 16.dp else 0.dp, 0.dp)) {
            Text(
                text = name,
                modifier = Modifier.padding(top = 8.dp, bottom = 6.dp),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = description,
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .alpha(0.7f),
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (socialUrl != null && socialNetworkImage != null) {
            Box(modifier = Modifier.size(48.dp).padding(end = 16.dp),
                contentAlignment = Alignment.Center){
                IconButton(
                    onClick = {
                        ChromeCustomTabsUtil.openUrl(socialUrl)
                    }) {
                    Icon(
                        imageVector = socialNetworkImage,
                        contentDescription = stringResource(id = R.string.social_logo),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}