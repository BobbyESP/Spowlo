package com.bobbyesp.spowlo.ui.pages.home.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.inapp_notifications.domain.NotificationManager
import com.bobbyesp.spowlo.ui.components.cards.notifications.SongDownloadNotification
import com.bobbyesp.spowlo.ui.components.topbars.SmallTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsPage(
    notificationManager: NotificationManager,
    onGoBack: () -> Unit
) {
    val notificationsMap =
        notificationManager.getNotificationMapFlow().collectAsStateWithLifecycle().value

    val notificationsAsList = notificationsMap.values.toList()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = stringResource(id = R.string.notifications),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onGoBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
        ) {
            items(
                count = notificationsAsList.size,
                key = { index -> notificationsAsList[index].id }
            ) { notificationIndex ->
                SongDownloadNotification(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    notification = notificationsAsList[notificationIndex],
                    showBar = false
                )
            }
        }
    }
}