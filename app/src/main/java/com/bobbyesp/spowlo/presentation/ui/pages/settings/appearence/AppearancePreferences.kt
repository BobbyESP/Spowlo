package com.bobbyesp.spowlo.presentation.ui.pages.settings.appearence

import android.os.Build
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.presentation.ui.common.LocalDarkTheme
import com.bobbyesp.spowlo.presentation.ui.common.LocalDynamicColorSwitch
import com.bobbyesp.spowlo.presentation.ui.common.LocalSeedColor
import com.bobbyesp.spowlo.presentation.ui.common.Route
import com.bobbyesp.spowlo.presentation.ui.components.*
import com.bobbyesp.spowlo.presentation.ui.theme.ColorScheme.DEFAULT_SEED_COLOR
import com.bobbyesp.spowlo.util.PreferencesUtil
import com.bobbyesp.spowlo.util.PreferencesUtil.DarkThemePreference.Companion.FOLLOW_SYSTEM
import com.bobbyesp.spowlo.util.PreferencesUtil.DarkThemePreference.Companion.OFF
import com.bobbyesp.spowlo.util.PreferencesUtil.DarkThemePreference.Companion.ON
import com.google.android.material.color.DynamicColors
import material.io.color.hct.Hct

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearancePreferences(
    navController: NavHostController
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )
    var showDarkThemeDialog by remember { mutableStateOf(false) }
    val darkTheme = LocalDarkTheme.current
    var darkThemeValue by remember { mutableStateOf(darkTheme.darkThemeValue) }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(R.string.display),
                    )
                }, navigationIcon = {
                    BackButton(modifier = Modifier.padding(start = 8.dp)) {
                        navController.popBackStack()
                    }
                }, scrollBehavior = scrollBehavior
            )
        }, content = {
            Column(
                Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp)
                    ) {
                        ColorButton(color = Color(DEFAULT_SEED_COLOR))
                        ColorButton(color = Color.Yellow)
                        ColorButton(
                            color = Color(
                                Hct.from(60.0, 150.0, 70.0).toInt()
                            )
                        )
                        ColorButton(
                            color = Color(
                                Hct.from(125.0, 50.0, 60.0).toInt()
                            )
                        )
                        ColorButton(color = Color.Cyan)
                        ColorButton(color = Color.Red)
                        ColorButton(color = Color.Magenta)
                        ColorButton(color = Color.Blue)
                    }
                }
                if (DynamicColors.isDynamicColorAvailable()) {
                    PreferenceSwitch(
                        title = stringResource(id = R.string.dynamic_color),
                        description = stringResource(
                            id = R.string.dynamic_color_desc
                        ),
                        icon = Icons.Outlined.Palette,
                        isChecked = LocalDynamicColorSwitch.current,
                        onClick = {
                            PreferencesUtil.switchDynamicColor()
                        }
                    )
                }
                PreferenceItem(
                    title = stringResource(id = R.string.dark_theme),
                    description = LocalDarkTheme.current.getDarkThemeDesc(),
                    icon = Icons.Outlined.DarkMode,
                    enabled = true
                ) { navController.navigate(Route.DARK_THEME_SELECTOR) }
                if (Build.VERSION.SDK_INT >= 24)
                    PreferenceItem(
                        title = stringResource(R.string.language),
                        icon = Icons.Outlined.Language,
                        description = PreferencesUtil.getLanguageDesc()
                    ) { navController.navigate(Route.LANGUAGES) }
            }
        })
    if (showDarkThemeDialog)
        AlertDialog(onDismissRequest = {
            showDarkThemeDialog = false
            darkThemeValue = darkTheme.darkThemeValue
        }, confirmButton = {
            ConfirmButton {
                showDarkThemeDialog = false
                PreferencesUtil.modifyDarkThemePreference(darkThemeValue)
            }
        }, dismissButton = {
            DismissButton {
                showDarkThemeDialog = false
                darkThemeValue = darkTheme.darkThemeValue
            }
        }, icon = { Icon(Icons.Outlined.DarkMode, null) },
            title = { Text(stringResource(R.string.dark_theme)) }, text = {
                Column {
                    SingleChoiceItem(
                        text = stringResource(R.string.follow_system),
                        selected = darkThemeValue == FOLLOW_SYSTEM
                    ) {
                        darkThemeValue = FOLLOW_SYSTEM
                    }
                    SingleChoiceItem(
                        text = stringResource(R.string.on),
                        selected = darkThemeValue == ON
                    ) {
                        darkThemeValue = ON
                    }
                    SingleChoiceItem(
                        text = stringResource(R.string.off),
                        selected = darkThemeValue == OFF
                    ) {
                        darkThemeValue = OFF
                    }
                }
            })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorButton(modifier: Modifier = Modifier, color: Color) {
    val corePalette = material.io.color.palettes.CorePalette.of(color.toArgb())
    val lightColor = corePalette.a2.tone(80)
    val seedColor = corePalette.a2.tone(60)
    val darkColor = corePalette.a2.tone(60)

    val showColor = if (LocalDarkTheme.current.isDarkTheme()) darkColor else lightColor
    val currentColor =
        !LocalDynamicColorSwitch.current && LocalSeedColor.current == seedColor
    val state = animateDpAsState(targetValue = if (currentColor) 48.dp else 36.dp)
    val state2 = animateDpAsState(targetValue = if (currentColor) 18.dp else 0.dp)
    ElevatedCard(modifier = modifier
        .clearAndSetSemantics { }
        .padding(4.dp)
        .size(72.dp), onClick = {
        PreferencesUtil.switchDynamicColor(enabled = false)
        PreferencesUtil.modifyThemeSeedColor(seedColor)
    }) {
        Box(Modifier.fillMaxSize()) {
            Box(
                modifier = modifier
                    .size(state.value)
                    .clip(CircleShape)
                    .background(Color(showColor))
                    .align(Alignment.Center)
            ) {

                Icon(
                    Icons.Outlined.Check,
                    null,
                    modifier = Modifier
                        .size(state2.value)
                        .align(Alignment.Center)
                        .clip(CircleShape),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }

}