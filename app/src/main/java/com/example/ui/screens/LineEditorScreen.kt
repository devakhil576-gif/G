package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmotionCatalog
import com.example.data.model.LineNarrationConfig
import com.example.ui.StudioUiState
import com.example.ui.theme.*

@Composable
fun LineEditorScreen(
    state: StudioUiState,
    onLineConfigUpdated: (Int, LineNarrationConfig) -> Unit,
    onPreviewLine: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Line-by-Line Expression Editor",
                    color = PoeticIvory,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Fine-tune delivery style, dramatic pauses, speed, and emotional weight for each couplet (Misra).",
                    color = PoeticIvoryMuted,
                    fontSize = 12.sp
                )
            }
        }

        if (state.lineConfigs.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Box(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "No lines found. Please enter some poetry verses in the Create tab first.",
                            color = PoeticIvoryMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            itemsIndexed(state.lineConfigs) { index, lineConfig ->
                LineConfigCard(
                    lineIndex = index,
                    config = lineConfig,
                    onConfigChanged = { updated -> onLineConfigUpdated(index, updated) },
                    onPreviewLine = { onPreviewLine(lineConfig.text) }
                )
            }
        }
    }
}

@Composable
fun LineConfigCard(
    lineIndex: Int,
    config: LineNarrationConfig,
    onConfigChanged: (LineNarrationConfig) -> Unit,
    onPreviewLine: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("line_card_$lineIndex")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SurfaceHigh
                ) {
                    Text(
                        text = "Misra #${lineIndex + 1}",
                        color = ImperialGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onPreviewLine,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Preview Line",
                            tint = ImperialGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand",
                            tint = PoeticIvoryMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Text(
                text = config.text,
                color = PoeticIvory,
                fontSize = 16.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(shape = RoundedCornerShape(6.dp), color = DeepNight) {
                    Text(
                        text = "Style: ${config.deliveryStyle}",
                        color = PoeticIvoryMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
                Surface(shape = RoundedCornerShape(6.dp), color = DeepNight) {
                    Text(
                        text = "Pause: ${config.pauseAfterMs}ms",
                        color = ImperialGold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            if (expanded) {
                Divider(color = BorderSubtle)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Delivery Style:", color = ImperialGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Whisper", "Resonant", "Lyrical", "Intense", "Natural Poetic").forEach { style ->
                            val isSelected = config.deliveryStyle == style
                            FilterChip(
                                selected = isSelected,
                                onClick = { onConfigChanged(config.copy(deliveryStyle = style)) },
                                label = { Text(style, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ImperialGold,
                                    selectedLabelColor = DeepNight
                                )
                            )
                        }
                    }

                    Text(
                        text = "Pause After Line: ${(config.pauseAfterMs / 1000f)}s",
                        color = PoeticIvoryMuted,
                        fontSize = 12.sp
                    )
                    Slider(
                        value = config.pauseAfterMs.toFloat(),
                        onValueChange = { onConfigChanged(config.copy(pauseAfterMs = it.toLong())) },
                        valueRange = 0f..3500f,
                        colors = SliderDefaults.colors(
                            thumbColor = ImperialGold,
                            activeTrackColor = ImperialGold
                        )
                    )

                    Text(
                        text = "Line Pace / Speed: ${"%.2f".format(config.speedMultiplier)}x",
                        color = PoeticIvoryMuted,
                        fontSize = 12.sp
                    )
                    Slider(
                        value = config.speedMultiplier,
                        onValueChange = { onConfigChanged(config.copy(speedMultiplier = it)) },
                        valueRange = 0.70f..1.30f,
                        colors = SliderDefaults.colors(
                            thumbColor = ImperialGold,
                            activeTrackColor = ImperialGold
                        )
                    )
                }
            }
        }
    }
}
