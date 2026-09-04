package com.sanskar.app.ui.screens

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.sanskar.app.AppViewModel
import com.sanskar.app.data.Booking
import com.sanskar.app.data.BookingStatus
import com.sanskar.app.data.PujaMode
import com.sanskar.app.data.UserProfile
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(
    bookingId: String,
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val booking = viewModel.bookings.firstOrNull { it.id == bookingId } ?: run {
        onBack(); return
    }
    val html = remember(bookingId) { invoiceHtml(booking, viewModel.currentUser) }
    var webView by remember { mutableStateOf<WebView?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invoice ${booking.id}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { webView?.let { downloadInvoicePdf(it, booking.id) } }) {
                        Icon(Icons.Filled.Download, contentDescription = "Download invoice as PDF")
                    }
                }
            )
        }
    ) { padding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
                    webView = this
                }
            },
            onRelease = { it.destroy() }
        )
    }
}

/** Opens the system print dialog, which includes "Save as PDF". */
private fun downloadInvoicePdf(webView: WebView, bookingId: String) {
    val jobName = "Sanskar-Invoice-$bookingId"
    val printManager = webView.context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    printManager.print(
        jobName,
        webView.createPrintDocumentAdapter(jobName),
        PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build()
    )
}

private fun invoiceHtml(booking: Booking, user: UserProfile?): String {
    val dateFmt = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val issued = LocalDate.now().format(dateFmt)
    val pujaDate = booking.date.format(dateFmt)
    val mode = if (booking.mode == PujaMode.ONLINE_LIVE) "Online live puja (video call)"
    else "Performed at partner mandir"
    val (payStatus, payColor) = when (booking.status) {
        BookingStatus.COMPLETED -> "PAID" to "#2E7D32"
        BookingStatus.UPCOMING -> "PAYMENT PENDING (PAY LATER)" to "#B26A00"
        BookingStatus.CANCELLED -> "CANCELLED" to "#C62828"
    }
    val sankalp = booking.sankalpName.ifBlank { user?.fullName ?: "-" }

    return """
    <!DOCTYPE html><html><head><meta charset="utf-8">
    <style>
      body { font-family: sans-serif; color: #3A2A20; margin: 24px; }
      .head { display: flex; justify-content: space-between; align-items: center;
              border-bottom: 3px solid #B93E0A; padding-bottom: 14px; }
      .brand { font-size: 26px; font-weight: 800; color: #B93E0A; }
      .tag { font-size: 12px; color: #6E5A4B; }
      h2 { font-size: 16px; margin: 22px 0 6px; color: #7B1E3B; }
      table { width: 100%; border-collapse: collapse; margin-top: 8px; }
      td, th { padding: 9px 10px; border: 1px solid #E5D2BE; font-size: 13px; text-align: left; }
      th { background: #FFEFE0; }
      .total td { font-weight: 800; font-size: 15px; background: #FFF8F1; }
      .status { display: inline-block; margin-top: 10px; padding: 5px 12px; border-radius: 6px;
                font-weight: 800; font-size: 12px; color: #fff; background: $payColor; }
      .foot { margin-top: 30px; font-size: 11px; color: #6E5A4B;
              border-top: 1px solid #E5D2BE; padding-top: 10px; }
    </style></head><body>
      <div class="head">
        <div>
          <div class="brand">🪔 Sanskar</div>
          <div class="tag">Your Mandir, Wherever You Are &bull; support@sanskar.app</div>
        </div>
        <div style="text-align:right">
          <b>INVOICE</b><br>${booking.id}<br>
          <span class="tag">Issued: $issued</span>
        </div>
      </div>

      <h2>Billed To</h2>
      <div style="font-size:13px; line-height:1.6">
        ${user?.fullName ?: "-"}<br>
        ${user?.email ?: "-"}${if (!user?.phone.isNullOrBlank()) "<br>${user?.phone}" else ""}
        ${if (!user?.city.isNullOrBlank() || !user?.country.isNullOrBlank())
        "<br>" + listOfNotNull(user?.city, user?.country).filter { it.isNotBlank() }.joinToString(", ")
    else ""}
      </div>

      <h2>Service Details</h2>
      <table>
        <tr><th>Description</th><th>Details</th></tr>
        <tr><td>Puja</td><td>${booking.pujaName}</td></tr>
        <tr><td>Mode</td><td>$mode</td></tr>
        <tr><td>Priest</td><td>${booking.priestName}</td></tr>
        <tr><td>Scheduled Date</td><td>$pujaDate &bull; ${booking.timeSlot}</td></tr>
        <tr><td>Sankalp Name</td><td>$sankalp</td></tr>
        <tr class="total"><td>Total Amount</td><td>USD $${booking.priceUsd}</td></tr>
      </table>

      <span class="status">$payStatus</span>

      <div class="foot">
        This is a system-generated invoice from the Sanskar app for devotional services.
        Prasad shipping (for in-temple pujas) is included in the amount above.
        For questions or refunds, contact support@sanskar.app quoting the invoice number.
        <br>Om Shanti 🙏
      </div>
    </body></html>
    """.trimIndent()
}
