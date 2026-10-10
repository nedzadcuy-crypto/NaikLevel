package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.AttachmentManager
import com.example.data.local.SavedAttachment
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
import com.example.ui.viewmodel.ExtractionStatus
import java.io.File

@Composable
fun MaterialCreateScreen(
    currentAttachment: SavedAttachment?,
    extractionStatus: ExtractionStatus,
    onSetAttachment: (SavedAttachment?) -> Unit,
    onRemoveAttachment: () -> Unit,
    onStartExtraction: (String, String, String?, Boolean) -> Unit,
    onResetExtraction: () -> Unit,
    onSaveMaterial: (
        title: String,
        description: String,
        content: String,
        schoolLevel: String,
        grade: Int,
        subject: String,
        topic: String,
        isDraft: Boolean,
        attachment: SavedAttachment?,
        attachmentNote: String?
    ) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf("SMA") }
    var selectedGrade by remember { mutableIntStateOf(10) }
    var selectedSubject by remember { mutableStateOf("Matematika") }
    var topic by remember { mutableStateOf("") }
    var attachmentFeedbackNotice by remember { mutableStateOf<String?>(null) }

    val schoolLevels = listOf("SD", "SMP", "SMA", "SMK")
    val subjects = listOf("Matematika", "IPA", "IPS", "Bahasa Indonesia", "Bahasa Inggris", "Informatika/RPL", "Ekonomi", "Fisika")

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val saved = AttachmentManager.saveBitmap(context, bitmap, prefix = "kamera")
            if (saved != null) {
                onSetAttachment(saved)
                attachmentFeedbackNotice = "Foto berhasil diambil dan disimpan (${saved.displayName})"
            } else {
                attachmentFeedbackNotice = "Gagal menyimpan foto kamera ke penyimpanan perangkat."
            }
        } else {
            attachmentFeedbackNotice = "Pengambilan foto dibatalkan."
        }
    }

    // Gallery Picker Launcher (Android Photo Picker)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val saved = AttachmentManager.saveUriAttachment(context, uri, isImage = true)
            if (saved != null) {
                onSetAttachment(saved)
                attachmentFeedbackNotice = "Gambar galeri berhasil dipilih: ${saved.displayName}"
            } else {
                attachmentFeedbackNotice = "Gagal menyalin gambar dari galeri."
            }
        }
    }

    // Document Picker Launcher (PDF & Documents)
    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val saved = AttachmentManager.saveUriAttachment(context, uri, isImage = false)
            if (saved != null) {
                onSetAttachment(saved)
                attachmentFeedbackNotice = "Dokumen berhasil dipilih: ${saved.displayName}"
                if (saved.extractedText != null && content.isBlank()) {
                    content = saved.extractedText
                }
            } else {
                attachmentFeedbackNotice = "Gagal memproses dokumen yang dipilih."
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .imePadding()
            .testTag("material_create_screen")
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
                    modifier = Modifier.testTag("create_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali"
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tambah Materi Belajar",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Ambil foto, pilih dokumen PDF, atau tulis catatan pribadi",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }
        }

        // Form Fields
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Three Clear Attachment Actions
            Text(
                text = "Lampirkan Berkas Materi:",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Ambil Foto
                AttachmentActionButton(
                    icon = Icons.Default.CameraAlt,
                    label = "Ambil Foto",
                    onClick = { cameraLauncher.launch(null) },
                    modifier = Modifier.weight(1f),
                    testTag = "btn_take_photo"
                )

                // 2. Pilih dari Galeri
                AttachmentActionButton(
                    icon = Icons.Default.PhotoLibrary,
                    label = "Pilih Galeri",
                    onClick = {
                        galleryLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    testTag = "btn_pick_gallery"
                )

                // 3. Pilih Dokumen (PDF, txt)
                AttachmentActionButton(
                    icon = Icons.Default.Description,
                    label = "Pilih Dokumen",
                    onClick = {
                        documentLauncher.launch(
                            arrayOf("application/pdf", "text/plain", "application/*")
                        )
                    },
                    modifier = Modifier.weight(1f),
                    testTag = "btn_pick_document"
                )
            }

            // Attachment Feedback banner
            attachmentFeedbackNotice?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BlueSoftBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall.copy(color = BluePrimary),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { attachmentFeedbackNotice = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                        }
                    }
                }
            }

            // Display Selected Attachment with Preview and Remove/Replace option
            if (currentAttachment != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("selected_attachment_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BlueSoftBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BlueSecondarySurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (currentAttachment.type == "IMAGE") Icons.Default.CameraAlt else Icons.Default.Description,
                                    contentDescription = null,
                                    tint = BluePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (currentAttachment.type == "IMAGE") "Lampiran Foto / Gambar" else "Lampiran Dokumen PDF / Berkas",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BluePrimary
                                    )
                                )
                            }

                            // Remove button
                            IconButton(
                                onClick = onRemoveAttachment,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("remove_attachment_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus Lampiran",
                                    tint = ErrorRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        val sizeKb = currentAttachment.sizeBytes / 1024
                        Text(
                            text = "${currentAttachment.displayName} (${sizeKb} KB)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )

                        // Image Preview if attachment is an image
                        if (currentAttachment.type == "IMAGE") {
                            Spacer(modifier = Modifier.height(10.dp))
                            val file = File(currentAttachment.localPath)
                            if (file.exists()) {
                                AsyncImage(
                                    model = file,
                                    contentDescription = "Preview Lampiran",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .testTag("image_attachment_preview"),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            // Document Badge
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceWhite,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = BluePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Dokumen siap disimpan bersama materi pelajaran",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                    )
                                }
                            }
                        }

                        // Extracted text notice if available
                        if (currentAttachment.extractedText != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Teks dokumen otomatis disalin ke kolom isi materi di bawah.",
                                style = MaterialTheme.typography.labelSmall.copy(color = SuccessGreen, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            // Title Field
            Text(
                text = "Judul Materi *",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Contoh: Rangkuman Hukum Newton & Contoh Soal") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("material_title_input"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = BorderSubtle
                )
            )

            // School Level & Grade Selector
            Text(
                text = "Jenjang & Kelas *",
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

            // Subject selector chips
            Text(
                text = "Mata Pelajaran *",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                subjects.forEach { subj ->
                    FilterChip(
                        selected = selectedSubject == subj,
                        onClick = { selectedSubject = subj },
                        label = { Text(subj, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlueSecondarySurface,
                            selectedLabelColor = BluePrimary
                        )
                    )
                }
            }

            // Topic Field
            Text(
                text = "Topik / Bab Pembahasan",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                placeholder = { Text("Contoh: Mekanika Fisika atau Aljabar") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("material_topic_input"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = BorderSubtle
                )
            )

            // Brief Description
            Text(
                text = "Deskripsi Singkat",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Penjelasan ringkas isi materi...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("material_description_input"),
                shape = RoundedCornerShape(10.dp),
                maxLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = BorderSubtle
                )
            )

            // Main Content Area
            Text(
                text = "Isi Konten Catatan Pembelajaran *",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Tulis atau edit teks materi pelajaran di sini. Gunakan # untuk judul bab dan ## untuk sub-bab.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .testTag("material_content_input"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = BorderSubtle
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Save Draft Button
                OutlinedButton(
                    onClick = {
                        onSaveMaterial(
                            title.ifBlank { "Draf Tanpa Judul" },
                            description,
                            content,
                            selectedLevel,
                            selectedGrade,
                            selectedSubject,
                            topic,
                            true, // isDraft
                            currentAttachment,
                            null
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_draft_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Simpan Draf")
                }

                // Publish Button
                Button(
                    onClick = {
                        onSaveMaterial(
                            title,
                            description,
                            content,
                            selectedLevel,
                            selectedGrade,
                            selectedSubject,
                            topic,
                            false, // isDraft = false (Publish)
                            currentAttachment,
                            null
                        )
                    },
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("publish_material_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("Terbitkan (+35 XP)")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mandatory Creator Footer
            NaikLevelFooter()
        }
    }
}

@Composable
private fun AttachmentActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag(testTag),
        color = SurfaceWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, BlueSecondarySurface)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = BluePrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = BluePrimary,
                    fontSize = 11.sp
                )
            )
        }
    }
}
