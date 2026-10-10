package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MaterialEntity
import com.example.data.model.SibiBook
import com.example.data.model.SibiCatalog
import com.example.data.sibi.SibiLinks
import com.example.ui.components.NaikLevelFooter
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BlueSecondarySurface
import com.example.ui.theme.BlueSoftBg
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningContainer
import com.example.ui.theme.WarningOrange
import com.example.ui.viewmodel.LearningFilter

@Composable
fun LearningScreen(
    materials: List<MaterialEntity>,
    sibiBooks: List<SibiBook>,
    filter: LearningFilter,
    sibiReadingPositions: Map<String, Int> = emptyMap(),
    onFilterTabChange: (Int) -> Unit,
    onFilterSchoolLevelChange: (String) -> Unit,
    onFilterGradeChange: (Int) -> Unit = {},
    onFilterSubjectChange: (String) -> Unit,
    onFilterCurriculumChange: (String) -> Unit = {},
    onSearchQueryChange: (String) -> Unit,
    onOpenMaterial: (MaterialEntity) -> Unit,
    onOpenSibiBook: (SibiBook) -> Unit,
    onAddNewMaterial: () -> Unit,
    onOpenCustomExamBuilder: () -> Unit,
    onStartExamSimulation: () -> Unit,
    onDeleteMaterial: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val schoolLevels = listOf("Semua", "SD", "SMP", "SMA", "SMK")
    val gradeOptions = when (filter.schoolLevel) {
        "SD" -> listOf(0 to "Semua", 1 to "Kelas 1", 4 to "Kelas 4", 5 to "Kelas 5")
        "SMP" -> listOf(0 to "Semua", 7 to "Kelas 7", 8 to "Kelas 8")
        "SMA" -> listOf(0 to "Semua", 10 to "Kelas 10", 11 to "Kelas 11")
        "SMK" -> listOf(0 to "Semua", 10 to "Kelas 10")
        else -> listOf(0 to "Semua", 1 to "Kelas 1", 4 to "Kelas 4", 5 to "Kelas 5", 7 to "Kelas 7", 8 to "Kelas 8", 10 to "Kelas 10", 11 to "Kelas 11")
    }
    val subjects = SibiCatalog.getDistinctSubjects()
    val curriculums = listOf("Semua", "Kurikulum Merdeka")

    val catalogNotice = SibiCatalog.getCatalogNotice(filter.subject)

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("learning_screen")
        ) {
            // Header Bar & Actions
            Surface(
                color = SurfaceWhite,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Perpustakaan Belajar",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Buku Resmi SIBI Kemendikdasmen & Kurikulum Nasional",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }

                        // Exam Shortcut Button
                        Button(
                            onClick = onOpenCustomExamBuilder,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            modifier = Modifier.testTag("custom_exam_header_button")
                        ) {
                            Icon(imageVector = Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Buat Ujian", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search Bar
                    OutlinedTextField(
                        value = filter.searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_material_input"),
                        placeholder = {
                            Text(
                                "Cari judul buku, materi, atau topik...",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 13.sp)
                            )
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Cari", tint = TextMuted)
                        },
                        trailingIcon = {
                            if (filter.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Hapus teks")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = BorderSubtle,
                            focusedContainerColor = BlueSoftBg.copy(alpha = 0.5f),
                            unfocusedContainerColor = BlueSoftBg.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Navigation Tabs: Kurikulum Nasional | Buku Resmi (SIBI) | Materi Saya
                    TabRow(
                        selectedTabIndex = filter.activeTab,
                        containerColor = SurfaceWhite,
                        contentColor = BluePrimary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[filter.activeTab]),
                                color = BluePrimary
                            )
                        }
                    ) {
                        Tab(
                            selected = filter.activeTab == 0,
                            onClick = { onFilterTabChange(0) },
                            text = {
                                Text(
                                    text = "Kurikulum",
                                    fontWeight = if (filter.activeTab == 0) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            modifier = Modifier.testTag("tab_kurikulum_nasional")
                        )
                        Tab(
                            selected = filter.activeTab == 1,
                            onClick = { onFilterTabChange(1) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(14.dp), tint = SuccessGreen)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Buku Resmi SIBI",
                                        fontWeight = if (filter.activeTab == 1) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            },
                            modifier = Modifier.testTag("tab_buku_resmi_sibi")
                        )
                        Tab(
                            selected = filter.activeTab == 2,
                            onClick = { onFilterTabChange(2) },
                            text = {
                                Text(
                                    text = "Materi Saya",
                                    fontWeight = if (filter.activeTab == 2) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            modifier = Modifier.testTag("tab_buatan_saya")
                        )
                    }
                }
            }

            // Horizontal Filter Chips: Jenjang (SD, SMP, SMA, SMK)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Jenjang:",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
                )
                schoolLevels.forEach { level ->
                    val isSelected = filter.schoolLevel == level
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterSchoolLevelChange(level) },
                        label = { Text(level) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlueSecondarySurface,
                            selectedLabelColor = BluePrimary
                        ),
                        modifier = Modifier.testTag("filter_level_$level")
                    )
                }
            }

            // Horizontal Filter Chips: Kelas / Grade
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kelas:",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
                )
                gradeOptions.forEach { (gradeVal, gradeLabel) ->
                    val isSelected = filter.grade == gradeVal
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterGradeChange(gradeVal) },
                        label = { Text(gradeLabel) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlueSecondarySurface,
                            selectedLabelColor = BluePrimary
                        ),
                        modifier = Modifier.testTag("filter_grade_$gradeVal")
                    )
                }
            }

            // Horizontal Filter Chips: Mata Pelajaran
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mapel:",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
                )
                subjects.forEach { subj ->
                    val isSelected = filter.subject == subj
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterSubjectChange(subj) },
                        label = { Text(subj) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlueSecondarySurface,
                            selectedLabelColor = BluePrimary
                        ),
                        modifier = Modifier.testTag("filter_subject_$subj")
                    )
                }
            }

            // TAB 1: BUKU RESMI (SIBI KEMENDIKDASMEN)
            if (filter.activeTab == 1) {
                if (sibiBooks.isEmpty()) {
                    SibiOfficialHub(
                        initialQuery = filter.searchQuery,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            // SIBI Official Header Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = SuccessContainer),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Katalog Buku Resmi SIBI Kemendikdasmen",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = SuccessGreen
                                            )
                                        )
                                        Text(
                                            text = "Buku pada daftar ini berasal dari sumber yang tersedia di aplikasi. Buka katalog resmi SIBI untuk edisi lengkap.",
                                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 11.sp)
                                        )
                                    }
                                }
                            }
                        }

                        // Target Notice Card if Subject has fewer than 5 books
                        if (catalogNotice != null) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = WarningContainer,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, WarningOrange.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = catalogNotice,
                                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 11.sp)
                                        )
                                    }
                                }
                            }
                        }

                        items(sibiBooks, key = { it.id }) { book ->
                            val lastPos = sibiReadingPositions[book.id]
                            SibiBookItemCard(
                                book = book,
                                lastReadChapter = lastPos,
                                onOpen = { onOpenSibiBook(book) },
                                onOpenExternal = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(SibiLinks.safeSourceUrlOrCatalog(book.officialUrl)))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        // Ignore
                                    }
                                }
                            )
                        }

                        item {
                            NaikLevelFooter()
                        }
                    }
                }
            } else {
                // TABS 0 & 2: KURIKULUM NASIONAL & MATERI SAYA
                if (materials.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(BlueSoftBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (filter.activeTab == 0) Icons.Default.Search else Icons.Default.EditNote,
                                        contentDescription = null,
                                        tint = BluePrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = if (filter.activeTab == 0)
                                        "Tidak Ada Materi yang Cocok"
                                    else
                                        "Belum Ada Materi Buatanmu",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (filter.activeTab == 0)
                                        "Coba ubah kata kunci pencarian atau sesuaikan filter jenjang dan mata pelajaran di atas."
                                    else
                                        "Buat materi belajarmu dengan foto kamera, pilih gambar galeri, atau unggah dokumen PDF.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Quick Action Header Card on User Created Tab
                        if (filter.activeTab == 2) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = BlueSoftBg),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BlueSecondarySurface)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(BluePrimary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Quiz,
                                                contentDescription = null,
                                                tint = SurfaceWhite,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Buat Ujian dari Materi Saya",
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = BluePrimary
                                                )
                                            )
                                            Text(
                                                text = "Buat soal latihan otomatis dari catatan yang kamu tambahkan",
                                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                                            )
                                        }
                                        Button(
                                            onClick = onOpenCustomExamBuilder,
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text("Mulai", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        items(materials, key = { it.id }) { material ->
                            MaterialItemCard(
                                material = material,
                                onOpen = { onOpenMaterial(material) },
                                onDelete = if (material.source == "USER_CREATED") {
                                    { onDeleteMaterial(material.id) }
                                } else null
                            )
                        }

                        item {
                            NaikLevelFooter()
                        }
                    }
                }
            }
        }

        // Floating Action Button to Add Material (visible on Tab 2)
        if (filter.activeTab == 2) {
            FloatingActionButton(
                onClick = onAddNewMaterial,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 20.dp, end = 20.dp)
                    .testTag("fab_add_material"),
                containerColor = BluePrimary,
                contentColor = SurfaceWhite
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Materi")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Tambah Materi",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun SibiBookItemCard(
    book: SibiBook,
    lastReadChapter: Int? = null,
    onOpen: () -> Unit,
    onOpenExternal: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("sibi_book_card_${book.id}"),
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SuccessContainer
                    ) {
                        Text(
                            text = "${book.schoolLevel} • Kelas ${book.grade}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BlueSecondarySurface
                    ) {
                        Text(
                            text = book.subject,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = BlueSoftBg
                ) {
                    Text(
                        text = "${book.year} • ${book.curriculum}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(book.coverColorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Penulis: ${book.authors}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
                        maxLines = 1
                    )
                    Text(
                        text = "Penerbit: ${book.publisher}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = book.synopsis,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    lineHeight = 18.sp
                ),
                maxLines = 2
            )

            if (lastReadChapter != null && lastReadChapter >= 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = BlueSoftBg
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Posisi Terakhir: Bab ${lastReadChapter + 1}",
                            style = MaterialTheme.typography.labelSmall.copy(color = BluePrimary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpen,
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("read_sibi_book_button_${book.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (lastReadChapter != null) "Lanjutkan Membaca" else "Baca di Aplikasi", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onOpenExternal,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(0.9f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Portal SIBI", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun MaterialItemCard(
    material: MaterialEntity,
    onOpen: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("material_card_${material.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BlueSecondarySurface
                    ) {
                        Text(
                            text = "${material.schoolLevel} • Kelas ${material.grade}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BlueSoftBg
                    ) {
                        Text(
                            text = material.subject,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (material.isDraft) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = WarningContainer
                        ) {
                            Text(
                                text = "DRAFT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WarningOrange,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (material.isCompleted) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selesai",
                            tint = SuccessGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Selesai",
                            style = MaterialTheme.typography.labelSmall.copy(color = SuccessGreen, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = material.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = material.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    lineHeight = 18.sp
                ),
                maxLines = 2
            )

            // Attachment indicator if any
            if (!material.attachmentName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BlueSoftBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = BluePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Lampiran: ${material.attachmentName}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BluePrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reading Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progres: ${(material.readProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 11.sp)
                    )
                    Text(
                        text = "~${material.estimatedReadMinutes} mnt baca",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 11.sp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { material.readProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (material.isCompleted) SuccessGreen else BluePrimary,
                    trackColor = BlueSecondarySurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onOpen,
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("read_material_button_${material.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (material.readProgress > 0) "Lanjutkan Baca" else "Mulai Baca",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (onDelete != null) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("delete_material_button_${material.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus Materi",
                            tint = ErrorRed
                        )
                    }
                }
            }
        }
    }
}


/**
 * Perpustakaan SIBI: katalog resmi tidak menyediakan API publik yang terdokumentasi, sehingga NaikLevel
 * tidak menampilkan daftar buku tiruan. Pengguna dibantu menuju katalog resmi dan query-nya disalin
 * agar dapat ditempel di kolom pencarian SIBI.
 */
@Composable
fun SibiOfficialHub(
    initialQuery: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var query by remember(initialQuery) { mutableStateOf(initialQuery) }
    var notice by remember { mutableStateOf<String?>(null) }

    fun open(url: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(SibiLinks.safeSourceUrlOrCatalog(url))))
        } catch (e: Exception) {
            notice = "Tidak ada aplikasi peramban untuk membuka tautan ini."
        }
    }

    androidx.compose.foundation.lazy.LazyColumn(
        modifier = modifier.testTag("sibi_official_hub"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Perpustakaan SIBI",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
            Text(
                text = "Buku pendidikan resmi dari Sistem Informasi Perbukuan Indonesia (Kemendikdasmen). Pencarian dan unduhan dilakukan langsung di situs resmi SIBI.",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
        }
        item {
            androidx.compose.material3.OutlinedTextField(
                value = query,
                onValueChange = { query = it.take(100); notice = null },
                label = { Text("Judul atau mata pelajaran") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("sibi_hub_query")
            )
        }
        item {
            androidx.compose.material3.Button(
                onClick = {
                    val q = query.trim()
                    if (q.isNotEmpty()) {
                        val cm = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE)
                            as android.content.ClipboardManager
                        cm.setPrimaryClip(android.content.ClipData.newPlainText("Pencarian SIBI", q))
                        notice = "\"$q\" disalin. Tempel di kolom pencarian SIBI."
                    }
                    open(SibiLinks.CATALOG_URL)
                },
                modifier = Modifier.fillMaxWidth().testTag("sibi_hub_open_catalog")
            ) { Text("Cari di katalog SIBI") }
        }
        item {
            androidx.compose.material3.OutlinedButton(
                onClick = { open(SibiLinks.HOME_URL) },
                modifier = Modifier.fillMaxWidth().testTag("sibi_hub_open_home")
            ) { Text("Buka situs SIBI") }
        }
        notice?.let {
            item {
                Text(it, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
            }
        }
        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = WarningContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Tutor AI hanya memberi label \"berdasarkan buku SIBI\" bila materi dokumen SIBI benar-benar tersedia dan dipakai. Saat ini jawaban dari Tutor AI ditandai sebagai belum diverifikasi SIBI.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 11.sp),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
