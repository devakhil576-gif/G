package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmotionCatalog
import com.example.data.model.EmotionProfile
import com.example.ui.StudioUiState
import com.example.ui.theme.*

@Composable
fun EmotionScreen(
    state: StudioUiState,
    onEmotionSelected: (EmotionProfile) -> Unit,
    onSlidersUpdated: (
        intensity: Float,
        speed: Float,
        pitch: Float,
        depth: Float,
        pause: Float,
        warmth: Float,
        reverb: Float
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Emotional Performance Direction",
                    color = PoeticIvory,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Select core emotional current and fine-tune vocal dynamics (pitch, depth, pauses, warmth).",
                    color = PoeticIvoryMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Active emotion banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, state.selectedEmotion.accentColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Current Emotion: ${state.selectedEmotion.name}",
                            color = ImperialGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = state.selectedEmotion.hindiUrduName,
                            color = PoeticIvory,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.selectedEmotion.description,
                            color = PoeticIvoryMuted,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(state.selectedEmotion.accentColor)
                    )
                }
            }
        }

        // 16 Emotions Grid
        item {
            Text(
                text = "16 Poetic Emotions / जज़्बात-ओ-कैफ़ियात",
                color = ImperialGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // 16 Emotions in 2-column or wrapping flow
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val chunked = EmotionCatalog.emotions.chunked(2)
                chunked.forEach { rowEmotions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowEmotions.forEach { emotion ->
                            val isSelected = emotion.id == state.selectedEmotion.id
                            Surface(
                                onClick = { onEmotionSelected(emotion) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) SurfaceHigh else SurfaceDark,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) emotion.accentColor else BorderSubtle
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("emotion_${emotion.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(emotion.accentColor)
                                    )
                                    Column {
                                        Text(
                                            text = emotion.name,
                                            color = if (isSelected) PoeticIvory else PoeticIvoryMuted,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                        Text(
                                            text = emotion.hindiUrduName,
                                            color = ImperialGold,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Manual Sliders Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Vocal Dynamics & Nuance Sliders",
                        color = ImperialGold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Emotion Intensity
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Emotion Intensity", color = PoeticIvory, fontSize = 13.sp)
                            Text("${"%.2f".format(state.emotionIntensity)}x", color = ImperialGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = state.emotionIntensity,
                            onValueChange = {
                                onSlidersUpdated(it, state.speakingSpeed, state.pitch, state.voiceDepth, state.pauseDurationSec, state.warmth, state.reverbLevel)
                            },
                            valueRange = 0.5f..2.0f,
                            colors = SliderDefaults.colors(thumbColor = ImperialGold, activeTrackColor = ImperialGold),
                            modifier = Modifier.testTag("slider_intensity")
                        )
                    }

                    // Speaking Speed
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Speaking Speed / Tempo", color = PoeticIvory, fontSize = 13.sp)
                            Text("${"%.2f".format(state.speakingSpeed)}x", color = ImperialGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = state.speakingSpeed,
                            onValueChange = {
                                onSlidersUpdated(state.emotionIntensity, it, state.pitch, state.voiceDepth, state.pauseDurationSec, state.warmth, state.reverbLevel)
                            },
                            valueRange = 0.70f..1.30f,
                            colors = SliderDefaults.colors(thumbColor = ImperialGold, activeTrackColor = ImperialGold),
                            modifier = Modifier.testTag("slider_speed")
                        )
                    }

                    // Voice Depth
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Voice Depth & Chest Resonance", color = PoeticIvory, fontSize = 13.sp)
                            Text("${"%.2f".format(state.voiceDepth)}x", color = ImperialGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = state.voiceDepth,
                            onValueChange = {
                                onSlidersUpdated(state.emotionIntensity, state.speakingSpeed, state.pitch, it, state.pauseDurationSec, state.warmth, state.reverbLevel)
                            },
                            valueRange = 0.8f..1.4f,
                            colors = SliderDefaults.colors(thumbColor = ImperialGold, activeTrackColor = ImperialGold)
                        )
                    }

                    // Dramatic Pauses
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Dramatic Pause Duration (between lines)", color = PoeticIvory, fontSize = 13.sp)
                            Text("${"%.1f".format(state.pauseDurationSec)}s", color = ImperialGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = state.pauseDurationSec,
                            onValueChange = {
                                onSlidersUpdated(state.emotionIntensity, state.speakingSpeed, state.pitch, state.voiceDepth, it, state.warmth, state.reverbLevel)
                            },
                            valueRange = 0.5f..3.0f,
                            colors = SliderDefaults.colors(thumbColor = ImperialGold, activeTrackColor = ImperialGold)
                        )
                    }

                    // Warmth & Reverb
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Acoustic Reverb / Hall Space", color = PoeticIvory, fontSize = 13.sp)
                            Text("${(state.reverbLevel * 100).toInt()}%", color = ImperialGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = state.reverbLevel,
                            onValueChange = {
                                onSlidersUpdated(state.emotionIntensity, state.speakingSpeed, state.pitch, state.voiceDepth, state.pauseDurationSec, state.warmth, it)
                            },
                            valueRange = 0.0f..0.8f,
                            colors = SliderDefaults.colors(thumbColor = ImperialGold, activeTrackColor = ImperialGold)
                        )
                    }
                }
            }
        }
    }
}
