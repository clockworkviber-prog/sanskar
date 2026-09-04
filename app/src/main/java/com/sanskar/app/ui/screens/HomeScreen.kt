package com.sanskar.app.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanskar.app.AppViewModel
import com.sanskar.app.data.MockData
import com.sanskar.app.data.MuhuratQuality
import com.sanskar.app.data.PujaService
import com.sanskar.app.data.TimeUtil
import com.sanskar.app.ui.theme.Marigold
import com.sanskar.app.ui.theme.Maroon
import com.sanskar.app.ui.theme.Saffron
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onPujaClick: (String) -> Unit,
    onSeeAllMuhurat: () -> Unit,
    onBookNow: () -> Unit
) {
    val muhurats = remember { MockData.muhuratsForCurrentMonth() }
    val firstName = viewModel.currentUser?.fullName?.substringBefore(" ") ?: "Bhakt"

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            HeaderBanner(firstName)
        }

        item(span = { GridItemSpan(2) }) {
            MuhuratStrip(muhurats.take(3), viewModel.currentUser?.timezone, onSeeAllMuhurat)
        }

        item(span = { GridItemSpan(2) }) {
            Text(
                "Puja Services",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(MockData.pujaServices, key = { it.id }) { puja ->
            PujaTile(puja = puja, onClick = { onPujaClick(puja.id) }, onBookNow = onBookNow)
        }
    }
}

@Composable
private fun HeaderBanner(firstName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(listOf(Saffron, Maroon)),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Text(
                "🙏 Namaste, $firstName",
                color = androidx.compose.ui.graphics.Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Book pujas with verified priests — live online or performed on your behalf at partner mandirs in India.",
                color = androidx.compose.ui.graphics.Color(0xFFFFE8CC),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun MuhuratStrip(
    muhurats: List<com.sanskar.app.data.Muhurat>,
    userZone: String?,
    onSeeAll: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "✨ Shubh Muhurat this month",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onSeeAll) { Text("See all") }
            }
            muhurats.forEach { m ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        m.date.format(DateTimeFormatter.ofPattern("dd MMM")),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.width(64.dp)
                    )
                    Column(Modifier.weight(1f)) {
                        Text(m.occasion, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            "${TimeUtil.localWindow(m.date, m.startIst, m.endIst, userZone)} your time",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (m.quality == MuhuratQuality.EXCELLENT) {
                        Text("★", color = Marigold, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PujaTile(puja: PujaService, onClick: () -> Unit, onBookNow: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(puja.emoji, fontSize = 34.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                puja.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                puja.sanskritName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "From $${puja.priceOnlineUsd}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                when {
                    puja.availableOnline && puja.inTempleAvailable -> "Online • In-temple"
                    puja.availableOnline -> "Online only"
                    else -> "In-temple only"
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onBookNow,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Book Now", fontSize = 13.sp)
            }
        }
    }
}
