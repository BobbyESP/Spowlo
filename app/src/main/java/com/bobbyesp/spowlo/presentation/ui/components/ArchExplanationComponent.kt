package com.bobbyesp.spowlo.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R

enum class ArchExplType(
    val type: String,
    val archDescription: String,
    val description: Int,
    val archType: ArchType = ArchType.Arm64
) {
    Arm64("ARM64-v8a", "64-bit ARM", R.string.arm64_desc, ArchType.Arm64),
    Arm("ARMEABI-v7a", "32-bit ARM", R.string.arm32_desc, ArchType.Arm),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchExplanationComponent(
    type : ArchExplType,
    expanded: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(expanded) }

    ElevatedCard(
        modifier = Modifier,
        onClick = { isExpanded = !isExpanded },
        shape = MaterialTheme.shapes.small
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .padding(end = 6.dp)
                        .padding(start = 2.dp)
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = type.type,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier.padding(top = 6.dp),
                        text = type.archDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(
                            modifier = Modifier
                                .weight(1f, true)
                        )

                        val animatedDegree =
                            animateFloatAsState(targetValue = if (isExpanded) 0f else -180f)
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .align(Alignment.Bottom)
                        ) {
                            FilledTonalIconButton(
                                modifier = Modifier
                                    .padding()
                                    .size(24.dp),
                                onClick = { isExpanded = !isExpanded }) {
                                Icon(
                                    Icons.Outlined.ExpandLess,
                                    null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.rotate(animatedDegree.value)
                                )
                            }
                        }
                    }
                }
            }
        }
        AnimatedVisibility(visible = isExpanded) {
                Box(modifier = Modifier
                    .align(Alignment.CenterHorizontally)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp, start = 4.dp, end = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ArchTag(arch = type.archType)
                        Text(
                            text = stringResource(id = type.description),
                            modifier = Modifier.padding(start = 6.dp),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }

@Preview
@Composable
fun previewArchExplanationComponent() {
        ArchExplanationComponent(type = ArchExplType.Arm64)
}