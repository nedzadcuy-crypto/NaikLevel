package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.AiTutorBottomSheet
import com.example.ui.components.NaikLevelBottomBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.CustomExamBuilderScreen
import com.example.ui.screens.ExamSimulationScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearningScreen
import com.example.ui.screens.MaterialCreateScreen
import com.example.ui.screens.MaterialReaderScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QuestScreen
import com.example.ui.screens.SibiBookReaderScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.viewmodel.NaikLevelViewModel

@Composable
fun NaikLevelApp(
    accountUid: String = "",
    accountName: String = "",
    accountEmail: String = "",
    emailVerified: Boolean = true,
    onSignOut: () -> Unit = {},
    onResendVerification: () -> Unit = {},
    viewModel: NaikLevelViewModel = viewModel(key = "naiklevel_$accountUid")
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val userProgress by viewModel.userProgress.collectAsStateWithLifecycle()

    // Sinkronkan nama akun ke profil lokal hanya saat nama masih bawaan.
    LaunchedEffect(accountName, userProgress?.userName) {
        val up = userProgress
        if (accountName.isNotBlank() && up != null && up.userName == "Pelajar Indonesia") {
            viewModel.updateProfile(accountName, up.schoolLevel, up.grade)
        }
    }
    val publishedMaterials by viewModel.publishedMaterials.collectAsStateWithLifecycle()
    val draftMaterials by viewModel.draftMaterials.collectAsStateWithLifecycle()
    val filteredMaterials by viewModel.filteredMaterials.collectAsStateWithLifecycle()
    val filteredSibiBooks by viewModel.filteredSibiBooks.collectAsStateWithLifecycle()
    val recentMaterial by viewModel.recentMaterial.collectAsStateWithLifecycle()
    val allQuests by viewModel.allQuests.collectAsStateWithLifecycle()
    val examAttempts by viewModel.examAttempts.collectAsStateWithLifecycle()
    val learningFilter by viewModel.learningFilter.collectAsStateWithLifecycle()
    val sibiReadingPositions by viewModel.sibiReadingPositions.collectAsStateWithLifecycle()
    val activeReadingMaterial by viewModel.activeReadingMaterial.collectAsStateWithLifecycle()
    val activeSibiBook by viewModel.activeSibiBook.collectAsStateWithLifecycle()
    val activeSibiChapterIndex by viewModel.activeSibiChapterIndex.collectAsStateWithLifecycle()
    val activeDraftAttachment by viewModel.activeDraftAttachment.collectAsStateWithLifecycle()
    val extractionStatus by viewModel.extractionStatus.collectAsStateWithLifecycle()
    val examSession by viewModel.examSession.collectAsStateWithLifecycle()
    val tutorMessages by viewModel.tutorMessages.collectAsStateWithLifecycle()
    val isTutorThinking by viewModel.isTutorThinking.collectAsStateWithLifecycle()
    val tutorContextSource by viewModel.tutorContextSource.collectAsStateWithLifecycle()
    val userNotice by viewModel.userNotice.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showAiTutorSheet by remember { mutableStateOf(false) }

    LaunchedEffect(userNotice) {
        userNotice?.let { notice ->
            snackbarHostState.showSnackbar(notice)
            viewModel.clearNotice()
        }
    }

    // Determine if bottom bar should be visible (only for main 5 top destinations)
    val isBottomBarVisible = currentRoute in Screen.bottomNavDestinations.map { it.route } &&
            activeReadingMaterial == null &&
            activeSibiBook == null &&
            !examSession.isActive

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (isBottomBarVisible) {
                NaikLevelBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { targetRoute ->
                        navController.navigate(targetRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Navigation Host
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.fillMaxSize()
            ) {
                // 1. Beranda
                composable(Screen.Home.route) {
                    HomeScreen(
                        userProgress = userProgress,
                        recentMaterial = recentMaterial,
                        dailyQuests = allQuests.filter { it.type == "DAILY" },
                        onOpenMaterial = { mat ->
                            viewModel.openMaterialForReading(mat)
                        },
                        onClaimQuest = { quest ->
                            viewModel.claimQuest(quest)
                        },
                        onOpenAiTutor = {
                            showAiTutorSheet = true
                        },
                        onNavigateToLearn = {
                            navController.navigate(Screen.Learn.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onStartExamSimulation = {
                            viewModel.startExamSimulation(
                                schoolLevel = userProgress?.schoolLevel ?: "SMA",
                                subject = "Semua"
                            )
                        }
                    )
                }

                // 2. Belajar
                composable(Screen.Learn.route) {
                    LearningScreen(
                        materials = filteredMaterials,
                        sibiBooks = filteredSibiBooks,
                        filter = learningFilter,
                        sibiReadingPositions = sibiReadingPositions,
                        onFilterTabChange = { viewModel.setFilterTab(it) },
                        onFilterSchoolLevelChange = { viewModel.setFilterSchoolLevel(it) },
                        onFilterGradeChange = { viewModel.setFilterGrade(it) },
                        onFilterSubjectChange = { viewModel.setFilterSubject(it) },
                        onFilterCurriculumChange = { viewModel.setFilterCurriculum(it) },
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onOpenMaterial = { mat ->
                            viewModel.openMaterialForReading(mat)
                        },
                        onOpenSibiBook = { book ->
                            viewModel.openSibiBook(book)
                        },
                        onAddNewMaterial = {
                            navController.navigate("create_material")
                        },
                        onOpenCustomExamBuilder = {
                            navController.navigate("custom_exam_builder")
                        },
                        onStartExamSimulation = {
                            viewModel.startExamSimulation(
                                schoolLevel = if (learningFilter.schoolLevel != "Semua") learningFilter.schoolLevel else (userProgress?.schoolLevel ?: "SMA"),
                                subject = if (learningFilter.subject != "Semua") learningFilter.subject else "Semua"
                            )
                        },
                        onDeleteMaterial = { id ->
                            viewModel.deleteMaterial(id)
                        }
                    )
                }

                // 3. Quest
                composable(Screen.Quest.route) {
                    QuestScreen(
                        userProgress = userProgress,
                        quests = allQuests,
                        onClaimQuest = { quest ->
                            viewModel.claimQuest(quest)
                        }
                    )
                }

                // 4. Statistik
                composable(Screen.Stats.route) {
                    StatisticsScreen(
                        materials = filteredMaterials,
                        examAttempts = examAttempts
                    )
                }

                // 5. Profil
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        userProgress = userProgress,
                        accountEmail = accountEmail,
                        emailVerified = emailVerified,
                        onSignOut = onSignOut,
                        onResendVerification = onResendVerification,
                        onUpdateProfile = { name, level, grade ->
                            viewModel.updateProfile(name, level, grade)
                        },
                        onTriggerSync = {
                            viewModel.triggerSyncCheck()
                        },
                        onResetData = {
                            viewModel.resetData()
                        },
                        onNavigateToAbout = {
                            navController.navigate("about")
                        }
                    )
                }

                // Subscreen: Create Material
                composable("create_material") {
                    MaterialCreateScreen(
                        currentAttachment = activeDraftAttachment,
                        extractionStatus = extractionStatus,
                        onSetAttachment = { att ->
                            viewModel.setDraftAttachment(att)
                        },
                        onRemoveAttachment = {
                            viewModel.removeDraftAttachment()
                        },
                        onStartExtraction = { type, input, attName, shouldFail ->
                            viewModel.simulateExtractContent(type, input, attName, shouldFail)
                        },
                        onResetExtraction = {
                            viewModel.resetExtractionStatus()
                        },
                        onSaveMaterial = { title, desc, content, level, grade, subject, topic, isDraft, att, attNote ->
                            viewModel.saveCreatedMaterial(
                                title = title,
                                description = desc,
                                content = content,
                                schoolLevel = level,
                                grade = grade,
                                subject = subject,
                                topic = topic,
                                isDraft = isDraft,
                                attachment = att,
                                attachmentNote = attNote,
                                onSuccess = {
                                    navController.popBackStack()
                                }
                            )
                        },
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

                // Subscreen: Custom Exam Builder
                composable("custom_exam_builder") {
                    CustomExamBuilderScreen(
                        materials = publishedMaterials + draftMaterials,
                        sibiBooks = SibiCatalog.books,
                        onStartExam = { config ->
                            viewModel.startCustomExam(config)
                            navController.popBackStack()
                        },
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

                // Subscreen: About Screen
                composable("about") {
                    AboutScreen(
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }

            // Interactive Fullscreen Overlays: Material Reader
            activeReadingMaterial?.let { material ->
                MaterialReaderScreen(
                    material = material,
                    onBack = { viewModel.closeReading() },
                    onProgressUpdate = { p -> viewModel.updateActiveReadingProgress(p) },
                    onMarkCompleted = { viewModel.markCurrentMaterialFinished() },
                    onAskAiTutor = { prompt ->
                        viewModel.sendAiTutorQuestion(prompt)
                        showAiTutorSheet = true
                    }
                )
            }

            // Interactive Fullscreen Overlays: SIBI Book Reader
            activeSibiBook?.let { sibiBook ->
                SibiBookReaderScreen(
                    book = sibiBook,
                    currentChapterIndex = activeSibiChapterIndex,
                    onSelectChapter = { idx -> viewModel.selectSibiChapter(idx) },
                    onAskAiAboutBook = { prompt ->
                        viewModel.sendAiTutorQuestion(prompt, explicitSource = "Buku SIBI: ${sibiBook.title}")
                        showAiTutorSheet = true
                    },
                    onBack = { viewModel.closeSibiBook() }
                )
            }

            // Interactive Fullscreen Overlays: Exam Simulation
            if (examSession.isActive) {
                ExamSimulationScreen(
                    session = examSession,
                    onSelectAnswer = { qId, optIdx -> viewModel.selectExamAnswer(qId, optIdx) },
                    onNextQuestion = { viewModel.nextExamQuestion() },
                    onPrevQuestion = { viewModel.previousExamQuestion() },
                    onSubmitExam = { viewModel.submitExamSimulation() },
                    onCloseExam = { viewModel.closeExamSession() }
                )
            }

            // BottomSheet: AI Tutor
            if (showAiTutorSheet) {
                AiTutorBottomSheet(
                    messages = tutorMessages,
                    isThinking = isTutorThinking,
                    contextSource = tutorContextSource,
                    onSendMessage = { query -> viewModel.sendAiTutorQuestion(query) },
                    onDismiss = { showAiTutorSheet = false }
                )
            }
        }
    }
}
