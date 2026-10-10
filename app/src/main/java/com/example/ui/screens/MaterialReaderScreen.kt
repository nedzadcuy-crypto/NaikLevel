package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.MaterialEntity
import com.example.ui.components.NaikLevelFooter
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BlueSecondarySurface
import com.example.ui.theme.BlueSoftBg
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.io.File

@Composable
fun MaterialReaderScreen(
    material: MaterialEntity,
    onBack: () -> Unit,
    onProgressUpdate: (Float) -> Unit,
    onMarkCompleted: () -> Unit,
    onAskAiTutor: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Dynamically calculate read progress as user scrolls through the content
    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        if (scrollState.maxValue > 0) {
            val progress = (scrollState.value.toFloat() / scrollState.maxValue.toFloat()).coerceIn(0f, 1f)
            if (progress > material.readProgress) {
                onProgressUpdate(progress)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .testTag("material_reader_screen")
    ) {
        // Top App Bar
        Surface(
            color = SurfaceWhite,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("reader_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Belajar"
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = material.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = "${material.schoolLevel} • Kelas ${material.grade} • ${material.subject}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                    IconButton(
                        onClick = { onAskAiTutor("Jelaskan ringkasan materi: ${material.title}") },
                        modifier = Modifier.testTag("reader_ask_ai_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Tanya AI",
                            tint = BluePrimary
                        )
                    }
                }

                // Progress Bar at top of reader
                LinearProgressIndicator(
                    progress = { material.readProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = if (material.isCompleted) SuccessGreen else BluePrimary,
                    trackColor = BlueSecondarySurface
                )
            }
        }

        // Scrollable Reading Content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Badges & Topic Meta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BlueSecondarySurface
                ) {
                    Text(
                        text = "Topik: ${material.topic}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "${material.estimatedReadMinutes} menit bacaan",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = material.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    lineHeight = 32.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = material.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    lineHeight = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Attachment Display if present
            if (!material.attachmentPath.isNullOrBlank()) {
                val file = File(material.attachmentPath)
                if (file.exists()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reader_attachment_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = BlueSoftBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BlueSecondarySurface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (material.attachmentType == "IMAGE") Icons.Default.Image else Icons.Default.Description,
                                    contentDescription = null,
                                    tint = BluePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Lampiran: ${material.attachmentName ?: file.name}",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BluePrimary
                                    )
                                )
                            }

                            if (material.attachmentType == "IMAGE") {
                                Spacer(modifier = Modifier.height(10.dp))
                                AsyncImage(
                                    model = file,
                                    contentDescription = "Lampiran Gambar",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Berkas dokumen tersimpan pada penyimpanan aplikasi.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else if (!material.originalAttachmentNote.isNullOrBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BlueSoftBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BlueSecondarySurface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Lampiran Sumber: ${material.attachmentName ?: "Berkas Asli"}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary
                            )
                        )
                        Text(
                            text = material.originalAttachmentNote,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Main formatted text content
            val paragraphs = material.content.split("\n\n")
            paragraphs.forEach { para ->
                when {
                    para.startsWith("# ") -> {
                        Text(
                            text = para.removePrefix("# ").trim(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary
                            ),
                            modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
                        )
                    }
                    para.startsWith("## ") -> {
                        Text(
                            text = para.removePrefix("## ").trim(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            ),
                            modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
                        )
                    }
                    para.startsWith("### ") -> {
                        Text(
                            text = para.removePrefix("### ").trim(),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            ),
                            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                        )
                    }
                    else -> {
                        Text(
                            text = para.trim(),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = TextPrimary,
                                lineHeight = 24.sp
                            ),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Actions Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BlueSoftBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (material.isCompleted) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Materi ini telah kamu selesaikan!",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    } else {
                        Text(
                            text = "Sudah selesai membaca dan memahami?",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Dapatkan +40 XP dan selesaikan quest harianmu",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onMarkCompleted,
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("mark_material_finished_button")
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tandai Selesai (+40 XP)")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { onAskAiTutor("Beri saya 2 latihan soal tentang materi: ${material.title}") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ask_ai_practice_button")
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Latihan Soal dengan Tutor AI", color = BluePrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mandatory Creator Footer
            NaikLevelFooter()
        }
    }
}
