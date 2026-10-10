package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.RisingBookLogoBadge
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BlueSecondarySurface
import com.example.ui.theme.BlueSoftBg
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .testTag("about_screen")
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
                    modifier = Modifier.testTag("about_back_button")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali ke Profil")
                }
                Text(
                    text = "Tentang Aplikasi",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Rising Book Logo
            RisingBookLogoBadge(size = 96.dp)

            Spacer(modifier = Modifier.height(20.dp))

            // EXACT BRANDING AND ATTRIBUTION AS REQUIRED
            Text(
                text = "NaikLevel",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary,
                    fontSize = 32.sp
                ),
                modifier = Modifier.testTag("about_app_name")
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Belajar Modern",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                ),
                modifier = Modifier.testTag("about_tagline")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mandatory Creator Attribution Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = BlueSecondarySurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("about_attribution_card")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = BluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Created by Nedzad Elvedino Ariesza",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary,
                            fontSize = 15.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Mission Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Visi NaikLevel",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "NaikLevel adalah platform edukasi modern asli Indonesia yang menghubungkan kurikulum berstandar nasional (SD, SMP, SMA, SMK) dengan kekuatan kecerdasan buatan dan gamifikasi yang menyenangkan. Belajar mandiri kini lebih terarah, interaktif, dan penuh motivasi.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Technical Specs Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BlueSoftBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, BlueSecondarySurface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Spesifikasi Teknologi",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )
                    )
                    AboutSpecRow(label = "Versi Aplikasi", value = "1.0.0 (Build Stabil)")
                    AboutSpecRow(label = "Platform", value = "Native Android (Kotlin & Jetpack Compose)")
                    AboutSpecRow(label = "Penyimpanan", value = "Room Database (Offline-First)")
                    AboutSpecRow(label = "Sinkronisasi", value = "Lokal & Cloud Ready")
                    AboutSpecRow(label = "Arsitektur", value = "MVVM + Clean Architecture")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Hak Cipta © 2026 NaikLevel • Belajar Modern\nSeluruh Hak Dilindungi",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AboutSpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        )
    }
}
