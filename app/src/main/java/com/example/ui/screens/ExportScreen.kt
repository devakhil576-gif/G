package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.ui.StudioUiState
import com.example.ui.theme.*

@Composable
fun ExportScreen(
    state: StudioUiState,
    onExportAudioClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Studio Export & Distribution",
                    color = PoeticIvory,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Export high-resolution master audio and share poetry cards across social channels.",
                    color = PoeticIvoryMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Master Audio Export Card
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Audiotrack,
                            contentDescription = null,
                            tint = ImperialGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Master Audio Mix (WAV Format)",
                                color = PoeticIvory,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "24-bit PCM • Voice + Mixed Atmospheric Ambience",
                                color = PoeticIvoryMuted,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Divider(color = BorderSubtle)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Active Voice:", color = PoeticIvoryMuted, fontSize = 12.sp)
                        Text(state.selectedVoice.name, color = PoeticIvory, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Active Ambience:", color = PoeticIvoryMuted, fontSize = 12.sp)
                        Text(state.selectedAmbience.name, color = PoeticIvory, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onExportAudioClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ImperialGold,
                            contentColor = DeepNight
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("button_export_audio")
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Render & Save Master Audio", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    // If file is already exported, show share action
                    state.exportedFile?.let { file ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceHigh,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Ready: ${file.name}", color = ImperialGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("${file.length() / 1024} KB", color = PoeticIvoryMuted, fontSize = 11.sp)
                                }
                                FilledTonalButton(
                                    onClick = {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "audio/wav"
                                            putExtra(Intent.EXTRA_SUBJECT, "${state.title} - Sukhan Narration")
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "Listen to '${state.title}' by ${state.poet}, performed on Sukhan Poetry Studio:\n\n${state.poetryText}"
                                            )
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share Master Audio"))
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("button_share_audio")
                                ) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Share", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Social Format Recommendations
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
                    Text(
                        text = "Optimized Export Profiles",
                        color = ImperialGold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    listOf(
                        "Instagram Reels & TikTok" to "9:16 Vertical • Synchronized Karaoked Ghazal Card",
                        "YouTube Video & Mushaira" to "16:9 Cinematic Widescreen • Studio Master Audio",
                        "WhatsApp Audio Status" to "30-sec High-Quality Mixed Performance Clip",
                        "Podcast & Audiobooks" to "Studio Master 24-bit Voice Only / Ambient Bed"
                    ).forEach { (platform, desc) ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DeepNight,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(platform, color = PoeticIvory, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(desc, color = PoeticIvoryMuted, fontSize = 11.sp)
                                }
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MysticEmerald,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
