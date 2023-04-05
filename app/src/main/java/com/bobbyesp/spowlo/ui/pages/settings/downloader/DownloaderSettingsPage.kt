package com.bobbyesp.spowlo.ui.pages.settings.downloader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cached
import androidx.compose.material.icons.outlined.Filter
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.intState
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.LargeTopAppBar
import com.bobbyesp.spowlo.ui.components.PreferenceSubtitle
import com.bobbyesp.spowlo.ui.components.settings.ElevatedSettingsCard
import com.bobbyesp.spowlo.ui.components.settings.SettingsSwitch
import com.bobbyesp.spowlo.utils.DONT_FILTER_RESULTS
import com.bobbyesp.spowlo.utils.GEO_BYPASS
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.PreferencesUtil.updateInt
import com.bobbyesp.spowlo.utils.THREADS
import com.bobbyesp.spowlo.utils.USE_CACHING
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun DownloaderSettingsPage(
    onBackPressed: () -> Unit,
) {

    var threadsNumber = THREADS.intState

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

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState(),
            canScroll = { true })

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.downloader),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    BackButton { onBackPressed() }
                },
                scrollBehavior = scrollBehavior
            )
        },
        content = {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                item {
                    PreferenceSubtitle(text = stringResource(id = R.string.general))
                }
                item {
                    ElevatedSettingsCard {
                        SettingsSwitch(
                            onCheckedChange = {
                                useCache = !useCache
                                PreferencesUtil.updateValue(USE_CACHING, useCache)
                            },
                            checked = useCache,
                            title = {
                                Text(
                                    text = stringResource(id = R.string.use_cache),
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            icon = Icons.Outlined.Cached,
                            description = { Text(text = stringResource(id = R.string.use_cache_desc)) },
                        )
                    }
                }
                item {
                    PreferenceSubtitle(text = stringResource(id = R.string.experimental_features))
                }
                item {
                    ElevatedSettingsCard {
                        SettingsSwitch(
                            onCheckedChange = {
                                useGeobypass = !useGeobypass
                                PreferencesUtil.updateValue(GEO_BYPASS, useGeobypass)
                            },
                            checked = useGeobypass,
                            title = {
                                Text(
                                    text = stringResource(id = R.string.geo_bypass),
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            icon = Icons.Outlined.MyLocation,
                            description = { Text(text = stringResource(id = R.string.use_geobypass_desc)) },
                        )

                        SettingsSwitch(
                            onCheckedChange = {
                                dontFilter = !dontFilter
                                PreferencesUtil.updateValue(DONT_FILTER_RESULTS, dontFilter)
                            },
                            checked = dontFilter,
                            title = {
                                Text(
                                    text = stringResource(id = R.string.dont_filter_results),
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            icon = Icons.Outlined.Filter,
                            description = { Text(text = stringResource(id = R.string.dont_filter_results_desc)) },
                        )
                    }
                }
                item {
                    PreferenceSubtitle(text = stringResource(id = R.string.advanced_features))
                }
                item {
                    ElevatedSettingsCard {
                        //threads number item with a slicer
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.threads),
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .padding(start = 16.dp, top = 16.dp)
                                                .weight(1f)
                                        )
                                        Text(
                                            text = stringResource(id = R.string.threads_number) + ": " + threadsNumber.value.toString(),
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.6f
                                                )
                                            ),
                                            modifier = Modifier.padding(end = 16.dp, top = 16.dp)
                                        )
                                    }
                                    Text(
                                        text = stringResource(id = R.string.threads_number_desc),
                                        modifier = Modifier.padding(
                                            vertical = 12.dp, horizontal = 16.dp
                                        ),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    )

                                }
                            }
                            Slider(
                                value = threadsNumber.value.toFloat(),
                                onValueChange = {
                                    threadsNumber.value = it.toInt()
                                    THREADS.updateInt(it.toInt())
                                },
                                valueRange = 1f..10f,
                                steps = 9,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}