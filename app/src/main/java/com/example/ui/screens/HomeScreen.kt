package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MaterialEntity
import com.example.data.local.QuestEntity
import com.example.data.local.UserProgressEntity
import com.example.ui.components.NaikLevelBrandHeader
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BlueSecondarySurface
import com.example.ui.theme.BlueSoftBg
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.SuccessContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningContainer
import com.example.ui.theme.WarningOrange

@Composable
fun HomeScreen(
    userProgress: UserProgressEntity?,
    recentMaterial: MaterialEntity?,
    dailyQuests: List<QuestEntity>,
    onOpenMaterial: (MaterialEntity) -> Unit,
    onClaimQuest: (QuestEntity) -> Unit,
    onOpenAiTutor: () -> Unit,
    onNavigateToLearn: () -> Unit,
    onStartExamSimulation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val xp = userProgress?.xp ?: 0
    val level = (xp / 100) + 1
    val progressInLevel = (xp % 100) / 100f
    val animatedProgress by animateFloatAsState(targetValue = progressInLevel, label = "levelProgress")
    val streak = userProgress?.streakDays ?: 0
    val userName = userProgress?.userName ?: "Pelajar Indonesia"

    val levelTitle = when (level) {
        1 -> "Level 1 • Pelajar Pemula"
        2 -> "Level 2 • Penjelajah Ilmu"
        3 -> "Level 3 • Pejuang Pintar"
        4 -> "Level 4 • Cendekiawan Muda"
        else -> "Level $level • Master Edukasi"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NaikLevelBrandHeader()

                // Streak Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = WarningContainer,
                    modifier = Modifier.testTag("user_streak_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = "Streak Belajar",
                            tint = WarningOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$streak Hari",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = WarningOrange
                            )
                        )
                    }
                }
            }
        }

        // Welcome Greeting
        item {
            Column {
                Text(
                    text = "Halo, $userName! 👋",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Siap naik level hari ini? Terus belajar untuk menguasai materi impianmu.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            }
        }

        // Gamification Level & XP Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("level_progress_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = levelTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BluePrimary
                                )
                            )
                            Text(
                                text = "$xp XP Terkumpul",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BlueSecondarySurface
                        ) {
                            Text(
                                text = "Lv. $level",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BluePrimary
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = BluePrimary,
                        trackColor = BlueSecondarySurface,
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val xpToNext = 100 - (xp % 100)
                    Text(
                        text = "$xpToNext XP lagi untuk naik ke Level ${level + 1}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        // AI Tutor Banner Shortcut
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenAiTutor() }
                    .testTag("ai_tutor_shortcut_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BlueSoftBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, BlueSecondarySurface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(BluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "Tutor AI",
                            tint = SurfaceWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tanya Tutor AI NaikLevel",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Dapatkan bimbingan konsep & penjelasan soal instan",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BluePrimary
                    ) {
                        Text(
                            text = "Tanya",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SurfaceWhite,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Continue Learning Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lanjutkan Belajar",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Lihat Semua",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = BluePrimary,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier
                        .clickable { onNavigateToLearn() }
                        .padding(4.dp)
                )
            }
        }

        item {
            if (recentMaterial != null) {
                // Actual reading progress card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenMaterial(recentMaterial) }
                        .testTag("recent_material_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BlueSecondarySurface
                            ) {
                                Text(
                                    text = "${recentMaterial.schoolLevel} • Kelas ${recentMaterial.grade} • ${recentMaterial.subject}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = BluePrimary,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            if (recentMaterial.isCompleted) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SuccessContainer
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selesai",
                                            tint = SuccessGreen,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Selesai",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = SuccessGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = recentMaterial.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = recentMaterial.description,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val percentage = (recentMaterial.readProgress * 100).toInt()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progres: $percentage%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = "${recentMaterial.estimatedReadMinutes} mnt baca",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { recentMaterial.readProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = BluePrimary,
                            trackColor = BlueSecondarySurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { onOpenMaterial(recentMaterial) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("continue_reading_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (recentMaterial.isCompleted) "Baca Ulang Materi" else "Lanjutkan Membaca")
                        }
                    }
                }
            } else {
                // Meaningful Empty State for New Users
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("empty_learning_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(BlueSoftBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Book,
                                contentDescription = null,
                                tint = BluePrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Belum Ada Materi yang Dibuka",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pilih materi dari kurikulum SD, SMP, SMA, atau SMK untuk memulai progres belajarmu hari ini.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onNavigateToLearn,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            modifier = Modifier.testTag("start_learning_button")
                        ) {
                            Text("Mulai Belajar Sekarang")
                        }
                    }
                }
            }
        }

        // Exam Quick Action
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStartExamSimulation() }
                    .testTag("exam_quick_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(WarningContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = "Simulasi Ujian",
                            tint = WarningOrange,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Simulasi Ujian Berwaktu",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Uji pemahamanmu dan raih skor tertinggi",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                    OutlinedButton(
                        onClick = onStartExamSimulation,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Mulai")
                    }
                }
            }
        }

        // Daily Quests Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = WarningOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Quest Harian",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }
            }
        }

        if (dailyQuests.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Memuat quest harianmu...",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
            }
        } else {
            items(dailyQuests) { quest ->
                DailyQuestItemCard(quest = quest, onClaim = { onClaimQuest(quest) })
            }
        }

        item {
            com.example.ui.components.NaikLevelFooter()
        }
    }
}

@Composable
fun DailyQuestItemCard(
    quest: QuestEntity,
    onClaim: () -> Unit
) {
    val isCompleted = quest.currentCount >= quest.targetCount
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quest_item_${quest.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (quest.isClaimed) SuccessContainer else BorderSubtle
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = quest.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BlueSecondarySurface
                    ) {
                        Text(
                            text = "+${quest.xpReward} XP",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = quest.description,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                val progress = (quest.currentCount.toFloat() / quest.targetCount.toFloat()).coerceIn(0f, 1f)
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (isCompleted) SuccessGreen else BluePrimary,
                        trackColor = BlueSecondarySurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${quest.currentCount}/${quest.targetCount}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            when {
                quest.isClaimed -> {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SuccessContainer
                    ) {
                        Text(
                            text = "Sudah Diklaim",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
                isCompleted -> {
                    Button(
                        onClick = onClaim,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("claim_quest_button_${quest.id}")
                    ) {
                        Text(
                            text = "Klaim",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SurfaceWhite,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                else -> {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BlueSoftBg
                    ) {
                        Text(
                            text = "Berjalan",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
