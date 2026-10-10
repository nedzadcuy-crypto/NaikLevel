package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.QuestEntity
import com.example.data.local.UserProgressEntity
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BlueSecondarySurface
import com.example.ui.theme.BlueSoftBg
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.SuccessContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningContainer
import com.example.ui.theme.WarningOrange

@Composable
fun QuestScreen(
    userProgress: UserProgressEntity?,
    quests: List<QuestEntity>,
    onClaimQuest: (QuestEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Harian, 1 = Mingguan, 2 = Prestasi

    val dailyQuests = quests.filter { it.type == "DAILY" }
    val weeklyQuests = quests.filter { it.type == "WEEKLY" }
    val xp = userProgress?.xp ?: 0
    val level = (xp / 100) + 1
    val streak = userProgress?.streakDays ?: 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .testTag("quest_screen")
    ) {
        // App Bar & Hero
        Surface(
            color = SurfaceWhite,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Misi & Gamifikasi",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Selesaikan misi berhadiah XP untuk meningkatkan level belajarmu",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(WarningContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = WarningOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Row: Level, Streak, Total XP
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Level Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = BlueSoftBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BlueSecondarySurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Level Kamu", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                            Text(
                                "Lv. $level",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BluePrimary
                                )
                            )
                        }
                    }

                    // Streak Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = WarningContainer),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarningOrange.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Streak Hari", style = MaterialTheme.typography.labelSmall.copy(color = WarningOrange))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = WarningOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "$streak Hari",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = WarningOrange
                                    )
                                )
                            }
                        }
                    }

                    // Total XP Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = SuccessContainer),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Total XP", style = MaterialTheme.typography.labelSmall.copy(color = SuccessGreen))
                            Text(
                                "$xp XP",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tabs: Harian, Mingguan, Lencana
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SurfaceWhite,
                    contentColor = BluePrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = BluePrimary
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Quest Harian", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium) },
                        modifier = Modifier.testTag("tab_quest_daily")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Quest Mingguan", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium) },
                        modifier = Modifier.testTag("tab_quest_weekly")
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Lencana Prestasi", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium) },
                        modifier = Modifier.testTag("tab_quest_achievements")
                    )
                }
            }
        }

        // List Content
        when (selectedTab) {
            0 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(dailyQuests) { quest ->
                        DailyQuestItemCard(quest = quest, onClaim = { onClaimQuest(quest) })
                    }
                    item {
                        com.example.ui.components.NaikLevelFooter()
                    }
                }
            }
            1 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(weeklyQuests) { quest ->
                        DailyQuestItemCard(quest = quest, onClaim = { onClaimQuest(quest) })
                    }
                    item {
                        com.example.ui.components.NaikLevelFooter()
                    }
                }
            }
            2 -> {
                // Achievements badges section
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        AchievementBadgeCard(
                            title = "Langkah Pertama",
                            desc = "Menyelesaikan 1 materi kurikulum pertama",
                            isUnlocked = xp >= 30,
                            icon = Icons.Default.WorkspacePremium
                        )
                    }
                    item {
                        AchievementBadgeCard(
                            title = "Pena Emas",
                            desc = "Menerbitkan materi belajar buatan sendiri ke perpustakaan",
                            isUnlocked = xp >= 65,
                            icon = Icons.Default.MilitaryTech
                        )
                    }
                    item {
                        AchievementBadgeCard(
                            title = "Ksatria Belajar",
                            desc = "Mencapai akumulasi 100 XP dan naik ke Level 2",
                            isUnlocked = level >= 2,
                            icon = Icons.Default.EmojiEvents
                        )
                    }
                    item {
                        AchievementBadgeCard(
                            title = "Sang Juara Ujian",
                            desc = "Menyelesaikan simulasi ujian berwaktu dengan skor tinggi",
                            isUnlocked = xp >= 120,
                            icon = Icons.Default.WorkspacePremium
                        )
                    }
                    item {
                        com.example.ui.components.NaikLevelFooter()
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementBadgeCard(
    title: String,
    desc: String,
    isUnlocked: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) SurfaceWhite else BlueSoftBg
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isUnlocked) SuccessContainer else BorderSubtle
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isUnlocked) SuccessContainer else BlueSecondarySurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isUnlocked) SuccessGreen else BluePrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) TextPrimary else TextSecondary
                    )
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isUnlocked) SuccessContainer else BlueSoftBg
            ) {
                Text(
                    text = if (isUnlocked) "Terbuka" else "Terkunci",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) SuccessGreen else TextSecondary,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
