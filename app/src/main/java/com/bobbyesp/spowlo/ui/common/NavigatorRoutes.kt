package com.bobbyesp.spowlo.ui.common

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalPlay
import androidx.compose.ui.graphics.vector.ImageVector
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.data.local.model.SelectedSong
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed class Route(
    val route: String,
    var title: String,
    val icon: ImageVector? = null,
) {
    //NAVIGATION HOSTS
    object MainHost : Route("main_host", "")

    //NAVIGATORS
    object HomeNavigator : Route("home_navigator", "", Icons.Default.Home)
    object SearchNavigator : Route("search_navigator", "", Icons.Default.Search)
    object UtilitiesNavigator : Route("utilities_navigator", "", Icons.Outlined.LocalPlay)
    object ProfileNavigator : Route("profile_navigator", "", Icons.Default.Person)
    object SettingsNavigator : Route("settings_navigator", "", Icons.Default.Settings)

    //ROUTES
    object Home : Route("home", "", Icons.Outlined.Home)
    object Utilities : Route("utilities", "", Icons.Outlined.LocalPlay)
    object LyricsDownloader : Route("lyrics_downloader", "", Icons.Default.Lyrics)
    object SelectedSongLyrics :
        Route("selected_song/{${NavArgs.SelectedSong.key}}", "", Icons.Default.Lyrics) {
        fun createRoute(selectedSong: SelectedSong) =
            "selected_song/${Uri.encode(Json.encodeToString<SelectedSong>(selectedSong))}"
    }

    object TagEditor : Route("tag_editor", "", Icons.Default.Edit) {
        object Editor : Route(
            "tag_editor/editor/{${NavArgs.TagEditorSelectedSong.key}}",
            "",
            Icons.Default.Edit
        ) {
            fun createRoute(selectedSong: SelectedSong) =
                "tag_editor/editor/${Uri.encode(Json.encodeToString<SelectedSong>(selectedSong))}"
        }
    }

    object MiniplayerPage : Route("miniplayer", "", Icons.Default.MusicNote)
    object Search : Route("search", "", Icons.Default.Search)
    object Profile : Route("profile", "", Icons.Default.Person)


    object OnboardingPage : Route("onboarding_page", "")
    object Settings : Route("settings_page", "", Icons.Default.Settings)

    class StringUtils(
        private val context: Context
    ) {
        fun getStringWithContext(@StringRes resId: Int): String {
            return context.getString(resId)
        }
    }

    companion object {
        private lateinit var applicationContext: Context

        fun initialize(applicationContext: Context) {
            this.applicationContext = applicationContext
            val stringUtils = StringUtils(applicationContext)

            OnboardingPage.title = stringUtils.getStringWithContext(R.string.onboarding)

            MainHost.title = stringUtils.getStringWithContext(R.string.app_name)

            HomeNavigator.title = stringUtils.getStringWithContext(R.string.home)
            SearchNavigator.title = stringUtils.getStringWithContext(R.string.search)
            UtilitiesNavigator.title = stringUtils.getStringWithContext(R.string.utilities)
            SettingsNavigator.title = stringUtils.getStringWithContext(R.string.settings)
            ProfileNavigator.title = stringUtils.getStringWithContext(R.string.profile)

            Settings.title = stringUtils.getStringWithContext(R.string.settings)

            Home.title = stringUtils.getStringWithContext(R.string.home)
            Search.title = stringUtils.getStringWithContext(R.string.search)
            Utilities.title = stringUtils.getStringWithContext(R.string.utilities)
            LyricsDownloader.title = stringUtils.getStringWithContext(R.string.lyrics_downloader)
            MiniplayerPage.title = stringUtils.getStringWithContext(R.string.miniplayer)
            TagEditor.title = stringUtils.getStringWithContext(R.string.id3_tag_editor)
            Profile.title = stringUtils.getStringWithContext(R.string.profile)

        }
    }
}

enum class NavArgs(val key: String) {
    SelectedSong(key = "selectedSong"),
    TagEditorSelectedSong(key = "tagEditorSelectedSong")
}