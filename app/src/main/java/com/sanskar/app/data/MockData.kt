package com.sanskar.app.data

import java.time.LocalDate
import java.time.LocalTime

/**
 * Sample catalog data. In production this would come from a backend API.
 */
object MockData {

    val pujaServices = listOf(
        PujaService(
            id = "griha_pravesh",
            name = "Griha Pravesh",
            sanskritName = "गृह प्रवेश",
            emoji = "🏠",
            description = "House-warming ceremony performed before moving into a new home, invoking Vastu Purush and Lord Ganesha to bless the household.",
            benefits = "Removes Vastu dosha, invites peace and prosperity into the new home.",
            durationMinutes = 120,
            priceOnlineUsd = 79,
            priceInTempleUsd = 149
        ),
        PujaService(
            id = "satyanarayan",
            name = "Satyanarayan Katha",
            sanskritName = "सत्यनारायण कथा",
            emoji = "🙏",
            description = "Puja and katha dedicated to Lord Vishnu in his Satyanarayan form, traditionally performed on Purnima or auspicious family occasions.",
            benefits = "Fulfilment of wishes, family harmony, gratitude for milestones.",
            durationMinutes = 150,
            priceOnlineUsd = 69,
            priceInTempleUsd = 129
        ),
        PujaService(
            id = "ganesh",
            name = "Ganesh Puja",
            sanskritName = "गणेश पूजा",
            emoji = "🐘",
            description = "Invocation of Lord Ganesha, the remover of obstacles, performed before new beginnings — a new job, business, or venture.",
            benefits = "Removes obstacles, blesses new beginnings with success.",
            durationMinutes = 60,
            priceOnlineUsd = 39,
            priceInTempleUsd = 79
        ),
        PujaService(
            id = "lakshmi",
            name = "Lakshmi Puja",
            sanskritName = "लक्ष्मी पूजा",
            emoji = "🪔",
            description = "Worship of Goddess Lakshmi for wealth and abundance, especially powerful on Fridays, Purnima, and Diwali.",
            benefits = "Attracts wealth, abundance and financial stability.",
            durationMinutes = 90,
            priceOnlineUsd = 49,
            priceInTempleUsd = 99
        ),
        PujaService(
            id = "rudrabhishek",
            name = "Rudrabhishek",
            sanskritName = "रुद्राभिषेक",
            emoji = "🔱",
            description = "Sacred abhishek of Lord Shiva with panchamrit while chanting Rudra mantras from the Yajurveda.",
            benefits = "Health, protection from negativity, spiritual upliftment.",
            durationMinutes = 120,
            priceOnlineUsd = 89,
            priceInTempleUsd = 169
        ),
        PujaService(
            id = "navagraha",
            name = "Navagraha Shanti",
            sanskritName = "नवग्रह शांति",
            emoji = "🪐",
            description = "Pacification of the nine planetary deities to reduce malefic effects shown in one's kundali.",
            benefits = "Reduces planetary doshas, brings balance during difficult dashas.",
            durationMinutes = 150,
            priceOnlineUsd = 99,
            priceInTempleUsd = 189
        ),
        PujaService(
            id = "mundan",
            name = "Mundan Sanskar",
            sanskritName = "मुंडन संस्कार",
            emoji = "👶",
            description = "First hair-shaving ceremony of a child, one of the sixteen sanskars, performed for the child's health and purification.",
            benefits = "Child's wellbeing, purification, long healthy life.",
            durationMinutes = 90,
            priceOnlineUsd = 59,
            priceInTempleUsd = 119
        ),
        PujaService(
            id = "annaprashan",
            name = "Annaprashan",
            sanskritName = "अन्नप्राशन",
            emoji = "🍚",
            description = "First rice-feeding ceremony of an infant, marking the beginning of solid food with divine blessings.",
            benefits = "Blesses the child with health, nourishment and strength.",
            durationMinutes = 60,
            priceOnlineUsd = 49,
            priceInTempleUsd = 99
        ),
        PujaService(
            id = "pitru",
            name = "Pitru Paksha Shraddh",
            sanskritName = "पितृ पक्ष श्राद्ध",
            emoji = "🪷",
            description = "Tarpan and shraddh rituals honouring ancestors, performed on their tithi or during Pitru Paksha.",
            benefits = "Peace for departed souls, removal of pitru dosha.",
            durationMinutes = 120,
            priceOnlineUsd = 79,
            priceInTempleUsd = 139
        ),
        PujaService(
            id = "vivah",
            name = "Vivah Sanskar",
            sanskritName = "विवाह संस्कार",
            emoji = "💞",
            description = "Complete Vedic wedding ceremony with all rituals — kanyadaan, panigrahan, saptapadi — conducted by an experienced acharya.",
            benefits = "A sacred, complete Vedic wedding wherever you are.",
            durationMinutes = 240,
            priceOnlineUsd = 299,
            priceInTempleUsd = 499,
            availableOnline = false
        ),
        PujaService(
            id = "sundarkand",
            name = "Sundarkand Path",
            sanskritName = "सुंदरकांड पाठ",
            emoji = "🏹",
            description = "Recitation of the Sundarkand from Ramcharitmanas, glorifying Hanuman ji's devotion and strength.",
            benefits = "Courage, removal of fear, resolution of stuck matters.",
            durationMinutes = 180,
            priceOnlineUsd = 59,
            priceInTempleUsd = 109
        ),
        PujaService(
            id = "consult",
            name = "Online Consultation",
            sanskritName = "ऑनलाइन परामर्श",
            emoji = "🗣️",
            description = "One-on-one video consultation with an experienced acharya — dedicated advice and guidance on rituals, muhurat selection, kundali questions, sanskars and spiritual practice. Fixed fee, 30 minutes.",
            benefits = "Personalised guidance and clarity for your specific questions.",
            durationMinutes = 30,
            priceOnlineUsd = 20,
            priceInTempleUsd = 20,
            availableOnline = true,
            inTempleAvailable = false
        ),
        PujaService(
            id = "vahan",
            name = "Vahan Puja",
            sanskritName = "वाहन पूजा",
            emoji = "🚗",
            description = "Blessing ceremony for a newly purchased vehicle, seeking protection for all journeys.",
            benefits = "Safe travels and protection for the new vehicle.",
            durationMinutes = 45,
            priceOnlineUsd = 29,
            priceInTempleUsd = 59
        )
    )

    val priests = listOf(
        Priest(
            id = "p1", name = "Pt. Ramesh Shastri", title = "Vedacharya",
            templeAffiliation = "Kashi Vishwanath Seva Samiti, Varanasi",
            specializations = listOf("Griha Pravesh", "Rudrabhishek", "Navagraha Shanti"),
            languages = listOf("Hindi", "English", "Sanskrit"),
            experienceYears = 24, rating = 4.9, pujasPerformed = 3200, isOnline = true
        ),
        Priest(
            id = "p2", name = "Pt. Suresh Trivedi", title = "Jyotishacharya",
            templeAffiliation = "Shri Kalkaji Mandir, Delhi",
            specializations = listOf("Satyanarayan Katha", "Navagraha Shanti", "Kundali Milan"),
            languages = listOf("Hindi", "English"),
            experienceYears = 18, rating = 4.8, pujasPerformed = 2100, isOnline = true
        ),
        Priest(
            id = "p3", name = "Acharya Venkatesh Iyer", title = "Agama Pandit",
            templeAffiliation = "Sri Venkateswara Temple, Tirupati Parampara",
            specializations = listOf("Lakshmi Puja", "Satyanarayan Katha", "Vivah Sanskar"),
            languages = listOf("Tamil", "Telugu", "English", "Sanskrit"),
            experienceYears = 30, rating = 5.0, pujasPerformed = 4500, isOnline = true
        ),
        Priest(
            id = "p4", name = "Pt. Anil Joshi", title = "Karmakandi Pandit",
            templeAffiliation = "Haridwar Ganga Sabha",
            specializations = listOf("Pitru Paksha Shraddh", "Mundan Sanskar", "Ganesh Puja"),
            languages = listOf("Hindi", "Marathi", "English"),
            experienceYears = 15, rating = 4.7, pujasPerformed = 1650, isOnline = false
        ),
        Priest(
            id = "p5", name = "Pt. Debashish Bhattacharya", title = "Smarta Purohit",
            templeAffiliation = "Kalighat Mandir Parishad, Kolkata",
            specializations = listOf("Lakshmi Puja", "Annaprashan", "Sundarkand Path"),
            languages = listOf("Bengali", "Hindi", "English"),
            experienceYears = 21, rating = 4.8, pujasPerformed = 2800, isOnline = true
        ),
        Priest(
            id = "p6", name = "Acharya Mahesh Dwivedi", title = "Vedacharya",
            templeAffiliation = "Ujjain Mahakaleshwar Seva Trust",
            specializations = listOf("Rudrabhishek", "Vahan Puja", "Griha Pravesh"),
            languages = listOf("Hindi", "Gujarati", "English"),
            experienceYears = 27, rating = 4.9, pujasPerformed = 3900, isOnline = false
        )
    )

    /**
     * Indicative shubh muhurats for the current month. Dates are illustrative —
     * users are advised to confirm the exact muhurat with the priest / panchang.
     */
    fun muhuratsForCurrentMonth(): List<Muhurat> {
        val today = LocalDate.now()
        fun day(d: Int): LocalDate = today.withDayOfMonth(minOf(d, today.lengthOfMonth()))
        fun t(h: Int, m: Int): LocalTime = LocalTime.of(h, m)
        return listOf(
            Muhurat(day(3), t(7, 12), t(9, 35), "Griha Pravesh", "Shukla Tritiya, Uttara Phalguni nakshatra", MuhuratQuality.GOOD),
            Muhurat(day(5), t(10, 5), t(12, 20), "Vehicle Purchase / Vahan Puja", "Shukla Panchami, Hasta nakshatra", MuhuratQuality.EXCELLENT),
            Muhurat(day(9), t(6, 45), t(8, 50), "Satyanarayan Katha", "Ekadashi, Anuradha nakshatra", MuhuratQuality.EXCELLENT),
            Muhurat(day(11), t(11, 30), t(13, 40), "New Business / Ganesh Puja", "Trayodashi, Mula nakshatra", MuhuratQuality.AVERAGE),
            Muhurat(day(13), t(5, 55), t(7, 30), "Purnima Satyanarayan Puja", "Purnima, Purva Ashadha nakshatra", MuhuratQuality.EXCELLENT),
            Muhurat(day(17), t(9, 15), t(11, 45), "Mundan Sanskar", "Krishna Chaturthi, Shatabhisha nakshatra", MuhuratQuality.GOOD),
            Muhurat(day(21), t(8, 20), t(10, 10), "Griha Pravesh", "Krishna Ashtami, Ashwini nakshatra", MuhuratQuality.GOOD),
            Muhurat(day(24), t(10, 40), t(12, 55), "Property Purchase", "Krishna Ekadashi, Rohini nakshatra", MuhuratQuality.EXCELLENT),
            Muhurat(day(27), t(7, 5), t(9, 0), "Annaprashan / Namkaran", "Amavasya end, Punarvasu nakshatra", MuhuratQuality.AVERAGE),
            Muhurat(day(29), t(6, 30), t(8, 45), "Lakshmi Puja", "Shukla Dwitiya, Magha nakshatra", MuhuratQuality.GOOD)
        )
    }

    /** Portfolio seeds shown on priest profiles until priests upload their own. */
    fun seedPortfolio(): List<PortfolioItem> = listOf(
        PortfolioItem(
            id = "pf1", priestId = "p1",
            title = "Maha Rudrabhishek — Shravan Somvar",
            description = "Led the sacred Shravan Monday Rudrabhishek at Kashi with 108 bel patra offerings."
        ),
        PortfolioItem(
            id = "pf2", priestId = "p1",
            title = "Griha Pravesh for NRI families",
            description = "Performed 300+ online Griha Pravesh ceremonies for families across the US, Canada and UK."
        ),
        PortfolioItem(
            id = "pf3", priestId = "p3",
            title = "Kalyanotsavam Seva",
            description = "Conducted the annual Kalyanotsavam following Tirupati parampara."
        )
    )

    /** Sample order history seeded for a newly signed-in user. */
    fun seedBookings(): List<Booking> {
        val today = LocalDate.now()
        return listOf(
            Booking(
                id = "SNK-1041", pujaName = "Satyanarayan Katha", priestName = "Pt. Suresh Trivedi",
                mode = PujaMode.ONLINE_LIVE, date = today.minusMonths(2).withDayOfMonth(14),
                timeSlot = "08:00 AM IST", priceUsd = 69, status = BookingStatus.COMPLETED
            ),
            Booking(
                id = "SNK-1187", pujaName = "Ganesh Puja", priestName = "Pt. Ramesh Shastri",
                mode = PujaMode.IN_TEMPLE, date = today.minusMonths(1).withDayOfMonth(9),
                timeSlot = "10:30 AM IST", priceUsd = 79, status = BookingStatus.COMPLETED
            ),
            Booking(
                id = "SNK-1203", pujaName = "Rudrabhishek", priestName = "Acharya Mahesh Dwivedi",
                mode = PujaMode.IN_TEMPLE, date = today.minusDays(12),
                timeSlot = "06:00 AM IST", priceUsd = 169, status = BookingStatus.CANCELLED
            )
        )
    }
}
