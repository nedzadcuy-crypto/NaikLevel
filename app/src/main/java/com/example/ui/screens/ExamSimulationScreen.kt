package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.viewmodel.ExamSessionState

@Composable
fun ExamSimulationScreen(
    session: ExamSessionState,
    onSelectAnswer: (questionId: Int, optionIndex: Int) -> Unit,
    onNextQuestion: () -> Unit,
    onPrevQuestion: () -> Unit,
    onSubmitExam: () -> Unit,
    onCloseExam: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!session.isActive || session.questions.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(SurfaceWhite)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Tidak ada sesi ujian aktif.")
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onCloseExam) {
                    Text("Kembali")
                }
            }
        }
        return
    }

    if (session.isSubmitted) {
        // Results & Review Screen
        ExamResultView(
            session = session,
            onClose = onCloseExam,
            modifier = modifier
        )
        return
    }

    val currentQuestion = session.questions[session.currentQuestionIndex]
    val selectedOptionIndex = session.userAnswers[currentQuestion.id]
    val answeredCount = session.userAnswers.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .testTag("exam_simulation_screen")
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
                    onClick = onCloseExam,
                    modifier = Modifier.testTag("exam_back_button")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Keluar Ujian")
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.subject,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "${session.schoolLevel} • Soal ${session.currentQuestionIndex + 1} dari ${session.questions.size}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }

                // Answered counter badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BlueSecondarySurface
                ) {
                    Text(
                        text = "$answeredCount/${session.questions.size} Terjawab",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BluePrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Progress bar
        val progress = (session.currentQuestionIndex + 1).toFloat() / session.questions.size.toFloat()
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = BluePrimary,
            trackColor = BlueSecondarySurface
        )

        // Question Body
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Subject & Question number
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BlueSoftBg
                ) {
                    Text(
                        text = currentQuestion.subject,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "Poin Penuh: 20",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                )
            }

            // Question prompt
            Text(
                text = currentQuestion.question,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    lineHeight = 28.sp
                ),
                modifier = Modifier.testTag("exam_question_text")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Multiple choice options
            currentQuestion.options.forEachIndexed { index, optionText ->
                val isSelected = selectedOptionIndex == index
                val optionLetter = ('A' + index).toString()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectAnswer(currentQuestion.id, index) }
                        .testTag("option_button_${currentQuestion.id}_$index"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) BlueSecondarySurface else SurfaceWhite
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isSelected) BluePrimary else BorderSubtle
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) BluePrimary else BlueSoftBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = optionLetter,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) SurfaceWhite else BluePrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = TextPrimary,
                                lineHeight = 20.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Bottom Controls
        Surface(
            color = SurfaceWhite,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onPrevQuestion,
                    enabled = session.currentQuestionIndex > 0,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Sebelumnya")
                }

                if (session.currentQuestionIndex == session.questions.size - 1) {
                    Button(
                        onClick = onSubmitExam,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        modifier = Modifier.testTag("submit_exam_button")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Selesai & Kumpulkan")
                    }
                } else {
                    Button(
                        onClick = onNextQuestion,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        modifier = Modifier.testTag("next_exam_question_button")
                    ) {
                        Text("Selanjutnya")
                    }
                }
            }
        }
    }
}

@Composable
fun ExamResultView(
    session: ExamSessionState,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("exam_result_view"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Trophy / Result Circle
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(if (session.score >= 60) SuccessContainer else WarningContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = if (session.score >= 60) SuccessGreen else WarningOrange,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = if (session.score >= 80) "Luar Biasa! Kerja Hebat" else "Simulasi Ujian Selesai",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Hasil ujianmu telah disimpan secara permanen ke statistik belajarmu.",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Score Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BlueSoftBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, BlueSecondarySurface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Skor Akhir",
                    style = MaterialTheme.typography.titleSmall.copy(color = TextSecondary)
                )
                Text(
                    text = "${session.score}",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Benar", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                        Text(
                            text = "${session.correctCount}/${session.questions.size}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("XP Diperoleh", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                        Text(
                            text = "+${session.earnedXp} XP",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Explanations Section
        Text(
            text = "Pembahasan & Kunci Jawaban:",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        session.questions.forEachIndexed { idx, q ->
            val userAnswer = session.userAnswers[q.id]
            val isCorrect = userAnswer == q.correctAnswerIndex
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isCorrect) SuccessContainer else ErrorContainer
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (isCorrect) SuccessGreen else ErrorRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Soal ${idx + 1} (${q.subject})",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) SuccessGreen else ErrorRed
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = q.question,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val correctOptionText = q.options.getOrNull(q.correctAnswerIndex) ?: ""
                    Text(
                        text = "Jawaban Benar: $correctOptionText",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SuccessGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Penjelasan: ${q.explanation}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("finish_exam_review_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
        ) {
            Text("Kembali ke Perpustakaan Belajar")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
