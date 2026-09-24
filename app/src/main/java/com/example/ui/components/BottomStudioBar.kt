package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.StudioTab
import com.example.ui.theme.*

@Composable
fun BottomStudioBar(
    currentTab: StudioTab,
    onTabSelected: (StudioTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DeepNight,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StudioTab.values().forEach { tab ->
                val isSelected = tab == currentTab
                val icon = getTabIcon(tab)

                Surface(
                    onClick = { onTabSelected(tab) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) ImperialGold else SurfaceDark,
                    contentColor = if (isSelected) DeepNight else PoeticIvoryMuted,
                    modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.title,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = tab.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getTabIcon(tab: StudioTab): ImageVector {
    return when (tab) {
        StudioTab.STUDIO -> Icons.Default.Create
        StudioTab.VOICES -> Icons.Default.RecordVoiceOver
        StudioTab.EDITOR -> Icons.Default.FormatLineSpacing
        StudioTab.EMOTION -> Icons.Default.Favorite
        StudioTab.AMBIENCE -> Icons.Default.MusicNote
        StudioTab.MIXER -> Icons.Default.Tune
        StudioTab.PREVIEW -> Icons.Default.PlayCircleFilled
        StudioTab.EXPORT -> Icons.Default.FileDownload
        StudioTab.PROJECTS -> Icons.Default.CollectionsBookmark
    }
}
