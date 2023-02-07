package com.bobbyesp.spowlo.features.mod_downloader.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.mod_downloader.domain.model.PackagesObjectDto
import com.bobbyesp.spowlo.features.mod_downloader.util.ModDownloaderUtils
import com.bobbyesp.spowlo.utils.GeneralTextUtils

enum class PackagesListItemType(
    val type: Int,
    val description: Int
) {
    Regular(
        R.string.regular,
        R.string.regular_description
    ),
    RegularCloned(
        R.string.regular_cloned,
        R.string.regular_cloned_description
    ),
    Amoled(
        R.string.amoled,
        R.string.amoled_description
    ),
    AmoledCloned(
        R.string.amoled_cloned,
        R.string.amoled_cloned_description
    ),
    Lite(
        R.string.lite,
        R.string.lite_description
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackagesListItem(
    modifier: Modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
    type: PackagesListItemType = PackagesListItemType.Regular,
    expanded: Boolean = false,
    packages: List<PackagesObjectDto>,
    latestVersion: String = "Unknown",
    onClick: () -> Unit = {},
) {
    var isExpanded by remember { mutableStateOf(expanded) }
    var show by remember {
        mutableStateOf(false)
    }
    ElevatedCard(
        modifier = modifier,
        onClick = { isExpanded = !isExpanded },
        shape = MaterialTheme.shapes.small
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .padding(end = 12.dp)
                        .padding(start = 8.dp)
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = stringResource(id = type.type),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier.padding(top = 6.dp),
                        text = stringResource(id = type.description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        overflow = TextOverflow.Ellipsis
                    )
                    Divider(modifier = Modifier.padding(top = 6.dp, bottom = 6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.latest_version),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold
                        )

                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.CenterEnd)){
                            Text(
                                modifier = Modifier,
                                text = latestVersion,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Bold,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

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
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp)
                        //adjust the size of the column to the quantity of items
                        .height(
                            when {
                                packages.size <= 2 -> 84.dp
                                else -> packages.size * 18.dp
                            }
                        )
                ) {
                    LazyColumn {
                        items(
                            items = packages,
                        ) { packageObject ->
                            val title = packageObject.Title
                            val link = packageObject.Link

                            //if the package title has ARM64-V8A, the type is Arm64
                            val isArm64: Boolean = title.contains("ARM64-V8A")

                            val containsArch: Boolean =
                                title.contains("(ARM64-V8A)") || title.contains("ARMEABI-V7A")

                            //Get just the version name without the architecture
                            val versionName =
                                if (containsArch) title.substringBefore("(").trim() else title

                            PackageItemComponent(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                //if its Arm64, ArchType is Arm64, else ArchType is Arm
                                type = if (isArm64) ArchType.Arm64 else ArchType.Arm,
                                version = versionName,
                                link = link,
                                //on click open the link in browser
                                onClick = { ModDownloaderUtils.openLinkInBrowser(link) },
                                onArchClick = { show = !show },
                                onCopyClick = { GeneralTextUtils.copyToClipboardAndNotify(link) }
                            )

                            //the last item of the lazy column will not have divider
                            if (packageObject != packages.last()) {
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}