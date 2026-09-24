package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PresetCatalog
import com.example.ui.StudioTab
import com.example.ui.StudioUiState
import com.example.ui.theme.*

@Composable
fun CreateStudioScreen(
    state: StudioUiState,
    onTitleChange: (String) -> Unit,
    onPoetChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onScriptSelect: (String) -> Unit,
    onAnalyzeClick: () -> Unit,
    onGenerateNarrationClick: () -> Unit,
    onPreviewVoiceClick: () -> Unit,
    onApplyPreset: (com.example.data.model.PerformancePreset) -> Unit,
    onNavigateTab: (StudioTab) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // Quick Presets Carousel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "One-Tap Performance Presets",
                    color = ImperialGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresetCatalog.presets.forEach { preset ->
                        Surface(
                            onClick = { onApplyPreset(preset) },
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.testTag("preset_${preset.id}")
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                Text(
                                    text = preset.name,
                                    color = PoeticIvory,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = preset.subtitle,
                                    color = ImperialGold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Title and Poet fields
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = state.title,
                        onValueChange = onTitleChange,
                        label = { Text("Poetry Title / उनवान", color = PoeticIvoryMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_title"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ImperialGold,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = PoeticIvory,
                            unfocusedTextColor = PoeticIvory
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = state.poet,
                            onValueChange = onPoetChange,
                            label = { Text("Shayar / Poet", color = PoeticIvoryMuted) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_poet"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ImperialGold,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = PoeticIvory,
                                unfocusedTextColor = PoeticIvory
                            )
                        )

                        // Script type dropdown / chip
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceHigh,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = state.scriptType,
                                color = ImperialGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Poetry Editor Area
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Poetic Verses / अश्आर-ओ-नज़्म",
                            color = ImperialGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextButton(
                                onClick = onAnalyzeClick,
                                modifier = Modifier.testTag("button_analyze_poetry")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Analyze",
                                    tint = ImperialGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (state.isAnalyzing) "Analyzing..." else "AI Interpret",
                                    color = ImperialGold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = state.poetryText,
                        onValueChange = onTextChange,
                        placeholder = {
                            Text(
                                "Type or paste your Hindi & Urdu ghazals, nazms, shayari, or poetic lines here...",
                                color = TextSecondary,
                                fontFamily = FontFamily.Serif
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 180.dp, max = 340.dp)
                            .testTag("input_poetry_content"),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = PoeticIvory,
                            lineHeight = 30.sp
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ImperialGold,
                            unfocusedBorderColor = BorderSubtle,
                            focusedContainerColor = DeepNight,
                            unfocusedContainerColor = DeepNight
                        )
                    )
                }
            }
        }

        // AI Interpretation Result Card (if analyzed)
        state.analysisResult?.let { analysis ->
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGoldDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Poetry AI Insights",
                                tint = ImperialGold
                            )
                            Text(
                                text = "AI Poetry Interpretation & Mood",
                                color = ImperialGold,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Detected Emotion:", color = PoeticIvoryMuted, fontSize = 12.sp)
                                Text(state.selectedEmotion.hindiUrduName, color = PoeticIvory, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Mood Dynamic:", color = PoeticIvoryMuted, fontSize = 12.sp)
                                Text(analysis.detectedMood, color = ImperialGoldLight, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        Text(
                            text = analysis.meaningSummary,
                            color = PoeticIvory,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Serif
                        )

                        if (analysis.pronunciationTips.isNotEmpty()) {
                            Text(
                                text = "Pronunciation & Nuqta Guide:",
                                color = ImperialGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            analysis.pronunciationTips.forEach { tip ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DeepNight,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(tip.word, color = ImperialGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("• ${tip.phoneticUrduHindi}: ${tip.note}", color = PoeticIvoryMuted, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected Voice & Quick Controls Summary Card
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Current Male Voice", color = PoeticIvoryMuted, fontSize = 12.sp)
                            Text(
                                text = state.selectedVoice.name,
                                color = ImperialGold,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = state.selectedVoice.style,
                                color = PoeticIvoryMuted,
                                fontSize = 12.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilledTonalIconButton(
                                onClick = onPreviewVoiceClick,
                                modifier = Modifier.testTag("button_quick_preview_voice")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Preview Voice",
                                    tint = ImperialGold
                                )
                            }
                            FilledTonalButton(
                                onClick = { onNavigateTab(StudioTab.VOICES) },
                                modifier = Modifier.testTag("button_change_voice")
                            ) {
                                Text("Change", fontSize = 12.sp)
                            }
                        }
                    }

                    Divider(color = BorderSubtle)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Background Atmosphere", color = PoeticIvoryMuted, fontSize = 12.sp)
                            Text(
                                text = state.selectedAmbience.name,
                                color = PoeticIvory,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        TextButton(onClick = { onNavigateTab(StudioTab.AMBIENCE) }) {
                            Text("Customize Music", color = ImperialGold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Primary Action Button: Generate Full Performance Narration
        item {
            Button(
                onClick = onGenerateNarrationClick,
                enabled = !state.isGeneratingNarration && state.poetryText.isNotBlank(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ImperialGold,
                    contentColor = DeepNight
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("button_generate_performance")
            ) {
                if (state.isGeneratingNarration) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = DeepNight,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Synthesizing Expression (gemini-3.8-flash-tts)...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                } else {
                    Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recite with Expressive Male Voice",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
