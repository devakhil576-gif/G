package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BottomStudioBar
import com.example.ui.components.TopHeader
import com.example.ui.screens.*
import com.example.ui.theme.*

@Composable
fun SukhanMainScreen(
    viewModel: PoetryStudioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()
    val savedProjects by viewModel.savedProjects.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DeepNight,
        topBar = {
            TopHeader(
                title = uiState.title,
                subtitle = uiState.poet,
                onSaveClick = { viewModel.saveProject() },
                onClassicLibraryClick = { viewModel.setActiveTab(StudioTab.PROJECTS) }
            )
        },
        bottomBar = {
            BottomStudioBar(
                currentTab = uiState.activeTab,
                onTabSelected = { viewModel.setActiveTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.activeTab) {
                StudioTab.STUDIO -> {
                    CreateStudioScreen(
                        state = uiState,
                        onTitleChange = { viewModel.updateTitle(it) },
                        onPoetChange = { viewModel.updatePoet(it) },
                        onTextChange = { viewModel.updatePoetryText(it) },
                        onScriptSelect = { viewModel.setScriptType(it) },
                        onAnalyzeClick = { viewModel.analyzePoetryWithGemini() },
                        onGenerateNarrationClick = { viewModel.generateFullPerformanceNarration() },
                        onPreviewVoiceClick = { viewModel.previewVoice(uiState.selectedVoice) },
                        onApplyPreset = { viewModel.applyPreset(it) },
                        onNavigateTab = { viewModel.setActiveTab(it) }
                    )
                }
                StudioTab.VOICES -> {
                    VoicesScreen(
                        state = uiState,
                        onVoiceSelected = { viewModel.selectVoice(it) },
                        onPreviewVoice = { viewModel.previewVoice(it) }
                    )
                }
                StudioTab.EDITOR -> {
                    LineEditorScreen(
                        state = uiState,
                        onLineConfigUpdated = { idx, config -> viewModel.updateLineConfig(idx, config) },
                        onPreviewLine = { line -> viewModel.audioEngine.previewVoiceOffline(line) }
                    )
                }
                StudioTab.EMOTION -> {
                    EmotionScreen(
                        state = uiState,
                        onEmotionSelected = { viewModel.selectEmotion(it) },
                        onSlidersUpdated = { intensity, speed, pitch, depth, pause, warmth, reverb ->
                            viewModel.updateSliders(
                                intensity = intensity,
                                speed = speed,
                                pitch = pitch,
                                depth = depth,
                                pause = pause,
                                warmth = warmth,
                                reverb = reverb
                            )
                        }
                    )
                }
                StudioTab.AMBIENCE -> {
                    AmbienceScreen(
                        state = uiState,
                        onAmbienceSelected = { viewModel.selectAmbience(it) },
                        onAiMatchClick = {
                            val rec = com.example.data.model.AmbienceCatalog.recommendForEmotion(uiState.selectedEmotion.id)
                            viewModel.selectAmbience(rec)
                        },
                        onTestAmbienceToggle = {
                            if (playbackState.isPlaying) viewModel.audioEngine.stopAll()
                            else viewModel.audioEngine.playPerformance()
                        },
                        isPlayingMusic = playbackState.isPlaying
                    )
                }
                StudioTab.MIXER -> {
                    AudioMixerScreen(
                        state = uiState,
                        onMixerUpdated = { voiceVol, musicVol, ambientVol, ducking ->
                            viewModel.updateMixer(voiceVol, musicVol, ambientVol, ducking)
                        },
                        onAutoMixClick = { viewModel.autoMix() }
                    )
                }
                StudioTab.PREVIEW -> {
                    PerformancePreviewScreen(
                        state = uiState,
                        playbackState = playbackState,
                        onPlayPauseClick = { viewModel.playPausePerformance() },
                        onSeek = { viewModel.seekPlayback(it) },
                        onStopClick = { viewModel.stopPlayback() },
                        onAspectRatioSelected = { viewModel.setAspectRatio(it) },
                        onVisualThemeSelected = { viewModel.setVisualTheme(it) }
                    )
                }
                StudioTab.EXPORT -> {
                    ExportScreen(
                        state = uiState,
                        onExportAudioClick = { viewModel.exportFinalAudio() }
                    )
                }
                StudioTab.PROJECTS -> {
                    ProjectsLibraryScreen(
                        savedProjects = savedProjects,
                        classicLibrary = viewModel.classicLibrary,
                        onLoadProject = { viewModel.loadProject(it) },
                        onDeleteProject = { viewModel.deleteProject(it) },
                        onLoadClassicPoem = { viewModel.applyClassicPoem(it) }
                    )
                }
            }

            // Floating status notification
            uiState.statusMessage?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceHigh,
                    tonalElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                        .testTag("status_snackbar")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = msg,
                            color = PoeticIvory,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearStatusMessage() },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss", tint = PoeticIvory)
                        }
                    }
                }
            }
        }
    }
}
