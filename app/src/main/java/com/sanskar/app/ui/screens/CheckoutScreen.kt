package com.sanskar.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private enum class CheckoutStage { PROCESSING, SUCCESS }

/**
 * Dummy in-app checkout: simulates a payment completing after a short delay.
 * No external payment provider, network call, or link is involved — swap
 * the delay/success branch for a real payment SDK when one is chosen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onClose: () -> Unit,
    onPayLater: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    var stage by remember { mutableStateOf(CheckoutStage.PROCESSING) }

    BackHandler(enabled = stage == CheckoutStage.PROCESSING) { onClose() }

    LaunchedEffect(Unit) {
        delay(1400)
        stage = CheckoutStage.SUCCESS
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                actions = {
                    if (stage == CheckoutStage.PROCESSING) {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Filled.Close, contentDescription = "Close checkout")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when (stage) {
                CheckoutStage.PROCESSING -> ProcessingContent(onPayLater = onPayLater)
                CheckoutStage.SUCCESS -> PaymentSuccessContent(onDone = onPaymentSuccess)
            }
        }
    }
}

@Composable
private fun ProcessingContent(onPayLater: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
        Text("Processing your payment…", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(28.dp))
        OutlinedButton(onClick = onPayLater, modifier = Modifier.fillMaxWidth()) {
            Text("Pay Later — keep booking & go to my orders")
        }
    }
}

@Composable
private fun PaymentSuccessContent(onDone: () -> Unit) {
    val scale = remember { Animatable(0.4f) }
    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = tween(350))
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(Color(0xFF2E7D32), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(52.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            "Payment Completed",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Your booking is confirmed. A receipt has been saved to your order history.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(28.dp))
        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Done")
        }
    }
}
