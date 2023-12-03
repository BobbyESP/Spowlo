package com.bobbyesp.spowlo.utils

import androidx.navigation.NavController
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.MetadataEntity
import com.bobbyesp.spowlo.ui.common.Route

object MetadataEntityUtil {
    fun navigateToEntity(navController: NavController, metadataEntity: MetadataEntity) {
        navController.navigate(
            Route.MetadataEntityViewer.createRoute(
                metadataEntity
            )
        )
    }
}