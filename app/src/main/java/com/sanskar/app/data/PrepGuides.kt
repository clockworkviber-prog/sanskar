package com.sanskar.app.data

data class PrepVideo(val title: String, val url: String)

data class PrepGuide(
    val pujaId: String,
    val intro: String,
    val checklist: List<String>,
    val videos: List<PrepVideo>
)

private fun yt(query: String) =
    "https://www.youtube.com/results?search_query=" + query.replace(' ', '+')

/**
 * Ritual preparation guides per puja: samagri checklist + popular YouTube
 * videos (linked via search so results stay fresh and never go dead).
 */
object PrepGuides {

    private val commonItems = listOf(
        "Take a bath and wear fresh/traditional clothes",
        "Clean the puja area and lay a clean cloth or chowki",
        "Diya with ghee/oil, cotton wicks and matchbox",
        "Incense sticks (agarbatti) and dhoop",
        "Roli/kumkum, haldi and akshat (unbroken rice)",
        "Fresh flowers and garland",
        "Fruits and sweets for prasad",
        "Betel leaves, betel nuts (supari) and coconut",
        "Kalash (small pot) with clean water and gangajal if available"
    )

    private val guides = listOf(
        PrepGuide(
            pujaId = "griha_pravesh",
            intro = "Enter the new home only at the confirmed muhurat. The lady of the house traditionally enters first with a kalash, right foot forward.",
            checklist = commonItems + listOf(
                "Mango-leaf toran for the main door",
                "Swastik drawn with kumkum on both sides of entrance",
                "Milk and a new vessel for the boiling-milk ritual",
                "Navagraha and Vastu havan samagri (if havan included)",
                "New broom and salt for the house",
                "Keys of the new house kept in the puja"
            ),
            videos = listOf(
                PrepVideo("Griha Pravesh puja vidhi — step by step", yt("griha pravesh puja vidhi step by step")),
                PrepVideo("Griha Pravesh samagri list", yt("griha pravesh puja samagri list")),
                PrepVideo("Vastu tips for entering a new home", yt("griha pravesh vastu tips new home"))
            )
        ),
        PrepGuide(
            pujaId = "satyanarayan",
            intro = "Traditionally performed on Purnima or after happy milestones. The whole family should sit for the katha and stay until aarti.",
            checklist = commonItems + listOf(
                "Banana leaves/stems to decorate the puja area",
                "Panchamrit — milk, curd, honey, ghee and sugar",
                "Sheera/panjiri prasad (wheat flour, sugar, ghee) — 1.25 measure",
                "Tulsi leaves for the prasad",
                "Photo/idol of Lord Satyanarayan (Vishnu)",
                "Yellow cloth and yellow flowers (marigold preferred)"
            ),
            videos = listOf(
                PrepVideo("Satyanarayan katha vidhi at home", yt("satyanarayan katha puja vidhi at home")),
                PrepVideo("Satyanarayan puja samagri list", yt("satyanarayan puja samagri list")),
                PrepVideo("Satyanarayan aarti", yt("satyanarayan bhagwan aarti"))
            )
        ),
        PrepGuide(
            pujaId = "ganesh",
            intro = "Ganesh ji is invoked before every new beginning. Keep the idol facing east or west, never towards the south.",
            checklist = commonItems + listOf(
                "Ganesh idol (clay preferred) or photo",
                "Durva grass (21 blades) — essential for Ganesh puja",
                "Modak or besan/motichoor laddoo for bhog",
                "Red flowers and red cloth",
                "Sindoor for the idol"
            ),
            videos = listOf(
                PrepVideo("Ganesh puja vidhi at home", yt("ganesh puja vidhi at home")),
                PrepVideo("Ganesh puja samagri list", yt("ganesh puja samagri list")),
                PrepVideo("Ganesh aarti — Sukhkarta Dukhharta", yt("sukhkarta dukhharta ganesh aarti"))
            )
        ),
        PrepGuide(
            pujaId = "lakshmi",
            intro = "Best performed on Friday evenings, Purnima or Diwali. Keep the house clean and well-lit — Lakshmi ji is welcomed with light.",
            checklist = commonItems + listOf(
                "Lakshmi idol/photo with Ganesh ji",
                "Lotus flowers (or pink/red flowers)",
                "Kheer or white sweets for bhog",
                "Coins or silver items to place in the puja",
                "Rice and a small pot of turmeric water",
                "Rangoli at the entrance"
            ),
            videos = listOf(
                PrepVideo("Lakshmi puja vidhi at home", yt("lakshmi puja vidhi at home")),
                PrepVideo("Lakshmi puja samagri list", yt("lakshmi puja samagri list")),
                PrepVideo("Lakshmi aarti — Om Jai Lakshmi Mata", yt("om jai lakshmi mata aarti"))
            )
        ),
        PrepGuide(
            pujaId = "rudrabhishek",
            intro = "Performed on a Shivling, ideally on Mondays, Pradosh or during Shravan. Devotees observe a light/satvik diet on the day.",
            checklist = commonItems + listOf(
                "Shivling (if at home) or temple visit arranged",
                "Bel patra (bilva leaves) — washed, stems removed",
                "Panchamrit items separately: milk, curd, honey, ghee, sugar",
                "Gangajal — the key abhishek liquid",
                "White flowers and white sandalwood paste",
                "Bhasma/vibhuti and rudraksha (if available)"
            ),
            videos = listOf(
                PrepVideo("Rudrabhishek puja vidhi", yt("rudrabhishek puja vidhi")),
                PrepVideo("Rudrabhishek samagri list", yt("rudrabhishek puja samagri list")),
                PrepVideo("Shiva Rudrashtakam chanting", yt("rudrashtakam shiva stotra"))
            )
        ),
        PrepGuide(
            pujaId = "navagraha",
            intro = "Keep your birth details (date, time, place) ready — the priest tailors the sankalp and mantra counts to your kundali.",
            checklist = commonItems + listOf(
                "Navadhanya — nine types of grains for the nine planets",
                "Nine colours of cloth pieces (or as guided by priest)",
                "Sesame oil and a separate lamp for Shani",
                "Birth details / kundali shared with the priest in advance",
                "Donation items (til, urad, mustard oil) as advised"
            ),
            videos = listOf(
                PrepVideo("Navagraha shanti puja vidhi", yt("navagraha shanti puja vidhi")),
                PrepVideo("Navagraha puja samagri", yt("navagraha puja samagri list")),
                PrepVideo("Navagraha mantra chanting", yt("navagraha mantra chanting"))
            )
        ),
        PrepGuide(
            pujaId = "mundan",
            intro = "Choose a muhurat suited to the child's nakshatra. Keep the child well-fed and rested before the ceremony.",
            checklist = commonItems + listOf(
                "New razor/blade (barber usually arranges)",
                "Wheat-flour dough ball to collect the hair",
                "Gangajal to wash the head",
                "New clothes for the child",
                "Small gift/dakshina for the barber",
                "Sweets for distribution"
            ),
            videos = listOf(
                PrepVideo("Mundan sanskar vidhi", yt("mundan sanskar puja vidhi")),
                PrepVideo("Mundan ceremony preparation", yt("mundan ceremony preparation checklist")),
                PrepVideo("Why mundan sanskar is performed", yt("mundan sanskar significance"))
            )
        ),
        PrepGuide(
            pujaId = "annaprashan",
            intro = "The baby's first solid food — usually kheer — is fed at the muhurat by a maternal uncle or grandparent.",
            checklist = commonItems + listOf(
                "Kheer (rice pudding) freshly prepared for the first feed",
                "New silver spoon or bowl (traditional)",
                "New clothes for the baby",
                "Objects for the career-picking ritual: book, pen, money, food, clay",
                "Banana leaf or new plate for serving"
            ),
            videos = listOf(
                PrepVideo("Annaprashan sanskar vidhi", yt("annaprashan sanskar puja vidhi")),
                PrepVideo("Annaprashan preparation at home", yt("annaprashan ceremony preparation")),
                PrepVideo("Annaprashan rice ceremony explained", yt("annaprashan rice ceremony significance"))
            )
        ),
        PrepGuide(
            pujaId = "pitru",
            intro = "Performed on the ancestor's tithi or during Pitru Paksha. Keep the departed's name, gotra and relation ready for the sankalp.",
            checklist = commonItems + listOf(
                "Black sesame seeds (til) — essential for tarpan",
                "Kusha grass and a copper/brass water pot",
                "Barley flour and rice for pind daan (if included)",
                "Food to offer to a brahmin, cow, dog and crow",
                "Departed ancestor's name, gotra and relation noted down",
                "White clothes for the karta (person performing)"
            ),
            videos = listOf(
                PrepVideo("Shraddh and tarpan vidhi", yt("shraddh tarpan vidhi pitru paksha")),
                PrepVideo("Pitru Paksha rituals explained", yt("pitru paksha shraddh rituals explained")),
                PrepVideo("Tarpan samagri and method", yt("tarpan vidhi samagri"))
            )
        ),
        PrepGuide(
            pujaId = "vivah",
            intro = "Share both kundalis with the acharya in advance. The mandap, havan kund and seating are arranged at the venue.",
            checklist = commonItems + listOf(
                "Mandap decoration and havan kund at the venue",
                "Havan samagri, samidha (wood) and ghee for the sacred fire",
                "Varmala (2 garlands), mangalsutra and sindoor",
                "Kanyadaan items as guided by the acharya",
                "Both kundalis / birth details shared in advance",
                "Rice, flowers and shagun items for saat pheras"
            ),
            videos = listOf(
                PrepVideo("Vedic vivah sanskar rituals explained", yt("hindu vivah sanskar rituals explained")),
                PrepVideo("Saptapadi — the seven vows", yt("saptapadi seven vows hindu wedding meaning")),
                PrepVideo("Hindu wedding samagri checklist", yt("hindu wedding puja samagri checklist"))
            )
        ),
        PrepGuide(
            pujaId = "sundarkand",
            intro = "Usually recited on Tuesday or Saturday. All present may join the recitation — books/screens for followers help.",
            checklist = commonItems + listOf(
                "Ramcharitmanas or Sundarkand book (extra copies for family)",
                "Hanuman ji photo or idol",
                "Sindoor and chameli (jasmine) oil for Hanuman ji",
                "Besan or boondi laddoo for bhog",
                "Red flowers and red cloth"
            ),
            videos = listOf(
                PrepVideo("Sundarkand path full with lyrics", yt("sundarkand path full with lyrics")),
                PrepVideo("How to do Sundarkand path at home", yt("sundarkand path vidhi at home")),
                PrepVideo("Hanuman Chalisa", yt("hanuman chalisa"))
            )
        ),
        PrepGuide(
            pujaId = "consult",
            intro = "Come prepared with your questions — 30 minutes goes quickly. The acharya can advise on rituals, muhurat, kundali and sanskars.",
            checklist = listOf(
                "Write down your questions in order of priority",
                "Keep birth details ready (date, time, place) if asking kundali questions",
                "Keep relevant documents/photos handy (e.g. new home layout for Vastu)",
                "Find a quiet spot with a stable internet connection",
                "Keep a notebook to jot down the guidance and remedies"
            ),
            videos = listOf(
                PrepVideo("How to read a janma kundali — basics", yt("janma kundali basics explained")),
                PrepVideo("Understanding muhurat selection", yt("muhurat selection explained panchang")),
                PrepVideo("Sixteen sanskars explained", yt("16 sanskars hinduism explained"))
            )
        ),
        PrepGuide(
            pujaId = "vahan",
            intro = "Wash the vehicle before the puja and bring it to an open space. Keep the keys in the puja thali.",
            checklist = commonItems + listOf(
                "Vehicle washed and parked in an open, clean spot",
                "Four lemons — one under each wheel",
                "Flower garland for the vehicle",
                "Swastik drawn with kumkum on the bonnet/handle",
                "Coconut to break before first drive",
                "Vehicle keys placed in the puja thali"
            ),
            videos = listOf(
                PrepVideo("Vahan puja vidhi for new vehicle", yt("vahan puja vidhi new car")),
                PrepVideo("New vehicle puja samagri", yt("new vehicle puja samagri list")),
                PrepVideo("Why lemons are used in vahan puja", yt("vahan puja lemon significance"))
            )
        )
    )

    fun forPuja(pujaId: String): PrepGuide? = guides.firstOrNull { it.pujaId == pujaId }
}
