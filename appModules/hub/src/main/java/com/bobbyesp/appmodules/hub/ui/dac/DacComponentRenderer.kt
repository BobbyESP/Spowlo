package com.bobbyesp.appmodules.hub.ui.dac

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.bobbyesp.appmodules.core.api.interalApi.SpotifyInternalApi
import com.bobbyesp.appmodules.core.objects.ui_components.UiResponse
import com.bobbyesp.appmodules.hub.BuildConfig
import com.bobbyesp.appmodules.hub.ui.components.PlaceholderType
import com.bobbyesp.appmodules.hub.ui.components.dac.RecentlyPlayedSectionComponentBinder
import com.bobbyesp.appmodules.hub.ui.components.dac.SongsShortcutsGrid
import com.bobbyesp.appmodules.hub.ui.components.dac.actionCards.MediumActionCardBinder
import com.bobbyesp.appmodules.hub.ui.components.dac.actionCards.SmallActionCardBinder
import com.bobbyesp.appmodules.hub.ui.components.dac.recsplanation.RecsplanationHeadingComponentBinder
import com.bobbyesp.appmodules.hub.ui.components.dac.section.SectionComponentBinder
import com.bobbyesp.appmodules.hub.ui.components.dac.section.SectionHeaderComponentBinder
import com.bobbyesp.appmodules.hub.ui.components.dac.toolbars.ToolbarComponentV1Binder
import com.bobbyesp.appmodules.hub.ui.components.dac.toolbars.ToolbarComponentV2Binder
import com.bobbyesp.spowlo.proto.ErrorComponent
import com.google.protobuf.Message
import com.spotify.allplans.v1.DisclaimerComponent
import com.spotify.allplans.v1.PlanComponent
import com.spotify.home.dac.component.v1.proto.AlbumCardActionsMediumComponent
import com.spotify.home.dac.component.v1.proto.AlbumCardActionsSmallComponent
import com.spotify.home.dac.component.v1.proto.ArtistCardActionsMediumComponent
import com.spotify.home.dac.component.v1.proto.ArtistCardActionsSmallComponent
import com.spotify.home.dac.component.v1.proto.PlaylistCardActionsMediumComponent
import com.spotify.home.dac.component.v1.proto.PlaylistCardActionsSmallComponent
import com.spotify.home.dac.component.v1.proto.RecentlyPlayedSectionComponent
import com.spotify.home.dac.component.v1.proto.RecsplanationHeadingComponent
import com.spotify.home.dac.component.v1.proto.SectionComponent
import com.spotify.home.dac.component.v1.proto.SectionHeaderComponent
import com.spotify.home.dac.component.v1.proto.ShortcutsSectionComponent
import com.spotify.home.dac.component.v1.proto.ToolbarComponent
import com.spotify.home.dac.component.v2.proto.ToolbarComponentV2
import com.spotify.planoverview.v1.BenefitListComponent
import com.spotify.planoverview.v1.FallbackPlanComponent
import com.spotify.planoverview.v1.MultiUserMemberComponent
import com.spotify.planoverview.v1.SingleUserPrepaidComponent
import com.spotify.planoverview.v1.SingleUserRecurringComponent
import com.spotify.planoverview.v1.SingleUserTrialComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun DacComponentRenderer(
    item: Message,
    onNavigateToRequested: (String) -> Unit
) {
    val viewModel = hiltViewModel<DacComponentRendererViewModel>()
    when (item) {


        //////*Home page*//////

        //Top bars
        is ToolbarComponent -> ToolbarComponentV1Binder(item = item, onNavigateToRequested)
        is ToolbarComponentV2 -> ToolbarComponentV2Binder(item = item, onNavigateToRequested)

        //Song shortcuts
        is ShortcutsSectionComponent -> SongsShortcutsGrid(item, onNavigateToRequested)

        //New content by an artist
        is RecsplanationHeadingComponent -> RecsplanationHeadingComponentBinder(
            item,
            onNavigateToRequested
        )

        //////*Music section*//////
        is AlbumCardActionsSmallComponent -> SmallActionCardBinder(
            title = item.title,
            subtitle = item.subtitle,
            navigateUri = item.navigateUri,
            likeUri = item.likeUri,
            imageUri = item.imageUri,
            imagePlaceholder = PlaceholderType.Album,
            playCommand = item.playCommand,
            onNavigateToUri = onNavigateToRequested
        )

        is ArtistCardActionsSmallComponent -> SmallActionCardBinder(
            title = item.title,
            subtitle = item.subtitle,
            navigateUri = item.navigateUri,
            likeUri = item.followUri,
            imageUri = item.imageUri,
            imagePlaceholder = PlaceholderType.Artist,
            playCommand = item.playCommand,
            onNavigateToUri = onNavigateToRequested
        )

        is PlaylistCardActionsSmallComponent -> SmallActionCardBinder(
            title = item.title,
            subtitle = item.subtitle,
            navigateUri = item.navigateUri,
            likeUri = item.likeUri,
            imageUri = item.imageUri,
            imagePlaceholder = PlaceholderType.Playlist,
            playCommand = item.playCommand,
            onNavigateToUri = onNavigateToRequested
        )

        is AlbumCardActionsMediumComponent -> MediumActionCardBinder(
            title = item.title,
            subtitle = item.description,
            navigateUri = item.navigateUri,
            likeUri = item.likeUri,
            imageUri = item.imageUri,
            imagePlaceholder = PlaceholderType.Album,
            playCommand = item.playCommand,
            contentType = item.contentType,
            fact = item.conciseFact,
            gradientColor = item.gradientColor,
            onNavigateToUri = onNavigateToRequested
        )

        is ArtistCardActionsMediumComponent -> MediumActionCardBinder(
            title = item.title,
            subtitle = item.description,
            navigateUri = item.navigateUri,
            likeUri = item.followUri,
            imageUri = item.imageUri,
            imagePlaceholder = PlaceholderType.Artist,
            playCommand = item.playCommand,
            contentType = item.contentType,
            fact = item.conciseFact,
            gradientColor = item.gradientColor,
            onNavigateToUri = onNavigateToRequested
        )

        is PlaylistCardActionsMediumComponent -> MediumActionCardBinder(
            title = item.title,
            subtitle = item.description,
            navigateUri = item.navigateUri,
            likeUri = item.likeUri,
            imageUri = item.imageUri,
            imagePlaceholder = PlaceholderType.Playlist,
            playCommand = item.playCommand,
            contentType = item.contentType,
            fact = item.conciseFact,
            gradientColor = item.gradientColor,
            onNavigateToUri = onNavigateToRequested
        )

        //////*AllPlans / PlanOverview*//////
        is MultiUserMemberComponent -> {}
        is BenefitListComponent -> {}
        is PlanComponent -> {}
        is DisclaimerComponent -> {}
        is SingleUserRecurringComponent -> {}
        is SingleUserPrepaidComponent -> {}
        is SingleUserTrialComponent -> {}
        is FallbackPlanComponent -> {}

        //////*Songs rows*//////
        is SectionHeaderComponent -> SectionHeaderComponentBinder(item)
        is SectionComponent -> SectionComponentBinder(
            item = item,
            onNavigateToUri = onNavigateToRequested
        )

        is RecentlyPlayedSectionComponent -> RecentlyPlayedSectionComponentBinder(viewModel)

        //////*Error component*//////
        is ErrorComponent -> {
            if (BuildConfig.DEBUG) {
                Column {
                    Text(
                        if (item.type == ErrorComponent.ErrorType.UNSUPPORTED) {
                            "DAC unsupported component"
                        } else {
                            "DAC rendering error"
                        }, Modifier.padding(horizontal = 16.dp)
                    )
                    Text(
                        item.message ?: "",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }

        else -> {
            if (BuildConfig.DEBUG) {
                Text(
                    "DAC proto-known, but UI-unknown component: ${item::class.java.simpleName}\n\n${item}",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@HiltViewModel
class DacComponentRendererViewModel @Inject constructor(
    private val spotifyInternalApi: SpotifyInternalApi
) : ViewModel() {

    suspend fun getRecentlyPlayed(): UiResponse {
        return spotifyInternalApi.getListeningHistory()
    }
}