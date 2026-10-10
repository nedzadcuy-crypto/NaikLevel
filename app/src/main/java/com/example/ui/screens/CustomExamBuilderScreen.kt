package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MaterialEntity
import com.example.data.model.CustomExamConfig
import com.example.data.model.SibiBook
import com.example.ui.components.NaikLevelFooter
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

@Composable
fun CustomExamBuilderScreen(
    materials: List<MaterialEntity>,
    sibiBooks: List<SibiBook> = emptyList(),
    onStartExam: (CustomExamConfig) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var examTitle by remember { mutableStateOf("Simulasi Ujian Mandiri NaikLevel") }
    val selectedMaterialIds = remember { mutableStateListOf<Long>() }
    val selectedSibiBookIds = remember { mutableStateListOf<String>() }

    var sourceTab by remember { mutableIntStateOf(if (sibiBooks.isEmpty()) 1 else 0) } // 0: Buku SIBI, 1: Materi Saya
    var questionCount by remember { mutableIntStateOf(5) }
    var selectedDifficulty by remember { mutableStateOf("Sedang") }
    var selectedType by remember { mutableStateOf("Semua Tipe") }
    var timeLimitMinutes by remember { mutableIntStateOf(5) } // 0 = untimed

    // Pre-select first SIBI book or material if nothing selected
    remember(sibiBooks, materials) {
        if (selectedSibiBookIds.isEmpty() && sibiBooks.isNotEmpty()) {
            selectedSibiBookIds.add(sibiBooks.first().id)
        } else if (selectedMaterialIds.isEmpty() && materials.isNotEmpty()) {
            selectedMaterialIds.add(materials.first().id)
        }
    }

    val totalSelectedSources = selectedMaterialIds.size + selectedSibiBookIds.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .imePadding()
            .testTag("custom_exam_builder_screen")
    ) {
        // App Bar
        Surface(
            color = SurfaceWhite,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("custom_exam_back_button")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Generator Ujian Kustom",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Buat soal latihan dari catatan pribadi (dan buku SIBI jika tersedia)",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Exam Title Input
            Text(
                text = "Judul Simulasi Ujian",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            OutlinedTextField(
                value = examTitle,
                onValueChange = { examTitle = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom_exam_title_input"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = BorderSubtle
                )
            )

            // Step 1: Source Selection Tabs
            Text(
                text = "Pilih Sumber Bahan Ujian ($totalSelectedSources terpilih) *",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            TabRow(
                selectedTabIndex = sourceTab,
                containerColor = SurfaceWhite,
                contentColor = BluePrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[sourceTab]),
                        color = BluePrimary
                    )
                }
            ) {
                Tab(
                    selected = sourceTab == 0,
                    onClick = { sourceTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(14.dp), tint = SuccessGreen)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Buku Resmi SIBI (${selectedSibiBookIds.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = sourceTab == 1,
                    onClick = { sourceTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.NoteAlt, contentDescription = null, modifier = Modifier.size(14.dp), tint = BluePrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Catatan Saya (${selectedMaterialIds.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            if (sourceTab == 0) {
                // SIBI Books List
                if (sibiBooks.isEmpty()) {
                    Text("Daftar buku SIBI belum tersedia di aplikasi. Gunakan tab Catatan Saya, atau buka katalog resmi SIBI dari menu Belajar.", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sibiBooks.forEach { book ->
                            val isChecked = selectedSibiBookIds.contains(book.id)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isChecked) selectedSibiBookIds.remove(book.id)
                                        else selectedSibiBookIds.add(book.id)
                                    }
                                    .testTag("select_sibi_book_${book.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isChecked) BlueSecondarySurface else SurfaceWhite
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isChecked) BluePrimary else BorderSubtle
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { checked ->
                                            if (checked) selectedSibiBookIds.add(book.id)
                                            else selectedSibiBookIds.remove(book.id)
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = BluePrimary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(book.coverColorHex)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = book.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        )
                                        Text(
                                            text = "${book.schoolLevel} Kelas ${book.grade} • ${book.subject} • ${book.chapters.size} Bab resmi",
                                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Personal Materials List
                if (materials.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BlueSoftBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BlueSecondarySurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Belum Ada Catatan Tersimpan",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BluePrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Silakan tambah materi catatan di menu Belajar ('+ Tambah Materi'), atau gunakan Buku Resmi SIBI pada tab di atas.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        materials.forEach { material ->
                            val isChecked = selectedMaterialIds.contains(material.id)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isChecked) selectedMaterialIds.remove(material.id)
                                        else selectedMaterialIds.add(material.id)
                                    }
                                    .testTag("select_material_checkbox_${material.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isChecked) BlueSecondarySurface else SurfaceWhite
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isChecked) BluePrimary else BorderSubtle
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { checked ->
                                            if (checked) selectedMaterialIds.add(material.id)
                                            else selectedMaterialIds.remove(material.id)
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = BluePrimary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = material.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        )
                                        Text(
                                            text = "${material.schoolLevel} • Kelas ${material.grade} • ${material.subject} • ${material.topic}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Step 2: Configure Questions Count
            Text(
                text = "Jumlah Soal",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(3, 5, 8, 10, 15).forEach { count ->
                    FilterChip(
                        selected = questionCount == count,
                        onClick = { questionCount = count },
                        label = { Text("$count Soal") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlueSecondarySurface,
                            selectedLabelColor = BluePrimary
                        )
                    )
                }
            }

            // Step 3: Difficulty
            Text(
                text = "Tingkat Kesulitan",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Mudah", "Sedang", "Sulit").forEach { diff ->
                    FilterChip(
                        selected = selectedDifficulty == diff,
                        onClick = { selectedDifficulty = diff },
                        label = { Text(diff) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlueSecondarySurface,
                            selectedLabelColor = BluePrimary
                        )
                    )
                }
            }

            // Step 4: Question Type
            Text(
                text = "Tipe Soal",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Semua Tipe", "Pilihan Ganda", "Benar/Salah").forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlueSecondarySurface,
                            selectedLabelColor = BluePrimary
                        )
                    )
                }
            }

            // Step 5: Time Limit
            Text(
                text = "Batas Waktu Pengerjaan",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair(3, "3 Menit"),
                    Pair(5, "5 Menit"),
                    Pair(10, "10 Menit"),
                    Pair(15, "15 Menit"),
                    Pair(0, "Tanpa Batas Waktu")
                ).forEach { (minutes, label) ->
                    FilterChip(
                        selected = timeLimitMinutes == minutes,
                        onClick = { timeLimitMinutes = minutes },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlueSecondarySurface,
                            selectedLabelColor = BluePrimary
                        )
                    )
                }
            }

            // Grounding Guarantee Banner
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SuccessContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Soal dibuat dari teks catatan/bab yang kamu pilih. Kunci jawaban dan pembahasan menyertakan rujukan bab & halaman asli.",
                        style = MaterialTheme.typography.bodySmall.copy(color = SuccessGreen, fontSize = 11.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Start Exam Button
            Button(
                onClick = {
                    onStartExam(
                        CustomExamConfig(
                            examTitle = examTitle.ifBlank { "Ujian Kustom NaikLevel" },
                            selectedMaterialIds = selectedMaterialIds.toList(),
                            selectedSibiBookIds = selectedSibiBookIds.toList(),
                            questionCount = questionCount,
                            difficulty = selectedDifficulty,
                            questionType = selectedType,
                            timeLimitMinutes = timeLimitMinutes
                        )
                    )
                },
                enabled = totalSelectedSources > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("generate_custom_exam_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Icon(imageVector = Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mulai Ujian ($questionCount Soal)", fontWeight = FontWeight.Bold)
            }

            NaikLevelFooter()
        }
    }
}
