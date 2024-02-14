package com.bobbyesp.spowlo.ui.components.cards.notifications

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.pages.LoginState

@Composable
fun LoginStateNotificationCard(
    modifier: Modifier = Modifier,
    loginState: LoginState
) {
    Surface(
        modifier = modifier,
        tonalElevation = 6.dp,
        shape = MaterialTheme.shapes.small
    ) {
        Crossfade(targetState = loginState, label = "Crossfade login states in the card") { state ->
            when (state) {
                LoginState.CHECKING_STATUS -> LoadingStateCardContent()
                LoginState.LOGGED_IN -> LoggedInStateCardContent()
                LoginState.NOT_LOGGED_IN -> NotLoggedInStateCardContent()
            }
        }

    }
}

@Composable
fun LoadingStateCardContent() {
    Row(
        modifier = Modifier
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CircularProgressIndicator()
        Column {
            Text(
                text = stringResource(id = R.string.checking_login_status),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(id = R.string.please_wait_login),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun LoggedInStateCardContent() {
    Row(
        modifier = Modifier
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Done,
            contentDescription = stringResource(id = R.string.logged_in)
        )
        Column {
            Text(
                text = stringResource(id = R.string.logged_in),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(id = R.string.logged_in_desc_card),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun NotLoggedInStateCardContent() {
    Row(
        modifier = Modifier
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = stringResource(id = R.string.not_logged_in)
        )
        Column {
            Text(
                text = stringResource(id = R.string.not_logged_in),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(id = R.string.not_logged_in_desc_card),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}