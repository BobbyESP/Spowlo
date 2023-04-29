package com.bobbyesp.spowlo.ui.components.bottomBar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun FloatingBottomBar(
    expanded: Boolean,
    selectedItem: Int,
    items: List<BottomBarItem>,
    expandedContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val innerContainerColor = MaterialTheme.colorScheme.surfaceColorAtAlpha(0.1f)
    val outerContainerColor = MaterialTheme.colorScheme.surfaceColorAtAlpha(0.2f)

    Box(
        modifier
            .fillMaxWidth()
            .navigationBarsPadding()) {

        ElevatedCard(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = innerContainerColor
            ), shape = MaterialTheme.shapes.large
        ) {
            SubcomposeLayout { constraints ->
                val expandedLayout = subcompose(BottomBarSlots.ExpandedContent) {
                    FloatingBottomBarExpandable(expanded, expandedContent)
                }.also {
                    require(it.size <= 1) { "Expanded layout should contain only one (or no) child!" }
                }.firstOrNull()?.measure(constraints)

                val expandedLayoutWidth = expandedLayout?.width ?: 0
                val expandedLayoutHeight = expandedLayout?.height ?: 0

                val tabsLayout = subcompose(BottomBarSlots.TabsContent) {
                    FloatingBottomBarTabs(items, selectedItem, expandedLayoutWidth.toDp(), outerContainerColor)
                }.first().measure(Constraints())

                val maxWidth = max(tabsLayout.width, expandedLayoutWidth)
                val maxHeight = expandedLayoutHeight + tabsLayout.height

                layout(maxWidth, maxHeight) {
                    if (expandedLayout != null) {
                        expandedLayout.placeRelative(0, 0)
                        tabsLayout.placeRelative(0, expandedLayoutHeight)
                    } else {
                        tabsLayout.placeRelative(0, 0)
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingBottomBarExpandable(
    expanded: Boolean,
    expandedContent: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = expanded,
        enter = expandIn(animationSpec = IslandAnimations.islandSpec(), expandFrom = Alignment.BottomCenter, clip = false),
        exit = shrinkOut(animationSpec = IslandAnimations.islandSpec(), shrinkTowards = Alignment.BottomCenter, clip = false),
        modifier = Modifier
    ) {
        expandedContent()
    }
}

@Composable
private fun FloatingBottomBarTabs(
    items: List<BottomBarItem>,
    selectedItem: Int,
    expandedWidth: Dp,
    containerColor: Color
) {
    IndicatorBehindScrollableTabRow(
        selectedTabIndex = selectedItem,
        containerColor = containerColor,
        indicator = { tabPositions ->
            Box(
                Modifier
                    .padding(vertical = 12.dp)
                    .tabIndicatorOffset(tabPositions[selectedItem])
                    .fillMaxHeight()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.onSurface)
            )
        },
        edgePadding = 16.dp,
        modifier = Modifier
            .animateContentSize(IslandAnimations.islandSpec())
            .clip(MaterialTheme.shapes.large)
            .widthIn(min = expandedWidth)
    ) {
        items.forEachIndexed { index, item ->
            NoRippleTab(
                selected = selectedItem == index,
                onClick = item.onClick,
                selectedContentColor = MaterialTheme.colorScheme.inverseOnSurface,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Row(Modifier.padding(vertical = 24.dp, horizontal = 12.dp)) {
                    when (item) {
                        is BottomBarItem.ComposableView -> {
                            item.content()
                        }

                        is BottomBarItem.Icon -> {
                            Icon(imageVector = item.icon(), contentDescription = item.description?.let { stringResource(id = it) })
                        }
                    }
                }
            }
        }
    }
}

@Immutable
sealed class BottomBarItem(
    val id: String,
    val onClick: () -> Unit
) {
    class Icon (
        val icon: () -> ImageVector,
        val description: Int? = null,
        id: String,
        onClick: () -> Unit
    ): BottomBarItem(id, onClick)

    class ComposableView (
        val content: @Composable () -> Unit,
        id: String,
        onClick: () -> Unit
    ): BottomBarItem(id, onClick)
}

private object IslandAnimations {
    private const val STIFFNESS = 450F
    private const val RATIO = 0.75f

    fun <T> islandSpec(): FiniteAnimationSpec<T> = spring(stiffness = STIFFNESS, dampingRatio = RATIO)
}

private enum class BottomBarSlots {
    ExpandedContent,
    TabsContent
}

fun ColorScheme.surfaceColorAtAlpha(
    alpha: Float,
): Color {
    return surfaceTint.copy(alpha = alpha).compositeOver(surface)
}