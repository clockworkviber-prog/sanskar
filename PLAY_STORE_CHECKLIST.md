# Sanskar — Production Launch Checklist

**Architecture decision (current):** the Neon-backed web app
(`web/`) is the real product — real accounts, working forgot-password,
double opt-in email verification, one shared database. The Android app
(`app/`) still runs entirely offline against its own on-device database and
has **no shared accounts, no email, and no forgot-password** — it's not
ready to represent your production system as-is. Two things must happen
before an Android Play Store release makes sense:

1. **Rewire the Android app to call the web app's API** instead of its
   local Room database, so both surfaces share one account system. This is
   a real (but bounded) project: replace `SanskarRepository`'s Room calls
   with HTTP calls to the same `/api/auth/*` endpoints already built in
   `web/`, add a network layer (Retrofit/Ktor), and decide what (if
   anything) stays cached locally for offline use. Not done yet — say the
   word when you want to start this and I'll scope it properly rather than
   rush it.
2. **Real payment processing.** Both apps currently show a simulated
   "Payment Completed" screen with no money actually moving — see
   [`CheckoutScreen.kt`](app/src/main/java/com/sanskar/app/ui/screens/CheckoutScreen.kt).
   Wiring a real gateway (Stripe, Razorpay, etc.) requires *your* merchant/
   business account and can't be done without your credentials — I can
   integrate it as soon as you have one.

Until both are done, treat the Android app as a **pre-release build** —
don't submit it to the Play Store yet. The checklist below still applies
once it's ready; it just isn't the current blocker.

---

## Web app (`web/`) — the real product today

### Done
- [x] Real accounts in Neon Postgres (devotee + priest), bcrypt-hashed
      passwords, no fake/demo data seeded by default (see
      `prisma/seed.ts` vs the clearly-labeled `prisma/seed-dev.ts`)
- [x] **Forgot password**, role-aware, for both "I am a Devotee" and
      "I am a Priest" — single-use, 1-hour-expiry reset links
- [x] **Double opt-in signup verification** — accounts can't log in until
      the emailed link is clicked; resend-verification available
- [x] Sessions via signed, httpOnly JWT cookie
- [x] Deployable to Vercel as-is (`vercel.json`, build wires
      `prisma generate` automatically)

### Before real users sign up
1. **Get a Neon production database** and a **Resend account with a
   verified sending domain** (see `web/README.md` and `.env.example`) —
   `onboarding@resend.dev` is fine for testing but looks unprofessional
   and has stricter limits for real users.
2. **Set every env var in Vercel** (`DATABASE_URL`, `DIRECT_URL`,
   `AUTH_SECRET`, `RESEND_API_KEY`, `EMAIL_FROM`, `NEXT_PUBLIC_APP_URL`
   pointed at your real domain).
3. **Run the production-safe seed only**: `npm run db:push && npm run
   db:seed` against your real Neon database. **Never run `db:seed:dev`**
   against it — that creates a publicly-known demo login.
4. **Real payment integration** (see architecture note above) — nothing
   in the current app should be presented to real users as "pay" until
   this is wired to a real gateway.
5. **Build out the rest of the product on web**: booking flow, order
   history, priest dashboard, invoices — currently only exist in the
   Android app / static mockup. The database schema already supports all
   of it (`prisma/schema.prisma`); each screen is now "a page + an API
   route," not a redesign.
6. **A real privacy policy** at a public URL — see `PRIVACY.md` (still a
   template with placeholders) — and terms of service.
7. **Rate limiting** on `/api/auth/*` (login, forgot-password, signup) —
   not yet added. Without it, someone could brute-force passwords or spam
   verification/reset emails. Vercel's own Firewall (free tier includes
   basic rate limiting) or a small in-route limiter is worth adding before
   heavy traffic.
8. **Monitoring**: Vercel's built-in logs/analytics are a starting point;
   consider Sentry (has a free tier) for error tracking once live.

---

## Android app (`app/`) — once rewired to the shared backend

### Already in place (still true after the rewire)
- [x] Release build type: `minifyEnabled true`, `shrinkResources true`,
      ProGuard rules
- [x] Release signing wired via `keystore.properties` (gitignored) — see
      `keystore.properties.example`
- [x] `targetSdk 35`
- [x] Cleartext traffic disabled, network security config in place
- [x] Local database excluded from Android auto-backup / device transfer
- [x] Adaptive icon + round icon variant, predictive back support
- [x] No demo accounts, no demo credentials shown on screen
- [x] Only requests the `INTERNET` permission

### Still to do before submitting
1. **Complete the backend rewire** described above (forgot-password,
   shared accounts, real bookings need this first).
2. **Real payment gateway**, same as web.
3. **Generate a real release keystore**, store it and its passwords in a
   secrets manager — never commit them.
4. **Publish the real privacy policy** URL in Play Console → App content.
5. **Play Console → App content**: data safety form (account info,
   payment info via your chosen gateway, priest photos), content rating
   questionnaire, target audience declaration.
6. **Store listing**: screenshots, feature graphic, description, 512×512
   icon, category ("Lifestyle" fits).
7. **Test on real devices** across a few Android versions — only
   emulator/build-machine testing has happened so far.
8. **Crash reporting**: add Firebase Crashlytics or similar.
9. **Internal testing track first** — invite testers, check Play
   Console's automated pre-launch report before any public release.

---

## Known scope limits (worth knowing, not blockers for getting started)

- Priest photo portfolio uploads are local content URIs on Android;
  they'll need to move to real cloud storage (S3/Cloudinary/Vercel Blob)
  once the backend rewire happens, so photos are visible to all devotees
  regardless of device.
- SanskarAI is a keyword-matched FAQ bot, not a real LLM — fine to ship
  as-is, or swap for a real API call later.
- The web app currently has no rate limiting or CAPTCHA on auth endpoints
  — acceptable for early testing, not for a public launch (see item 7
  above).
