package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.StudioUiState
import com.example.ui.theme.*

@Composable
fun AudioMixerScreen(
    state: StudioUiState,
    onMixerUpdated: (voiceVol: Float, musicVol: Float, ambientVol: Float, ducking: Boolean) -> Unit,
    onAutoMixClick: () -> Unit,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Master Audio Mixer",
                        color = PoeticIvory,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Dual-track faders, auto-ducking & studio leveling",
                        color = PoeticIvoryMuted,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onAutoMixClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImperialGold,
                        contentColor = DeepNight
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("button_auto_mix")
                ) {
                    Icon(imageVector = Icons.Default.Equalizer, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Auto Mix", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Faders Console Card
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Track 1: Voice Narration
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = ImperialGold, modifier = Modifier.size(18.dp))
                                Text("Voice Narration Level", color = PoeticIvory, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                            Text("${(state.voiceVolume * 100).toInt()}%", color = ImperialGold, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = state.voiceVolume,
                            onValueChange = { onMixerUpdated(it, state.musicVolume, state.ambientVolume, state.autoDucking) },
                            valueRange = 0.0f..1.5f,
                            colors = SliderDefaults.colors(thumbColor = ImperialGold, activeTrackColor = ImperialGold),
                            modifier = Modifier.testTag("slider_voice_vol")
                        )
                    }

                    Divider(color = BorderSubtle)

                    // Track 2: Background Music
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, tint = RubyRose, modifier = Modifier.size(18.dp))
                                Text("Background Music", color = PoeticIvory, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                            Text("${(state.musicVolume * 100).toInt()}%", color = RubyRose, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = state.musicVolume,
                            onValueChange = { onMixerUpdated(state.voiceVolume, it, state.ambientVolume, state.autoDucking) },
                            valueRange = 0.0f..1.0f,
                            colors = SliderDefaults.colors(thumbColor = RubyRose, activeTrackColor = RubyRose),
                            modifier = Modifier.testTag("slider_music_vol")
                        )
                    }

                    Divider(color = BorderSubtle)

                    // Track 3: Ambient Atmosphere / Room Noise
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.Grain, contentDescription = null, tint = MysticEmerald, modifier = Modifier.size(18.dp))
                                Text("Ambient Rain & Room Tone", color = PoeticIvory, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                            Text("${(state.ambientVolume * 100).toInt()}%", color = MysticEmerald, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = state.ambientVolume,
                            onValueChange = { onMixerUpdated(state.voiceVolume, state.musicVolume, it, state.autoDucking) },
                            valueRange = 0.0f..1.0f,
                            colors = SliderDefaults.colors(thumbColor = MysticEmerald, activeTrackColor = MysticEmerald)
                        )
                    }
                }
            }
        }

        // Auto Ducking Setting Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
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
                            text = "Intelligent Auto-Ducking",
                            color = PoeticIvory,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Automatically softens background music during recitation so the male voice remains pristine and legible.",
                            color = PoeticIvoryMuted,
                            fontSize = 12.sp
                        )
                    }

                    Switch(
                        checked = state.autoDucking,
                        onCheckedChange = { onMixerUpdated(state.voiceVolume, state.musicVolume, state.ambientVolume, it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DeepNight,
                            checkedTrackColor = ImperialGold,
                            uncheckedThumbColor = PoeticIvoryMuted,
                            uncheckedTrackColor = SurfaceHigh
                        ),
                        modifier = Modifier.testTag("switch_auto_ducking")
                    )
                }
            }
        }
    }
}
