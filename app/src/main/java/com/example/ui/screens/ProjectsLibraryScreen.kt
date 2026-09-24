package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PoetryProjectEntity
import com.example.data.repository.ClassicPoem
import com.example.ui.theme.*

@Composable
fun ProjectsLibraryScreen(
    savedProjects: List<PoetryProjectEntity>,
    classicLibrary: List<ClassicPoem>,
    onLoadProject: (PoetryProjectEntity) -> Unit,
    onDeleteProject: (Long) -> Unit,
    onLoadClassicPoem: (ClassicPoem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableStateOf("Classics") } // "Classics" or "My Projects"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Poetry Library & Vault",
                    color = PoeticIvory,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                // Tab Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        onClick = { selectedSection = "Classics" },
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedSection == "Classics") ImperialGold else SurfaceDark,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_classics")
                    ) {
                        Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Classic Masterworks (${classicLibrary.size})",
                                color = if (selectedSection == "Classics") DeepNight else PoeticIvoryMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Surface(
                        onClick = { selectedSection = "My Projects" },
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedSection == "My Projects") ImperialGold else SurfaceDark,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_my_projects")
                    ) {
                        Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "My Recordings (${savedProjects.size})",
                                color = if (selectedSection == "My Projects") DeepNight else PoeticIvoryMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        if (selectedSection == "Classics") {
            items(classicLibrary) { poem ->
                Card(
                    onClick = { onLoadClassicPoem(poem) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("classic_card_${poem.title}")
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
                            Column {
                                Text(
                                    text = poem.title,
                                    color = ImperialGold,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif
                                )
                                Text(
                                    text = "${poem.poet} • ${poem.era}",
                                    color = PoeticIvoryMuted,
                                    fontSize = 12.sp
                                )
                            }

                            FilledTonalButton(
                                onClick = { onLoadClassicPoem(poem) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Perform", fontSize = 11.sp, color = ImperialGold)
                            }
                        }

                        Text(
                            text = poem.content.lines().take(2).joinToString("\n"),
                            color = PoeticIvory,
                            fontSize = 13.sp,
                            lineHeight = 22.sp,
                            fontFamily = FontFamily.Serif
                        )

                        Text(
                            text = "\"${poem.englishMeaning}\"",
                            color = PoeticIvoryMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        } else {
            if (savedProjects.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                    ) {
                        Box(modifier = Modifier.padding(24.dp)) {
                            Text(
                                text = "No saved poetry projects yet. Create a poem and tap the bookmark icon in the top bar to save.",
                                color = PoeticIvoryMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(savedProjects) { project ->
                    Card(
                        onClick = { onLoadProject(project) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("project_item_${project.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = project.title,
                                    color = ImperialGold,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${project.poet} • Voice: ${project.selectedVoiceId}",
                                    color = PoeticIvoryMuted,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = project.content.lines().firstOrNull().orEmpty(),
                                    color = PoeticIvory,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = { onLoadProject(project) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OpenInNew,
                                        contentDescription = "Open",
                                        tint = ImperialGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteProject(project.id) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete",
                                        tint = RubyRose,
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
}
