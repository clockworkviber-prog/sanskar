package com.sanskar.app.data

import java.time.LocalDate
import java.time.LocalTime

data class PujaService(
    val id: String,
    val name: String,
    val sanskritName: String,
    val emoji: String,
    val description: String,
    val benefits: String,
    val durationMinutes: Int,
    val priceOnlineUsd: Int,
    val priceInTempleUsd: Int,
    val availableOnline: Boolean = true,
    val inTempleAvailable: Boolean = true
)

data class Priest(
    val id: String,
    val name: String,
    val title: String,
    val templeAffiliation: String,
    val specializations: List<String>,
    val languages: List<String>,
    val experienceYears: Int,
    val rating: Double,
    val pujasPerformed: Int,
    val isOnline: Boolean
)

enum class MuhuratQuality { EXCELLENT, GOOD, AVERAGE }

data class Muhurat(
    val date: LocalDate,
    val startIst: LocalTime,
    val endIst: LocalTime,
    val occasion: String,
    val panchangNote: String,
    val quality: MuhuratQuality
)

enum class PujaMode { ONLINE_LIVE, IN_TEMPLE }

enum class BookingStatus { UPCOMING, COMPLETED, CANCELLED }

data class Booking(
    val id: String,
    val pujaName: String,
    val priestName: String,
    val mode: PujaMode,
    val date: LocalDate,
    val timeSlot: String,
    val priceUsd: Int,
    val status: BookingStatus,
    val sankalpName: String = ""
)

/** A major puja showcased by a priest on their profile, with an optional photo. */
data class PortfolioItem(
    val id: String,
    val priestId: String,
    val title: String,
    val description: String = "",
    val imageUri: String? = null
)

data class ChatMessage(
    val text: String,
    val fromUser: Boolean
)

data class UserProfile(
    val fullName: String,
    val email: String,
    val phone: String = "",
    val city: String = "",
    val country: String = "",
    val gotra: String = "",
    val timezone: String = ""
)
