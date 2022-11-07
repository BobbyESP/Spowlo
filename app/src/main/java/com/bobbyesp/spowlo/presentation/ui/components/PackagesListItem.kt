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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.domain.spotify.model.PackagesObject
import com.bobbyesp.spowlo.presentation.MainActivity
import com.bobbyesp.spowlo.presentation.ui.pages.home.HomeViewModel
import com.bobbyesp.spowlo.util.DownloadUtil

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
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackagesListItem(
    modifier: Modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
    type: PackagesListItemType = PackagesListItemType.Regular,
    expanded: Boolean = false,
    packages: List<PackagesObject>,
    onClick: () -> Unit = {},
) {
    var isExpanded by remember { mutableStateOf(expanded) }


    ElevatedCard(
        modifier = modifier,
        onClick = { isExpanded = !isExpanded },
        shape = MaterialTheme.shapes.small) {
        Box{
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
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Spacer(modifier = Modifier
                            .weight(1f, true))

                        val animatedDegree = animateFloatAsState(targetValue = if (isExpanded) 0f else -180f)
                        Box(modifier = Modifier
                            .padding(2.dp)
                            .align(Alignment.Bottom)){
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
            Box{
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
                ) {
                    packages.forEach { version ->
                        //if the package title has ARM64-V8A, the type is Arm64
                        val isArm64: Boolean = packages[packages.indexOf(version)].Title.contains("ARM64-V8A")
                        val containsArch: Boolean = packages[packages.indexOf(version)].Title.contains("(ARM64-V8A)") || packages[packages.indexOf(version)].Title.contains("ARMEABI-V7A")
                        //get just the version name without the architecture
                        val versionName = if (containsArch) packages[packages.indexOf(version)].Title.substringBefore("(").trim() else packages[packages.indexOf(version)].Title
                        PackageItem(
                            modifier = Modifier
                                .fillMaxWidth(),
                            //if its Arm64, ArchType is Arm64, else ArchType is Arm
                            type = if (isArm64) ArchType.Arm64 else ArchType.Arm,
                            version = versionName,
                            link = packages[packages.indexOf(version)].Link,
                            //on click open the link in browser
                            onClick = { DownloadUtil.openLinkInBrowser(packages[packages.indexOf(version)].Link) }
                        )

                        if (packages.indexOf(version) != packages.lastIndex) {
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

@Composable
@Preview
fun CardPreview(){
    Column() {
       // PackagesListItem( type = PackagesListItemType.RegularCloned, expanded = true, versions = listOf("1.0.0", "1.0.1", "1.0.2").random(), archs = listOf(ArchType.Arm64, ArchType.Arm).random())
        //PackagesListItem(type = PackagesListItemType.Amoled, expanded = true, versions = listOf("1.0.0", "1.0.1", "1.0.2").random(), archs = listOf(ArchType.Arm64, ArchType.Arm).random())
    }

}