package com.bobbyesp.spowlo.ui.pages.settings.general

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cached
import androidx.compose.material.icons.outlined.Filter
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.PrintDisabled
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import com.bobbyesp.library.SpotDL
import com.bobbyesp.library.SpotDLRequest
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.booleanState
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.LargeTopAppBar
import com.bobbyesp.spowlo.ui.components.PreferenceItem
import com.bobbyesp.spowlo.ui.components.PreferenceSubtitle
import com.bobbyesp.spowlo.ui.components.PreferenceSwitch
import com.bobbyesp.spowlo.utils.DEBUG
import com.bobbyesp.spowlo.utils.DONT_FILTER_RESULTS
import com.bobbyesp.spowlo.utils.GEO_BYPASS
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.USE_CACHING
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun GeneralSettingsPage(
    onBackPressed: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true })

    var displayErrorReport by DEBUG.booleanState

    var useCache by remember {
        mutableStateOf(
            PreferencesUtil.getValue(USE_CACHING)
        )
    }

    var dontFilter by remember {
        mutableStateOf(
            PreferencesUtil.getValue(DONT_FILTER_RESULTS)
        )
    }

    var useGeobypass by remember {
        mutableStateOf(
            PreferencesUtil.getValue(GEO_BYPASS)
        )
    }

    val loadingString = App.context.getString(R.string.loading)

    var spotDLVersion by remember { mutableStateOf(
        loadingString
    ) }

    //create a non-blocking coroutine to get the version
    LaunchedEffect(Unit) {
        GlobalScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    spotDLVersion = SpotDL.getInstance().execute(SpotDLRequest().addOption("-v"), null, null).output
                }
            }catch (e: Exception) {
                spotDLVersion = e.message ?: e.toString()
            }

        }
    }

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.general_settings)) },
                navigationIcon = {
                    BackButton { onBackPressed() }
                },
                scrollBehavior = scrollBehavior
            )
        },
        content = {
            LazyColumn(
                modifier = Modifier.padding(it)
            ) {
                item {
                    PreferenceItem(
                        title = stringResource(id = R.string.spotdl_version),
                        description = spotDLVersion,
                        icon = Icons.Outlined.Info,
                        onClick = {
                        },
                        onClickLabel = stringResource(id = R.string.update),
                        onLongClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)

                        }, onLongClickLabel = stringResource(id = R.string.open_settings)
                    )
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(R.string.print_details),
                        description = stringResource(R.string.print_details_desc),
                        icon = if (displayErrorReport) Icons.Outlined.Print else Icons.Outlined.PrintDisabled,
                        enabled = true,
                        onClick = {
                            displayErrorReport = !displayErrorReport
                            PreferencesUtil.updateValue(DEBUG, displayErrorReport)
                        },
                        isChecked = displayErrorReport
                    )
                }

                item{
                    PreferenceSubtitle(text = stringResource(id = R.string.library_settings))
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(id = R.string.use_cache),
                        description = stringResource(id = R.string.use_cache_desc),
                        icon = Icons.Outlined.Cached,
                        onClick = {
                            scope.launch {
                                useCache = !useCache
                                PreferencesUtil.updateValue(USE_CACHING, useCache)
                            }
                        },
                        isChecked = useCache
                    )
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(id = R.string.geo_bypass),
                        description = stringResource(id = R.string.use_geobypass_desc),
                        icon = Icons.Outlined.MyLocation,
                        onClick = {
                            scope.launch {
                                useGeobypass = !useGeobypass
                                PreferencesUtil.updateValue(GEO_BYPASS, useGeobypass)
                            }
                        },
                        isChecked = useGeobypass
                    )
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(id = R.string.dont_filter_results),
                        description = stringResource(id = R.string.dont_filter_results_desc),
                        icon = Icons.Outlined.Filter,
                        onClick = {
                            scope.launch {
                                dontFilter = !dontFilter
                                PreferencesUtil.updateValue(DONT_FILTER_RESULTS, dontFilter)
                            }
                        },
                        isChecked = dontFilter
                    )
                }
            }
        })
}