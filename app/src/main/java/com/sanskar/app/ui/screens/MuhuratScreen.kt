package com.sanskar.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sanskar.app.AppViewModel
import com.sanskar.app.data.MockData
import com.sanskar.app.data.Muhurat
import com.sanskar.app.data.MuhuratQuality
import com.sanskar.app.data.TimeUtil
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MuhuratScreen(viewModel: AppViewModel) {
    val muhurats = remember { MockData.muhuratsForCurrentMonth() }
    val monthTitle = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    val userZone = viewModel.currentUser?.timezone
    val zoneCity = TimeUtil.zoneCity(userZone)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                "Shubh Muhurat — $monthTitle",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Auspicious dates and time windows for common ceremonies this month. " +
                        "Times are shown in your local time ($zoneCity), converted from the actual ritual timings in India (IST).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
        }

        items(muhurats) { m ->
            MuhuratCard(m, userZone, zoneCity)
        }

        item {
            Spacer(Modifier.height(4.dp))
            Text(
                "⚠️ Muhurats shown are indicative and based on a general panchang. " +
                        "Exact timings vary by your city and time zone — our priests will " +
                        "confirm the precise muhurat for your location at the time of booking.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MuhuratCard(m: Muhurat, userZone: String?, zoneCity: String) {
    val (badgeText, badgeColor) = when (m.quality) {
        MuhuratQuality.EXCELLENT -> "Excellent" to Color(0xFF2E7D32)
        MuhuratQuality.GOOD -> "Good" to Color(0xFFE8A100)
        MuhuratQuality.AVERAGE -> "Average" to Color(0xFF8D6E63)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .width(64.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    m.date.format(DateTimeFormatter.ofPattern("dd")),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    m.date.format(DateTimeFormatter.ofPattern("EEE")),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(m.occasion, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    "🕒 ${TimeUtil.localWindow(m.date, m.startIst, m.endIst, userZone)} ($zoneCity)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    TimeUtil.istWindow(m.startIst, m.endIst),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    m.panchangNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                badgeText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = badgeColor
            )
        }
    }
}
