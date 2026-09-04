package com.sanskar.app.data

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object TimeUtil {

    val IST: ZoneId = ZoneId.of("Asia/Kolkata")

    private val timeFmt = DateTimeFormatter.ofPattern("hh:mm a")
    private val dayFmt = DateTimeFormatter.ofPattern("dd MMM")

    /** Timezones offered in the profile dropdown. */
    val commonTimezones = listOf(
        "Asia/Kolkata",
        "America/New_York",
        "America/Chicago",
        "America/Denver",
        "America/Los_Angeles",
        "America/Toronto",
        "Europe/London",
        "Europe/Berlin",
        "Europe/Paris",
        "Asia/Dubai",
        "Asia/Singapore",
        "Asia/Hong_Kong",
        "Asia/Tokyo",
        "Australia/Sydney",
        "Australia/Melbourne",
        "Pacific/Auckland"
    )

    fun zoneOrDefault(zoneId: String?): ZoneId =
        try {
            if (zoneId.isNullOrBlank()) ZoneId.systemDefault() else ZoneId.of(zoneId)
        } catch (e: Exception) {
            ZoneId.systemDefault()
        }

    /** e.g. "America/New_York (GMT-4)" for dropdown labels. */
    fun zoneLabel(zoneId: String): String {
        val offset = ZonedDateTime.now(ZoneId.of(zoneId)).offset.id.let {
            if (it == "Z") "+00:00" else it
        }
        return "$zoneId (GMT$offset)"
    }

    fun istWindow(start: LocalTime, end: LocalTime): String =
        "${start.format(timeFmt)} – ${end.format(timeFmt)} IST"

    /**
     * Converts an IST muhurat window on [date] to the user's timezone.
     * Includes the local date when it differs from the IST date
     * (e.g. an early-morning IST muhurat falls on the previous evening in the US).
     */
    fun localWindow(date: LocalDate, start: LocalTime, end: LocalTime, zoneId: String?): String {
        val zone = zoneOrDefault(zoneId)
        if (zone == IST) return istWindow(start, end)
        val s = date.atTime(start).atZone(IST).withZoneSameInstant(zone)
        val e = date.atTime(end).atZone(IST).withZoneSameInstant(zone)
        val sameDay = s.toLocalDate() == date && e.toLocalDate() == date
        val dayPart = when {
            sameDay -> ""
            s.toLocalDate() == e.toLocalDate() -> ", ${s.format(dayFmt)}"
            else -> " ${s.format(dayFmt)} – ${e.format(dayFmt)}"
        }
        return "${s.format(timeFmt)} – ${e.format(timeFmt)}$dayPart"
    }

    /** Short city-style name of the zone, e.g. "New York". */
    fun zoneCity(zoneId: String?): String =
        zoneOrDefault(zoneId).id.substringAfterLast('/').replace('_', ' ')
}
