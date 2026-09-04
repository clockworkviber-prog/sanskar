package com.sanskar.app.data

/**
 * Rule-based FAQ assistant. In production this would call an LLM API
 * (e.g. Claude) with the FAQ corpus as context; the keyword engine keeps
 * the demo fully offline.
 */
object SanskarAI {

    const val GREETING =
        "🙏 Namaste! I am SanskarAI, your seva assistant. Ask me anything about " +
                "bookings, online pujas, payments, prasad, muhurats or priests — " +
                "or tap a quick question below."

    val quickQuestions = listOf(
        "How do online pujas work?",
        "How do I cancel or reschedule?",
        "When will I get my prasad?",
        "What is Pay Later?",
        "Are the priests verified?"
    )

    private data class Faq(val keywords: List<String>, val answer: String)

    private val faqs = listOf(
        Faq(
            listOf("online puja", "how do online", "video call", "how does online", "live puja"),
            "For an online puja, you receive a video-call link 30 minutes before the muhurat. " +
                    "The priest takes the sankalp in your name, guides you step by step, and you " +
                    "participate from home with the samagri from the preparation checklist. " +
                    "A recording is shared afterwards."
        ),
        Faq(
            listOf("cancel", "reschedule", "change date", "change booking", "postpone"),
            "You can cancel or reschedule free of charge up to 24 hours before the puja — " +
                    "just contact support with your order ID (find it in Profile → Order history). " +
                    "Within 24 hours, a 50% fee applies as the priest and samagri are already committed."
        ),
        Faq(
            listOf("refund", "money back"),
            "Refunds for eligible cancellations are processed to your original payment method " +
                    "within 5–7 business days. If a puja could not be performed for any reason on " +
                    "our side, you receive a full refund automatically."
        ),
        Faq(
            listOf("prasad", "shipping", "delivery", "courier"),
            "For in-temple pujas, prasad is shipped from India within 3 business days of the " +
                    "puja and typically arrives in 10–14 days internationally. Tracking details " +
                    "are emailed to you. Shipping is included in the price."
        ),
        Faq(
            listOf("pay later", "payment pending", "pay at"),
            "Pay Later keeps your booking confirmed with payment pending — handy while you " +
                    "decide. You can complete payment any time from your order in Order History. " +
                    "Bookings unpaid 24 hours before the muhurat may be released."
        ),
        Faq(
            listOf("payment", "card", "stripe", "upi", "pay "),
            "Payments are processed securely by Stripe inside the app. We accept international " +
                    "cards, and you can also choose Pay Later to complete payment closer to the puja date. " +
                    "An invoice is available for every order in Order History."
        ),
        Faq(
            listOf("invoice", "receipt", "bill"),
            "Every order has a downloadable invoice — go to Profile → Order history → " +
                    "Download invoice. You can save it as a PDF from the invoice screen."
        ),
        Faq(
            listOf("muhurat", "timing", "auspicious", "local time", "timezone"),
            "The Muhurat tab shows this month's auspicious windows converted to your local " +
                    "timezone (set it in Profile → Update profile). Timings are indicative — the " +
                    "priest confirms the exact muhurat for your city when you book."
        ),
        Faq(
            listOf("priest", "pandit", "verified", "acharya", "qualification"),
            "All priests on Sanskar are verified — we check their temple affiliation, " +
                    "training lineage and experience before onboarding. You can see each priest's " +
                    "specializations, languages, rating and photos of major pujas they have " +
                    "performed on their profile."
        ),
        Faq(
            listOf("consult", "advice", "guidance", "kundali", "astro"),
            "Online Consultation is a 30-minute one-on-one video session with an experienced " +
                    "acharya for a fixed fee of $20 — great for kundali questions, muhurat advice " +
                    "or guidance on which puja suits your situation. Book it from the Home tab."
        ),
        Faq(
            listOf("samagri", "items", "checklist", "prepare", "what do i need"),
            "Every puja has a Ritual Preparation Guide — open the puja and tap " +
                    "\"Ritual preparation guide & checklist\". It lists all samagri with a tick-off " +
                    "checklist and helpful videos. For in-temple pujas, the mandir arranges everything."
        ),
        Faq(
            listOf("griha pravesh", "housewarming", "new home", "new house"),
            "Griha Pravesh is our house-warming ceremony — available online (priest guides you " +
                    "live) or performed at a partner mandir. Check the Muhurat tab for auspicious " +
                    "dates this month, and the preparation guide for the samagri you'll need."
        ),
        Faq(
            listOf("human", "agent", "support team", "talk to someone", "email", "contact"),
            "You can reach our human seva team at support@sanskar.app — we reply within " +
                    "24 hours. For urgent booking-day issues, mention URGENT and your order ID " +
                    "in the subject line."
        ),
        Faq(
            listOf("hello", "hi", "hey", "namaste", "hola"),
            "Namaste! 🙏 How can I help you today? You can ask about bookings, payments, " +
                    "prasad, muhurats, priests, or preparing for your puja."
        ),
        Faq(
            listOf("thank", "thanks", "dhanyavad"),
            "Dhanyavad! 🙏 May your puja bring peace and prosperity. Is there anything else " +
                    "I can help with?"
        )
    )

    fun reply(question: String): String {
        val q = question.lowercase()
        val match = faqs.firstOrNull { faq -> faq.keywords.any { q.contains(it) } }
        return match?.answer
            ?: ("I'm still learning about that! 🙏 For anything I can't answer, our human " +
                    "seva team at support@sanskar.app will gladly help. Meanwhile, try asking about " +
                    "bookings, online pujas, payments, prasad shipping, muhurats or priests.")
    }
}
