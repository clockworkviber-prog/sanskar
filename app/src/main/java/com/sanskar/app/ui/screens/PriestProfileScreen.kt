package com.sanskar.app.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sanskar.app.AppViewModel
import com.sanskar.app.data.PortfolioItem
import com.sanskar.app.ui.theme.Marigold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriestProfileScreen(
    priestId: String,
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val priest = viewModel.priests.firstOrNull { it.id == priestId } ?: run { onBack(); return }
    var portfolio by remember { mutableStateOf<List<PortfolioItem>>(emptyList()) }
    LaunchedEffect(priestId) { viewModel.portfolioFor(priestId) { portfolio = it } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(priest.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🧘", fontSize = 30.sp)
                            }
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        priest.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(
                                                if (priest.isOnline) Color(0xFF2E7D32) else Color(0xFFBDBDBD),
                                                CircleShape
                                            )
                                    )
                                }
                                Text(
                                    "${priest.title} • ${priest.experienceYears} yrs experience",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    priest.templeAffiliation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Text("★ ${priest.rating}  •  ${priest.pujasPerformed}+ pujas performed", color = Marigold, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text("Specializes in: ${priest.specializations.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                        Text("Languages: ${priest.languages.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item {
                Text(
                    "🖼 Major pujas performed",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Photos and highlights uploaded by the priest.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (portfolio.isEmpty()) {
                item {
                    Text(
                        "This priest hasn't added portfolio highlights yet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(portfolio.size) { i ->
                    val item = portfolio[i]
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            if (item.imageUri != null) {
                                AsyncImage(
                                    model = item.imageUri,
                                    contentDescription = item.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(170.dp)
                                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(90.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🛕", fontSize = 36.sp)
                                }
                            }
                            Column(Modifier.padding(12.dp)) {
                                Text(item.title, fontWeight = FontWeight.SemiBold)
                                if (item.description.isNotBlank()) {
                                    Text(
                                        item.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }
}
