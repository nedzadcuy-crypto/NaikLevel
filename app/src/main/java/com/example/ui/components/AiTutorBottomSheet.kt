package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.data.sibi.SibiLinks
import com.example.ui.viewmodel.AiTutorMessage
import com.example.ui.viewmodel.AnswerBasis

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AiTutorBottomSheet(
    messages: List<AiTutorMessage>,
    isThinking: Boolean,
    contextSource: String,
    onSendMessage: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val suggestedPrompts = listOf(
        "Jelaskan Teorema Pythagoras dari SIBI",
        "Fotosintesis & bagian tumbuhan (IPAS)",
        "Apa ciri utama teks LHO?",
        "Jelaskan GLBB & gerak parabola",
        "4 Pilar berpikir komputasional SMK"
    )

    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        modifier = Modifier.testTag("ai_tutor_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(horizontal = 16.dp)
                .imePadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BlueSecondarySurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "AI Tutor",
                            tint = BluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Tutor AI NaikLevel",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Jawaban AI; diberi label sumber hanya jika memakai materimu",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                        )
                    }
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_ai_tutor_button")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            // Grounding Context Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = BlueSoftBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, BlueSecondarySurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Fokus Sumber: $contextSource",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = BluePrimary,
                            fontSize = 11.sp
                        ),
                        maxLines = 1
                    )
                }
            }

            // Quick Prompt Suggestions
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                suggestedPrompts.forEach { prompt ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onSendMessage(prompt) },
                        color = SurfaceWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(message = msg)
                }

                if (isThinking) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = BluePrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tutor AI sedang mencari sumber SIBI & menyusun jawaban...",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = {
                        Text(
                            "Tanyakan konsep atau minta latihan soal...",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 13.sp)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_tutor_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceWhite
                    ),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            val toSend = textInput
                            textInput = ""
                            onSendMessage(toSend)
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (textInput.isNotBlank()) BluePrimary else BlueSecondarySurface)
                        .testTag("send_ai_tutor_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Kirim Pertanyaan",
                        tint = if (textInput.isNotBlank()) SurfaceWhite else BluePrimary
                    )
                }
            }

            // Subtle Footer Attribution
            NaikLevelFooter(modifier = Modifier.padding(bottom = 6.dp))
        }
    }
}

@Composable
private fun ChatBubble(message: AiTutorMessage) {
    val context = LocalContext.current
    val isUser = message.sender == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(if (isUser) 0.82f else 0.92f)
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .background(if (isUser) BluePrimary else BlueSoftBg)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isUser) SurfaceWhite else TextPrimary,
                            lineHeight = 20.sp
                        )
                    )

                    if (!isUser && message.basis != AnswerBasis.ERROR && message.basis != AnswerBasis.INFO) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Salin jawaban",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BluePrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.clickable {
                                val cm = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE)
                                    as android.content.ClipboardManager
                                cm.setPrimaryClip(android.content.ClipData.newPlainText("Jawaban Tutor AI", message.text))
                                android.widget.Toast.makeText(context, "Jawaban disalin", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    // Sumber / status dukungan sumber
                    if (!isUser && message.sourceTitle != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (message.isSibiVerified) SuccessContainer else SurfaceWhite,
                            border = androidx.compose.foundation.BorderStroke(
                                0.5.dp,
                                if (message.isSibiVerified) SuccessGreen.copy(alpha = 0.5f) else BorderSubtle
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = if (message.isSibiVerified) SuccessGreen else BluePrimary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = when (message.basis) {
                                                AnswerBasis.SIBI_DOCUMENT -> "Berdasarkan dokumen SIBI"
                                                AnswerBasis.USER_MATERIAL -> "Berdasarkan materi kamu"
                                                AnswerBasis.GENERAL_UNVERIFIED -> "Belum diverifikasi SIBI"
                                                else -> "Informasi"
                                            },
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (message.isSibiVerified) SuccessGreen else BluePrimary,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }

                                    if (message.sourceUrl != null) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable {
                                                try {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(SibiLinks.safeSourceUrlOrCatalog(message.sourceUrl)))
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    // Ignore
                                                }
                                            }
                                        ) {
                                            Text(
                                                text = if (message.basis == AnswerBasis.SIBI_DOCUMENT) "Buka sumber SIBI" else "Cari buku di SIBI",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = BluePrimary,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                                contentDescription = null,
                                                tint = BluePrimary,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = message.sourceTitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
