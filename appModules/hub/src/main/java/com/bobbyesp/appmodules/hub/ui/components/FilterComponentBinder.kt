package com.bobbyesp.appmodules.hub.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bobbyesp.uisdk.components.AnimatedFilterChip
import com.spotify.home.dac.component.experimental.v1.proto.FilterComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterComponentBinder(
    component: FilterComponent,
    selectedFacet: String,
    selectFacet: (String) -> Unit,
) {
    LazyRow(
        Modifier.padding(start = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(component.facetsList) { item ->
            val selected = selectedFacet == item.value
            AnimatedFilterChip(
                selected = selected,
                onClick = {
                    selectFacet(if (selected) "default" else item.value)
                }, label = item.title,
                animated = true)
        }
    }
}