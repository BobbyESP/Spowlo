package com.bobbyesp.spowlo.ui.ext

import androidx.navigation.NavHostController
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.MetadataEntity
import com.bobbyesp.spowlo.ui.common.Route

fun NavHostController.navigateToMetadataEntity(metadata: MetadataEntity) {
    this.navigate(
        Route.MetadataEntityViewer.createRoute(
            metadata
        )
    )
}