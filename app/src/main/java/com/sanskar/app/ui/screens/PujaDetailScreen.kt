package com.sanskar.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanskar.app.AppViewModel
import com.sanskar.app.data.MockData
import com.sanskar.app.data.PujaMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val timeSlots = listOf("06:00 AM IST", "08:00 AM IST", "10:30 AM IST", "05:00 PM IST", "07:30 PM IST")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PujaDetailScreen(
    pujaId: String,
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onBooked: () -> Unit,
    onOpenGuide: (String) -> Unit
) {
    val puja = MockData.pujaServices.firstOrNull { it.id == pujaId } ?: run {
        onBack(); return
    }

    var mode by remember {
        mutableStateOf(if (puja.availableOnline) PujaMode.ONLINE_LIVE else PujaMode.IN_TEMPLE)
    }
    var selectedSlot by remember { mutableStateOf(timeSlots[1]) }
    var selectedPriestId by remember {
        mutableStateOf(viewModel.priests.first { it.isOnline }.id)
    }
    var daysFromNow by remember { mutableStateOf(3) }
    var sankalpName by remember { mutableStateOf(viewModel.currentUser?.fullName ?: "") }

    val date = LocalDate.now().plusDays(daysFromNow.toLong())
    val price = if (mode == PujaMode.ONLINE_LIVE) puja.priceOnlineUsd else puja.priceInTempleUsd
    val priests = if (mode == PujaMode.ONLINE_LIVE) viewModel.priests.filter { it.isOnline } else viewModel.priests

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(puja.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("${puja.emoji}  ${puja.sanskritName}", fontSize = 22.sp)
            Spacer(Modifier.height(8.dp))
            Text(puja.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(6.dp))
            Text(
                "Benefits: ${puja.benefits}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Duration: ~${puja.durationMinutes / 60}h ${puja.durationMinutes % 60}m",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { onOpenGuide(puja.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Ritual preparation guide & checklist")
            }

            Spacer(Modifier.height(16.dp))
            Text("How would you like the puja?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (puja.availableOnline) {
                    FilterChip(
                        selected = mode == PujaMode.ONLINE_LIVE,
                        onClick = { mode = PujaMode.ONLINE_LIVE },
                        label = { Text("Online live — $${puja.priceOnlineUsd}") }
                    )
                }
                if (puja.inTempleAvailable) {
                    FilterChip(
                        selected = mode == PujaMode.IN_TEMPLE,
                        onClick = { mode = PujaMode.IN_TEMPLE },
                        label = { Text("At mandir for you — $${puja.priceInTempleUsd}") }
                    )
                }
            }
            Text(
                when {
                    puja.id == "consult" ->
                        "A one-on-one video session with the acharya at your chosen time — fixed fee, no samagri needed."
                    mode == PujaMode.ONLINE_LIVE ->
                        "Join live over video call — the priest guides you through the ritual step by step."
                    else ->
                        "The puja is performed on your behalf at a partner mandir in India, with sankalp in your name. Video recording and prasad shipping included."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))
            Text("Choose date", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1, 3, 7, 14).forEach { d ->
                    FilterChip(
                        selected = daysFromNow == d,
                        onClick = { daysFromNow = d },
                        label = {
                            Text(LocalDate.now().plusDays(d.toLong()).format(DateTimeFormatter.ofPattern("dd MMM")))
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Choose time slot", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                timeSlots.chunked(3).forEach { rowSlots ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowSlots.forEach { slot ->
                            FilterChip(
                                selected = selectedSlot == slot,
                                onClick = { selectedSlot = slot },
                                label = { Text(slot.removeSuffix(" IST")) }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Choose priest", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            priests.forEach { priest ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPriestId == priest.id)
                            MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    ),
                    onClick = { selectedPriestId = priest.id }
                ) {
                    Row(Modifier.padding(10.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text(priest.name, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${priest.languages.joinToString(", ")} • ★ ${priest.rating}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (priest.isOnline) {
                            Text(
                                "Online",
                                style = MaterialTheme.typography.labelMedium,
                                color = androidx.compose.ui.graphics.Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = sankalpName,
                onValueChange = { sankalpName = it },
                label = { Text("Name for sankalp") },
                supportingText = { Text("The name (and gotra, if known) in which the sankalp will be taken") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val priest = priests.first { it.id == selectedPriestId }
                    viewModel.bookPuja(
                        pujaName = puja.name,
                        priestName = priest.name,
                        mode = mode,
                        date = date,
                        timeSlot = selectedSlot,
                        priceUsd = price,
                        sankalpName = sankalpName
                    )
                    onBooked()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    "Pay ${'$'}$price & Book • ${date.format(DateTimeFormatter.ofPattern("dd MMM"))}, $selectedSlot",
                    fontSize = 16.sp
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
