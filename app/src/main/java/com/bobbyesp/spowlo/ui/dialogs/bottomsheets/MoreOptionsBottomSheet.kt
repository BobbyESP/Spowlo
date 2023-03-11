package com.bobbyesp.spowlo.ui.dialogs.bottomsheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.Route

@Composable
fun MoreOptionsHomeBottomSheet(
    onBackPressed : () -> Unit,
    navController: NavController
){
    val uriHandler = LocalUriHandler.current

    val roundedTopShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
            .clip(roundedTopShape)

    ) {
        BottomSheetHandle(modifier = Modifier.align(Alignment.CenterHorizontally))

        Card(
            modifier = Modifier.padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
        ) {
            ListItem(
                leadingContent = {
                    Icon(imageVector = Icons.Rounded.Settings, contentDescription = null)
                }, headlineContent = {
                    Text(text = stringResource(id = R.string.settings))
                }, modifier = Modifier.clickable(onClick = {
                    navController.navigate(Route.SETTINGS)
                }), colors = ListItemDefaults.colors(
                    leadingIconColor = MaterialTheme.colorScheme.primary,
                    containerColor = Color.Transparent,
                )
            )
        }
        Column(
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
        ){
            Text(
                text = stringResource(id = R.string.app_name) + " " + App.packageInfo.versionName,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = stringResource(id = R.string.app_description),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(Modifier.padding(horizontal = 4.dp)) {
                IconButton(onClick = {
                    navController.navigate(Route.ABOUT)
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = {
                    uriHandler.openUri("https://github.com/BobbyESP/Spowlo")
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Code,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

}

@Composable
fun BottomSheetHandle(
    modifier: Modifier = Modifier
) {
    Divider(
        modifier = modifier
            .width(32.dp)
            .padding(vertical = 14.dp)
            .clip(CircleShape),
        thickness = 4.dp,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f)
    )
}

@Composable
fun BottomSheetHeader(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text,
        fontSize = 22.sp,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}