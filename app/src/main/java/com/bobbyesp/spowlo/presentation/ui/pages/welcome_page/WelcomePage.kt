package com.bobbyesp.spowlo.presentation.ui.pages.welcome_page

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.presentation.ui.common.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomePage(
    navController: NavController,
    viewModel: WelcomePageViewModel = hiltViewModel(),
    activity: Activity? = null){

    val viewState = viewModel.stateFlow.collectAsState()
    /*Welcome Page with one button to login to spotify or enter without login
    *If logged, navigate to main page
    If not logged, navigate to login page*/
    with(viewState.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                color = MaterialTheme.colorScheme.background,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .padding(25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 20.dp, top = 20.dp, start = 10.dp, end = 10.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                   Button(onClick = {
                       viewModel.loginToSpotify(activity, navController)
                   }) {
                        Text(text = stringResource(R.string.login))
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(onClick = { navController.navigate(Route.HOME) }) {
                        Text(text = stringResource(R.string.enter_without_login))
                    }

                   }
                }
            }
        }
    }