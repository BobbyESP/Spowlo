package com.bobbyesp.spowlo.ui.components.searchBar

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.bobbyesp.spowlo.ui.theme.SpowloTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    placeholderText: String,
    leadingIcon: ImageVector,
    content: @Composable () -> Unit
) {
    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        active = active,
        onActiveChange = onActiveChange,
        modifier = modifier,
        placeholder = {
            Text(text = placeholderText)
        },
        leadingIcon = {
            Icon(leadingIcon, contentDescription = "Leading Icon of the search bar")
        },
        trailingIcon = {
            if(active) {
                Icon(
                    modifier = Modifier.clickable {
                        if(query.isNotEmpty()) {
                            onQueryChange("")
                        } else {
                            onActiveChange(false)
                        }
                    },
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Close search bar")
            }
        }
    ) {
        content()
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ExpandableSearchBarPreview() {
    SpowloTheme {
        var active by remember { mutableStateOf(false) }
        ExpandableSearchBar(
            query = "",
            onQueryChange = {},
            onSearch = {},
            active = active,
            onActiveChange = {
                active = it
            },
            content = {},
            placeholderText = "Search",
            leadingIcon = Icons.Outlined.Search
        )
    }
}