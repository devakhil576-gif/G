package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VoiceCatalog
import com.example.data.model.VoiceProfile
import com.example.ui.StudioUiState
import com.example.ui.theme.*

@Composable
fun VoicesScreen(
    state: StudioUiState,
    onVoiceSelected: (VoiceProfile) -> Unit,
    onPreviewVoice: (VoiceProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    var comparisonVoiceId by remember { mutableStateOf(state.selectedVoice.id) }
    var isComparisonMode by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Male Voice Profiles",
                            color = PoeticIvory,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "12 Distinct Performance Personalities & Shayar Styles",
                            color = PoeticIvoryMuted,
                            fontSize = 12.sp
                        )
                    }

                    FilterChip(
                        selected = isComparisonMode,
                        onClick = { isComparisonMode = !isComparisonMode },
                        label = { Text("Compare", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CompareArrows,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ImperialGold,
                            selectedLabelColor = DeepNight
                        )
                    )
                }
            }
        }

        // Voice Comparison Showcase (if enabled)
        if (isComparisonMode) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGoldDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Voice Comparison Mode",
                            color = ImperialGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Listen to the same couplet performed across different male vocal styles:",
                            color = PoeticIvoryMuted,
                            fontSize = 12.sp
                        )

                        val comparisonList = listOf(
                            VoiceCatalog.maleVoices[0], // Deep Emotional
                            VoiceCatalog.maleVoices[1], // Soft Romantic
                            VoiceCatalog.maleVoices[2], // Mature Poet
                            VoiceCatalog.maleVoices[3]  // Cinematic
                        )

                        comparisonList.forEach { voice ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (voice.id == state.selectedVoice.id) SurfaceHigh else DeepNight,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (voice.id == state.selectedVoice.id) ImperialGold else BorderSubtle
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(voice.name, color = PoeticIvory, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(voice.style, color = ImperialGold, fontSize = 11.sp)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        FilledTonalIconButton(
                                            onClick = { onPreviewVoice(voice) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Listen",
                                                tint = ImperialGold,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Button(
                                            onClick = { onVoiceSelected(voice) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (voice.id == state.selectedVoice.id) ImperialGold else SurfaceDark,
                                                contentColor = if (voice.id == state.selectedVoice.id) DeepNight else PoeticIvory
                                            ),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            Text(if (voice.id == state.selectedVoice.id) "Active" else "Select", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Full Voice Catalog List
        items(VoiceCatalog.maleVoices) { voice ->
            val isSelected = voice.id == state.selectedVoice.id

            Card(
                onClick = { onVoiceSelected(voice) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) SurfaceElevated else SurfaceDark
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) ImperialGold else BorderSubtle
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_card_${voice.id}")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) ImperialGold else SurfaceHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = if (isSelected) DeepNight else ImperialGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = voice.name,
                                    color = if (isSelected) ImperialGoldLight else PoeticIvory,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = voice.titleHindiUrdu,
                                    color = ImperialGold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) ImperialGoldDark else SurfaceHigh
                        ) {
                            Text(
                                text = voice.tag,
                                color = if (isSelected) PoeticIvory else PoeticIvoryMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = voice.description,
                        color = PoeticIvoryMuted,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DeepNight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sample: \"${voice.sampleText}\"",
                                color = PoeticIvoryMuted,
                                fontSize = 11.sp,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            FilledTonalButton(
                                onClick = { onPreviewVoice(voice) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .height(30.dp)
                                    .testTag("preview_voice_${voice.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Preview",
                                    modifier = Modifier.size(14.dp),
                                    tint = ImperialGold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Preview", fontSize = 11.sp, color = ImperialGold)
                            }
                        }
                    }
                }
            }
        }
    }
}
