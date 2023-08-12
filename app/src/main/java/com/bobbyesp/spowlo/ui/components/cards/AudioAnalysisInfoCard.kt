package com.bobbyesp.spowlo.ui.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.ui.components.text.AutoResizableText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtraInfoCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    headlineText: String,
    bodyText: String
) {
    OutlinedCard(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.size(width = 150.dp, height = 100.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
        )
    ) {
        Text(
            text = headlineText,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(top = 8.dp)
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AutoResizableText(
                    text = bodyText,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WideExtraInfoCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    headlineText: String,
    bodyText: String
) {
    OutlinedCard(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
        )
    ) {
        Text(
            text = headlineText,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(top = 8.dp)
        )
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = bodyText,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}