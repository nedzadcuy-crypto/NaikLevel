package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProgressEntity
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BlueSecondarySurface
import com.example.ui.theme.BlueSoftBg
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningContainer
import com.example.ui.theme.WarningOrange

@Composable
fun ProfileScreen(
    userProgress: UserProgressEntity?,
    accountEmail: String = "",
    emailVerified: Boolean = true,
    onSignOut: () -> Unit = {},
    onResendVerification: () -> Unit = {},
    onUpdateProfile: (name: String, schoolLevel: String, grade: Int) -> Unit,
    onTriggerSync: () -> Unit,
    onResetData: () -> Unit,
    onNavigateToAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editName by remember(userProgress?.userName) { mutableStateOf(userProgress?.userName ?: "Pelajar Indonesia") }
    var selectedLevel by remember(userProgress?.schoolLevel) { mutableStateOf(userProgress?.schoolLevel ?: "SMA") }
    var selectedGrade by remember(userProgress?.grade) { mutableIntStateOf(userProgress?.grade ?: 10) }
    var showResetDialog by remember { mutableStateOf(false) }

    val schoolLevels = listOf("SD", "SMP", "SMA", "SMK")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Akun
        if (accountEmail.isNotBlank()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.testTag("account_card")) {
                    Text("Akun", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                    Text(accountEmail, style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
                    if (!emailVerified) {
                        Text("Email belum diverifikasi.", style = MaterialTheme.typography.bodySmall.copy(color = WarningOrange))
                        androidx.compose.material3.TextButton(onClick = onResendVerification) { Text("Kirim ulang email verifikasi") }
                    }
                    androidx.compose.material3.OutlinedButton(
                        onClick = onSignOut,
                        modifier = Modifier.testTag("sign_out_button")
                    ) { Text("Keluar") }
                }
            }
        }

        // Top Header
        item {
            Column {
                Text(
                    text = "Profil & Pengaturan",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Kelola data akun, jenjang sekolah, dan kendali privasi belajarmu",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }
        }

        // User Avatar & Identity Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BlueSoftBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, BlueSecondarySurface)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(BluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = SurfaceWhite,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userProgress?.userName ?: "Pelajar Indonesia",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${userProgress?.schoolLevel ?: "SMA"} • Kelas ${userProgress?.grade ?: 10}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BluePrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = "${userProgress?.xp ?: 0} XP • ${userProgress?.streakDays ?: 0} Hari Streak",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
            }
        }

        // Profile Settings Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pengaturan Profil Belajar",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Nama Panggilan",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_name_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Jenjang Sekolah",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        schoolLevels.forEach { lvl ->
                            FilterChip(
                                selected = selectedLevel == lvl,
                                onClick = {
                                    selectedLevel = lvl
                                    selectedGrade = when (lvl) {
                                        "SD" -> 4
                                        "SMP" -> 7
                                        "SMA" -> 10
                                        "SMK" -> 10
                                        else -> 10
                                    }
                                },
                                label = { Text(lvl) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BlueSecondarySurface,
                                    selectedLabelColor = BluePrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { onUpdateProfile(editName, selectedLevel, selectedGrade) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_profile_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        Text("Simpan Perubahan")
                    }
                }
            }
        }

        // Account & Synchronization Status
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Status Akun & Sinkronisasi",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Penyimpanan Utama: Lokal (Offline-First)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Seluruh materi, draf, riwayat ujian, dan transaksi XP disimpan di database Room lokal perangkatmu agar dapat diakses kapan pun tanpa kuota internet.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Sync state badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (userProgress?.syncStatus) {
                            "PENDING" -> WarningContainer
                            "SYNCED" -> SuccessContainer
                            else -> BlueSoftBg
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (userProgress?.syncStatus == "PENDING") Icons.Default.Sync else Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = when (userProgress?.syncStatus) {
                                    "PENDING" -> WarningOrange
                                    "SYNCED" -> SuccessGreen
                                    else -> BluePrimary
                                },
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (userProgress?.syncStatus) {
                                    "PENDING" -> "Memeriksa sinkronisasi..."
                                    "SYNCED" -> "Tersinkronisasi dengan Cloud"
                                    else -> "Tersimpan Aman di Penyimpanan Lokal (Offline Siap)"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = when (userProgress?.syncStatus) {
                                        "PENDING" -> WarningOrange
                                        "SYNCED" -> SuccessGreen
                                        else -> BluePrimary
                                    }
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onTriggerSync,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("check_sync_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Periksa & Cek Sinkronisasi")
                    }
                }
            }
        }

        // Privacy and Data Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Privasi & Kendali Data",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Data belajarmu adalah milik pribadimu. Kamu dapat menghapus riwayat atau mengatur ulang data kapan saja.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { showResetDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reset_data_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                    ) {
                        Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset Riwayat Belajar & Ujian")
                    }
                }
            }
        }

        // About Application Card (Navigation Destination)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToAbout() }
                    .testTag("about_app_entry_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BlueSecondarySurface)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(BlueSecondarySurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tentang NaikLevel",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Belajar Modern • Info pengembang & hak cipta",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Buka Tentang",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        item {
            com.example.ui.components.NaikLevelFooter()
        }
    }

    // Reset confirmation dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text("Reset Riwayat Belajar?")
            },
            text = {
                Text("Tindakan ini akan mengosongkan riwayat simulasi ujian dan mengatur ulang akumulasi XP ke awal. Materi pembelajaran kurikulum tetap utuh.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        onResetData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Ya, Reset Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
