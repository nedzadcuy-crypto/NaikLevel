
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
import com.example.data.model.SibiCatalog
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
    val currentRoute =
        navBackStackEntry?.destination?.route ?: Screen.Home.route

    val userProgress by viewModel.userProgress.collectAsStateWithLifecycle()
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

    LaunchedEffect(accountName, userProgress?.userName) {
        val progress = userProgress
        if (
            accountName.isNotBlank() &&
            progress != null &&
            progress.userName == "Pelajar Indonesia"
        ) {
            viewModel.updateProfile(
                accountName,
                progress.schoolLevel,
                progress.grade
            )
        }
    }

    LaunchedEffect(userNotice) {
        userNotice?.let { notice ->
            snackbarHostState.showSnackbar(notice)
            viewModel.clearNotice()
        }
    }

    val isBottomBarVisible =
        currentRoute in Screen.bottomNavDestinations.map { it.route } &&
            activeReadingMaterial == null &&
            activeSibiBook == null &&
            !examSession.isActive

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            if (isBottomBarVisible) {
                NaikLevelBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { targetRoute ->
                        navController.navigate(targetRoute) {
                            popUpTo(
                                navController.graph.findStartDestination().id
                            ) {
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
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        userProgress = userProgress,
                        recentMaterial = recentMaterial,
                        dailyQuests = allQuests.filter {
                            it.type == "DAILY"
                        },
                        onOpenMaterial = { material ->
                            viewModel.openMaterialForReading(material)
                        },
                        onClaimQuest = { quest ->
                            viewModel.claimQuest(quest)
                        },
                        onOpenAiTutor = {
                            showAiTutorSheet = true
                        },
                        onNavigateToLearn = {
                            navController.navigate(Screen.Learn.route) {
                                popUpTo(
                                    navController.graph.findStartDestination().id
                                ) {
                                    saveState = true
                                }
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

                composable(Screen.Learn.route) {
                    LearningScreen(
                        materials = filteredMaterials,
                        sibiBooks = filteredSibiBooks,
                        filter = learningFilter,
                        sibiReadingPositions = sibiReadingPositions,
                        onFilterTabChange = {
                            viewModel.setFilterTab(it)
                        },
                        onFilterSchoolLevelChange = {
                            viewModel.setFilterSchoolLevel(it)
                        },
                        onFilterGradeChange = {
                            viewModel.setFilterGrade(it)
                        },
                        onFilterSubjectChange = {
                            viewModel.setFilterSubject(it)
                        },
                        onFilterCurriculumChange = {
                            viewModel.setFilterCurriculum(it)
                        },
                        onSearchQueryChange = {
                            viewModel.setSearchQuery(it)
                        },
                        onOpenMaterial = { material ->
                            viewModel.openMaterialForReading(material)
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
                                schoolLevel = if (
                                    learningFilter.schoolLevel != "Semua"
                                ) {
                                    learningFilter.schoolLevel
                                } else {
                                    userProgress?.schoolLevel ?: "SMA"
                                },
                                subject = if (
                                    learningFilter.subject != "Semua"
                                ) {
                                    learningFilter.subject
                                } else {
                                    "Semua"
                                }
                            )
                        },
                        onDeleteMaterial = { id ->
                            viewModel.deleteMaterial(id)
                        }
                    )
                }

                composable(Screen.Quest.route) {
                    QuestScreen(
                        userProgress = userProgress,
                        quests = allQuests,
                        onClaimQuest = { quest ->
                            viewModel.claimQuest(quest)
                        }
                    )
                }

                composable(Screen.Stats.route) {
                    StatisticsScreen(
                        materials = filteredMaterials,
                        examAttempts = examAttempts
                    )
                }

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

                composable("create_material") {
                    MaterialCreateScreen(
                        currentAttachment = activeDraftAttachment,
                        extractionStatus = extractionStatus,
                        onSetAttachment = { attachment ->
                            if (attachment != null) {
                                viewModel.setDraftAttachment(attachment)
                            } else {
                                viewModel.removeDraftAttachment()
                            }
                        },
                        onRemoveAttachment = {
                            viewModel.removeDraftAttachment()
                        },
                        onStartExtraction = { _, _, _, _ ->
                            viewModel.startExtraction()
                        },
                        onResetExtraction = {
                            viewModel.clearExtractionStatus()
                        },
                        onSaveMaterial = {
                                title,
                                description,
                                content,
                                level,
                                grade,
                                subject,
                                topic,
                                isDraft,
                                _,
                                _ ->
                            viewModel.saveCreatedMaterial(
                                title = title,
                                description = description,
                                content = content,
                                schoolLevel = level,
                                grade = grade,
                                subject = subject,
                                topic = topic,
                                isDraft = isDraft,
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

                composable("about") {
                    AboutScreen(
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }

            activeReadingMaterial?.let { material ->
                MaterialReaderScreen(
                    material = material,
                    onBack = {
                        viewModel.closeReading()
                    },
                    onProgressUpdate = { progress ->
                        viewModel.updateActiveReadingProgress(progress)
                    },
                    onMarkCompleted = {
                        viewModel.markCurrentMaterialFinished()
                    },
                    onAskAiTutor = { prompt ->
                        viewModel.sendAiTutorQuestion(prompt)
                        showAiTutorSheet = true
                    }
                )
            }

            activeSibiBook?.let { book ->
                SibiBookReaderScreen(
                    book = book,
                    currentChapterIndex = activeSibiChapterIndex,
                    onSelectChapter = { index ->
                        viewModel.selectSibiChapter(index)
                    },
                    onAskAiAboutBook = { prompt ->
                        viewModel.sendAiTutorQuestion(
                            prompt,
                            explicitSource = "Buku SIBI: ${book.title}"
                        )
                        showAiTutorSheet = true
                    },
                    onBack = {
                        viewModel.closeSibiBook()
                    }
                )
            }

            if (examSession.isActive) {
                ExamSimulationScreen(
                    session = examSession,
                    onSelectAnswer = { questionId, optionIndex ->
                        viewModel.selectExamAnswer(questionId, optionIndex)
                    },
                    onNextQuestion = {
                        viewModel.nextExamQuestion()
                    },
                    onPrevQuestion = {
                        viewModel.previousExamQuestion()
                    },
                    onSubmitExam = {
                        viewModel.submitExamSimulation()
                    },
                    onCloseExam = {
                        viewModel.closeExamSession()
                    }
                )
            }

            if (showAiTutorSheet) {
                AiTutorBottomSheet(
                    messages = tutorMessages,
                    isThinking = isTutorThinking,
                    contextSource = tutorContextSource,
                    onSendMessage = { query ->
                        viewModel.sendAiTutorQuestion(query)
                    },
                    onDismiss = {
                        showAiTutorSheet = false
                    }
                )
            }
        }
    }
}
