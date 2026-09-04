package com.sanskar.app.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.runtime.LaunchedEffect
import com.sanskar.app.AppViewModel
import com.sanskar.app.data.Booking
import com.sanskar.app.data.BookingStatus
import com.sanskar.app.data.PortfolioItem
import com.sanskar.app.data.PujaMode
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriestHomeScreen(
    viewModel: AppViewModel,
    onLogout: () -> Unit
) {
    val priest = viewModel.currentPriest ?: run { onLogout(); return }
    var upcoming by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var completed by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var myPortfolio by remember { mutableStateOf<List<PortfolioItem>>(emptyList()) }
    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(priest.id, refreshKey) {
        viewModel.priestBookings(BookingStatus.UPCOMING) { upcoming = it }
        viewModel.priestBookings(BookingStatus.COMPLETED) { completed = it }
        viewModel.portfolioFor(priest.id) { myPortfolio = it }
    }

    var newTitle by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }
    var pickedImage by remember { mutableStateOf<String?>(null) }
    var addError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // OpenDocument (not GetContent) lets us request a persistable read grant,
    // so the photo URI saved in the database still resolves after the app
    // process restarts — GetContent's grant is a one-shot, in-memory only.
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                // Some providers don't support persistable grants; the photo
                // still works for this session even if it won't survive a restart.
            }
            pickedImage = uri.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Priest Dashboard") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🧘", fontSize = 26.sp)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                "Pranam, ${priest.name}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${priest.title} • ${priest.templeAffiliation}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatBox("Upcoming", upcoming.size.toString(), Modifier.weight(1f))
                    StatBox("Completed", completed.size.toString(), Modifier.weight(1f))
                    StatBox("★ Rating", priest.rating.toString(), Modifier.weight(1f))
                }
            }

            item {
                Text(
                    "📅 Upcoming bookings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            if (upcoming.isEmpty()) {
                item {
                    Text(
                        "No upcoming bookings yet — devotee bookings assigned to you will appear here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(upcoming.size) { i -> PriestBookingCard(upcoming[i]) }
            }

            item {
                Spacer(Modifier.height(4.dp))
                Text(
                    "✅ Completed pujas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            if (completed.isEmpty()) {
                item {
                    Text(
                        "No completed pujas recorded yet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(completed.size) { i -> PriestBookingCard(completed[i]) }
            }

            // ---- Portfolio ----
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "🖼 My major pujas (shown to devotees)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Add photos of significant pujas you have performed — devotees see these on your profile.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it; addError = null },
                            label = { Text("Puja title (e.g. Maha Rudrabhishek, Kashi)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newDescription,
                            onValueChange = { newDescription = it },
                            label = { Text("Short description (optional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(onClick = { imagePicker.launch(arrayOf("image/*")) }) {
                                Icon(Icons.Filled.AddAPhoto, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text(if (pickedImage == null) "Add photo" else "Photo selected ✓")
                            }
                            Spacer(Modifier.width(10.dp))
                            pickedImage?.let {
                                AsyncImage(
                                    model = it,
                                    contentDescription = "Selected puja photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            }
                        }
                        addError?.let {
                            Spacer(Modifier.height(6.dp))
                            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = {
                                viewModel.addPortfolioItem(newTitle, newDescription, pickedImage) { err ->
                                    addError = err
                                    if (err == null) {
                                        newTitle = ""; newDescription = ""; pickedImage = null
                                        refreshKey++
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Add to my profile")
                        }
                    }
                }
            }

            items(myPortfolio.size) { i ->
                val item = myPortfolio[i]
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (item.imageUri != null) {
                            AsyncImage(
                                model = item.imageUri,
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🛕", fontSize = 26.sp)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
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

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun PriestBookingCard(booking: Booking) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(booking.pujaName, fontWeight = FontWeight.SemiBold)
                Text(
                    "$${booking.priceUsd}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                "${booking.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))} • ${booking.timeSlot}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                (if (booking.mode == PujaMode.ONLINE_LIVE) "🖥 Online live" else "🛕 At mandir") +
                        (if (booking.sankalpName.isNotBlank()) " • Sankalp: ${booking.sankalpName}" else "") +
                        " • ${booking.id}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
