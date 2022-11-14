package com.bobbyesp.spowlo.presentation.ui.components.BottomNavBar

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bobbyesp.spowlo.presentation.ui.common.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    items: List<NavBarItem>,
    navController: NavController,
    onItemClicked: (NavBarItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    NavigationBar(
        modifier = modifier,
    ){
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            NavigationBarItem(
                icon = {
                       Column(horizontalAlignment = CenterHorizontally) {
                           if(item.badgeCount > 0){
                               BadgedBox(badge = {
                                   Text(text = item.badgeCount.toString())
                               }) {
                                   Icon(imageVector = item.icon, contentDescription = item.name)
                               }
                           }
                           else {
                               Icon(imageVector = item.icon, contentDescription = item.name)
                           }
                       }
                },
                label = { Text(text = item.name) },
                selected = selected,
                onClick = { onItemClicked(item) }
            )
        }
    }
}