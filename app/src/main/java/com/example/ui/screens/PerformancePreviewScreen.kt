package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackState
import com.example.ui.StudioUiState
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun PerformancePreviewScreen(
    state: StudioUiState,
    playbackState: PlaybackState,
    onPlayPauseClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onStopClick: () -> Unit,
    onAspectRatioSelected: (String) -> Unit,
    onVisualThemeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll as active line moves
    LaunchedEffect(playbackState.currentLineIndex) {
        if (playbackState.isPlaying && state.lineConfigs.isNotEmpty()) {
            val target = playbackState.currentLineIndex.coerceIn(0, state.lineConfigs.size - 1)
            coroutineScope.launch {
                listState.animateScrollToItem(target)
            }
        }
    }

    val themeBackground = when (state.visualTheme) {
        "Royal Mughal Gold" -> Brush.verticalGradient(listOf(Color(0xFF261805), Color(0xFF0F0A02)))
        "Ruby Twilight" -> Brush.verticalGradient(listOf(Color(0xFF2D0A14), Color(0xFF0D0306)))
        "Emerald Court" -> Brush.verticalGradient(listOf(Color(0xFF041E15), Color(0xFF020D09)))
        "Obsidian Stars" -> Brush.verticalGradient(listOf(Color(0xFF10141D), Color(0xFF040608)))
        else -> Brush.verticalGradient(listOf(Color(0xFF1A0F26), Color(0xFF0A050F))) // Velvet Midnight
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
    ) {
        // Aspect Ratio and Theme Pickers
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Aspect Ratio Selector
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("9:16", "1:1", "16:9").forEach { ratio ->
                        val isSel = state.videoAspectRatio == ratio
                        Surface(
                            onClick = { onAspectRatioSelected(ratio) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) ImperialGold else SurfaceDark,
                            modifier = Modifier.testTag("ratio_$ratio")
                        ) {
                            Text(
                                text = ratio,
                                color = if (isSel) DeepNight else PoeticIvoryMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Visual Theme Quick Selector
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Velvet Midnight", "Royal Mughal Gold", "Ruby Twilight").forEach { themeName ->
                        val isSel = state.visualTheme == themeName
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(
                                    when (themeName) {
                                        "Royal Mughal Gold" -> ImperialGold
                                        "Ruby Twilight" -> RubyRose
                                        else -> Color(0xFF6B4B8B)
                                    }
                                )
                                .clickable { onVisualThemeSelected(themeName) }
                                .border(
                                    width = if (isSel) 2.dp else 0.dp,
                                    color = if (isSel) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }

        // The Cinematic Poetry Video Card (Visualizer + Synchronized Karaoked Lines)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, ImperialGoldDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 360.dp, max = 460.dp)
                    .testTag("cinematic_poetry_stage")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(themeBackground)
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header ornament
                        Text(
                            text = "بِسْمِ اللَّهِ / سُخَن",
                            color = ImperialGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.title,
                            color = ImperialGoldLight,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = state.poet,
                            color = PoeticIvoryMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Synchronized Scrolling Lines
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            itemsIndexed(state.lineConfigs) { index, lineConfig ->
                                val isCurrent = playbackState.isPlaying && (playbackState.currentLineIndex == index)

                                val textColor by animateColorAsState(
                                    targetValue = if (isCurrent) ImperialGoldLight else PoeticIvory.copy(alpha = 0.7f),
                                    label = "lineTextColor"
                                )
                                val bgLineColor by animateColorAsState(
                                    targetValue = if (isCurrent) SurfaceHigh.copy(alpha = 0.6f) else Color.Transparent,
                                    label = "lineBg"
                                )

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = bgLineColor,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = lineConfig.text,
                                            color = textColor,
                                            fontSize = if (isCurrent) 20.sp else 17.sp,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                            fontFamily = FontFamily.Serif,
                                            textAlign = TextAlign.Center,
                                            lineHeight = 32.sp
                                        )
                                        if (isCurrent) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.GraphicEq,
                                                    contentDescription = null,
                                                    tint = ImperialGold,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    text = "Reciting with ${state.selectedVoice.name} • ${state.selectedEmotion.name}",
                                                    color = ImperialGold,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Footer signature
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sukhan Poetry Studio",
                            color = PoeticIvoryMuted.copy(alpha = 0.5f),
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        // Playback Transport Controls
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Scrubber
                    Column {
                        Slider(
                            value = playbackState.currentPositionMs.toFloat(),
                            onValueChange = { onSeek(it.toLong()) },
                            valueRange = 0f..(playbackState.totalDurationMs.takeIf { it > 0 }?.toFloat() ?: 60000f),
                            colors = SliderDefaults.colors(
                                thumbColor = ImperialGold,
                                activeTrackColor = ImperialGold,
                                inactiveTrackColor = SurfaceHigh
                            ),
                            modifier = Modifier.testTag("playback_seekbar")
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = formatTime(playbackState.currentPositionMs),
                                color = PoeticIvoryMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = formatTime(playbackState.totalDurationMs),
                                color = PoeticIvoryMuted,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Transport Buttons (Stop, Play/Pause)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalIconButton(
                            onClick = onStopClick,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("button_stop_playback")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop",
                                tint = PoeticIvory
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        FloatingActionButton(
                            onClick = onPlayPauseClick,
                            containerColor = ImperialGold,
                            contentColor = DeepNight,
                            modifier = Modifier
                                .size(64.dp)
                                .testTag("button_play_pause")
                        ) {
                            Icon(
                                imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
