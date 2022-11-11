package com.bobbyesp.spowlo.presentation.ui.pages.settings.appearence

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import com.bobbyesp.spowlo.R
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.presentation.MainActivity
import com.bobbyesp.spowlo.presentation.ui.components.BackButton
import com.bobbyesp.spowlo.presentation.ui.components.PreferenceSingleChoiceItem
import com.bobbyesp.spowlo.util.PreferencesUtil
import com.bobbyesp.spowlo.util.PreferencesUtil.LANGUAGE
import com.bobbyesp.spowlo.util.PreferencesUtil.SYSTEM_DEFAULT
import com.bobbyesp.spowlo.util.PreferencesUtil.getLanguageConfiguration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagesPreferences(onBackPressed: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )
    var language by remember { mutableStateOf(PreferencesUtil.getLanguageNumber()) }
    val uriHandler = LocalUriHandler.current
    val weblate = "https://hosted.weblate.org/engage/spowlo/"
    fun setLanguage(selectedLanguage: Int) {
        language = selectedLanguage
        PreferencesUtil.updateInt(LANGUAGE, language)
        MainActivity.setLanguage(getLanguageConfiguration())
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(id = R.string.language),
                    )
                }, navigationIcon = {
                    BackButton(modifier = Modifier.padding(start = 8.dp)) {
                        onBackPressed()
                    }
                }, scrollBehavior = scrollBehavior
            )
        }, content = {
            LazyColumn(modifier = Modifier.padding(it)) {
                /*item {
                    PreferencesHint(
                        title = stringResource(R.string.translate),
                        description = stringResource(R.string.translate_desc),
                        icon = Icons.Outlined.Translate,
                    ) { uriHandler.openUri(weblate) }
                }*/
                item {
                    PreferenceSingleChoiceItem(
                        text = stringResource(R.string.follow_system),
                        selected = language == SYSTEM_DEFAULT
                    ) { setLanguage(SYSTEM_DEFAULT) }
                }
                for (languageData in PreferencesUtil.languageMap) {
                    item {
                        PreferenceSingleChoiceItem(
                            text = PreferencesUtil.getLanguageDesc(languageData.key),
                            selected = language == languageData.key
                        ) { setLanguage(languageData.key) }
                    }
                }
            }
        })
}