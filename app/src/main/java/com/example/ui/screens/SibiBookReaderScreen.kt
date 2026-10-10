package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun SibiBookReaderScreen(
    book: SibiBook,
    currentChapterIndex: Int,
    onSelectChapter: (Int) -> Unit,
    onAskAiAboutBook: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val chapters = book.chapters
    val activeChapter = chapters.getOrNull(currentChapterIndex) ?: chapters.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .testTag("sibi_book_reader_screen")
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
                    modifier = Modifier.testTag("sibi_reader_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali ke Katalog"
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SuccessContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Buku Resmi SIBI",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SuccessGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        maxLines = 1
                    )
                }

                IconButton(
                    onClick = {
                        val chapterTitle = activeChapter?.title ?: book.title
                        onAskAiAboutBook("Jelaskan materi dari buku resmi '${book.title}', bagian: $chapterTitle")
                    },
                    modifier = Modifier.testTag("sibi_ask_ai_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Tanya AI tentang buku ini",
                        tint = BluePrimary
                    )
                }
            }
        }

        // Main Scrollable Reader Content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Book Info Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(book.coverColorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = SurfaceWhite,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${book.schoolLevel} • Kelas ${book.grade} • ${book.subject}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BluePrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = book.publisher,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Penulis: ${book.authors}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            // Chapter Navigation Chips
            if (chapters.isNotEmpty()) {
                Text(
                    text = "Daftar Bab Tersedia:",
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
                    chapters.forEachIndexed { index, ch ->
                        FilterChip(
                            selected = currentChapterIndex == index,
                            onClick = { onSelectChapter(index) },
                            label = {
                                Text(
                                    text = "Bab ${ch.chapterNumber}: ${ch.title}",
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BlueSecondarySurface,
                                selectedLabelColor = BluePrimary
                            )
                        )
                    }
                }
            }

            // Chapter Body Content
            if (activeChapter != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Bab ${activeChapter.chapterNumber}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = BluePrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = activeChapter.pageRange,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = activeChapter.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Chapter summary box
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BlueSoftBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Intisari Bab Resmi:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BluePrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = activeChapter.summary,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        lineHeight = 18.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Formatted markdown content of chapter
                        val paragraphs = activeChapter.content.split("\n\n")
                        paragraphs.forEach { p ->
                            when {
                                p.startsWith("# ") -> {
                                    Text(
                                        text = p.removePrefix("# ").trim(),
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = BluePrimary
                                        ),
                                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                                    )
                                }
                                p.startsWith("## ") -> {
                                    Text(
                                        text = p.removePrefix("## ").trim(),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        ),
                                        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                                    )
                                }
                                else -> {
                                    Text(
                                        text = p.trim(),
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = TextPrimary,
                                            lineHeight = 24.sp
                                        ),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Fallback & External Official Link Action Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BlueSoftBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, BlueSecondarySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Akses Lengkap SIBI Kemendikdasmen",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Kamu sedang membaca ringkasan bab. Untuk membaca edisi buku lengkap atau mengunduh PDF asli, kamu dapat mengunjungi portal resmi SIBI Kemendikdasmen.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(com.example.data.sibi.SibiLinks.safeSourceUrlOrCatalog(book.officialUrl)))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(context, "Tidak ada aplikasi untuk membuka tautan ini.", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_sibi_official_portal_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Buka di Portal SIBI Kemendikdasmen")
                    }
                }
            }

            // Mandatory Creator Footer
            NaikLevelFooter()
        }
    }
}
