package com.bobbyesp.appModules.auth.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bobbyesp.appModules.auth.R
import com.bobbyesp.uisdk.UiUtils
import com.bobbyesp.uisdk.components.HorizontalDivider

@Composable
fun OnboardPage(
    onOnlyDownloaderClicked: () -> Unit,
    onAuthClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            Column(
                Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.new_onboarding_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.new_onboarding_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal
                )
            }
        }, bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onAuthClicked,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Key,
                        contentDescription = "Spotify login button"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(id = R.string.login_spotify))
                }

                FilledTonalButton(
                    onClick = onOnlyDownloaderClicked,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Download,
                        contentDescription = "Go downloader page button"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(id = R.string.new_onboarding_downloader))
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
            item {
                OnboardingCard(
                    icon = {
                        Icon(
                            imageVector = UiUtils.localAsset(R.drawable.spotify_logo),
                            contentDescription = null
                        )
                    },
                    title = stringResource(id = R.string.new_onboarding_spotify),
                    text = stringResource(id = R.string.new_onboarding_spotify_desc)
                )
            }
            item {
                OnboardingCard(
                    icon = {
                        Icon(imageVector = Icons.Rounded.Download, contentDescription = null)
                    },
                    title = stringResource(id = R.string.integrated_downloader),
                    text = stringResource(id = R.string.integrated_downloader_desc)
                )
            }
        }
    }
}

@Composable
private fun OnboardingCard(
    icon: @Composable () -> Unit,
    title: String,
    text: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Surface(
            tonalElevation = 16.dp,
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                .compositeOver(MaterialTheme.colorScheme.surfaceVariant),
            contentColor = MaterialTheme.colorScheme.primary,
        ) {
            Box(Modifier.padding(12.dp)) {
                icon()
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}