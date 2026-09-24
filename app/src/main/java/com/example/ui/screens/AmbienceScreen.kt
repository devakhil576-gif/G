package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.AmbienceCatalog
import com.example.data.model.BackgroundAmbience
import com.example.ui.StudioUiState
import com.example.ui.theme.*

@Composable
fun AmbienceScreen(
    state: StudioUiState,
    onAmbienceSelected: (BackgroundAmbience) -> Unit,
    onAiMatchClick: () -> Unit,
    onTestAmbienceToggle: () -> Unit,
    isPlayingMusic: Boolean,
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Background Music & Ambience",
                            color = PoeticIvory,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Atmospheric acoustic and procedural soundscapes to elevate the poetry",
                            color = PoeticIvoryMuted,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onAiMatchClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ImperialGold,
                            contentColor = DeepNight
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("button_ai_music_match")
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI Match", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Active Track banner with test play button
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Active Ambience", color = ImperialGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(state.selectedAmbience.name, color = PoeticIvory, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("${state.selectedAmbience.category} • ${state.selectedAmbience.tempoBpm} BPM", color = PoeticIvoryMuted, fontSize = 12.sp)
                    }

                    FilledTonalIconButton(
                        onClick = onTestAmbienceToggle,
                        modifier = Modifier.testTag("button_test_ambience")
                    ) {
                        Icon(
                            imageVector = if (isPlayingMusic) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Test Music",
                            tint = ImperialGold
                        )
                    }
                }
            }
        }

        items(AmbienceCatalog.ambiences) { ambience ->
            val isSelected = ambience.id == state.selectedAmbience.id

            Card(
                onClick = { onAmbienceSelected(ambience) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) SurfaceHigh else SurfaceDark
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) ImperialGold else BorderSubtle
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ambience_card_${ambience.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = ambience.name,
                                color = if (isSelected) ImperialGoldLight else PoeticIvory,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = DeepNight
                            ) {
                                Text(
                                    text = ambience.category,
                                    color = ImperialGold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ambience.description,
                            color = PoeticIvoryMuted,
                            fontSize = 12.sp
                        )
                    }

                    RadioButton(
                        selected = isSelected,
                        onClick = { onAmbienceSelected(ambience) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = ImperialGold,
                            unselectedColor = BorderSubtle
                        )
                    )
                }
            }
        }
    }
}
