package com.sanskar.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.unit.dp
import com.sanskar.app.AppViewModel
import com.sanskar.app.data.TimeUtil
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: AppViewModel,
    onDone: () -> Unit
) {
    val user = viewModel.currentUser ?: run { onDone(); return }

    var fullName by remember { mutableStateOf(user.fullName) }
    var phone by remember { mutableStateOf(user.phone) }
    var city by remember { mutableStateOf(user.city) }
    var country by remember { mutableStateOf(user.country) }
    var gotra by remember { mutableStateOf(user.gotra) }
    var timezone by remember {
        mutableStateOf(user.timezone.ifBlank { ZoneId.systemDefault().id })
    }
    var tzExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Profile") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
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
                .padding(20.dp)
        ) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = user.email,
                onValueChange = { },
                label = { Text("Email (cannot be changed)") },
                enabled = false,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("Country") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            ExposedDropdownMenuBox(
                expanded = tzExpanded,
                onExpandedChange = { tzExpanded = it }
            ) {
                OutlinedTextField(
                    value = TimeUtil.zoneLabel(timezone),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Timezone") },
                    supportingText = { Text("Muhurat timings are shown converted to this timezone") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tzExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = tzExpanded,
                    onDismissRequest = { tzExpanded = false }
                ) {
                    val zones = (listOf(ZoneId.systemDefault().id) + TimeUtil.commonTimezones).distinct()
                    zones.forEach { zone ->
                        DropdownMenuItem(
                            text = { Text(TimeUtil.zoneLabel(zone)) },
                            onClick = {
                                timezone = zone
                                tzExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = gotra,
                onValueChange = { gotra = it },
                label = { Text("Gotra (optional)") },
                supportingText = { Text("Used during sankalp in your pujas") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.updateProfile(
                        user.copy(
                            fullName = fullName.trim(),
                            phone = phone.trim(),
                            city = city.trim(),
                            country = country.trim(),
                            gotra = gotra.trim(),
                            timezone = timezone
                        )
                    )
                    onDone()
                },
                enabled = fullName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Save Changes")
            }
        }
    }
}
