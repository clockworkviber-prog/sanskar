# Sanskar 🪔

**Your Mandir, Wherever You Are** — an Android app for the Indian diaspora to book Hindu puja
rituals (Griha Pravesh, Satyanarayan Katha, Rudrabhishek, and more) performed **online live over
video** or **at partner mandirs in India on your behalf**.

## Features

- **Login / Sign up** — standard email + password auth (in-memory demo implementation).
  Demo account: `demo@sanskar.app` / `demo123`
- **Puja services tile view** — 12 pujas including Griha Pravesh, Satyanarayan Katha, Ganesh Puja,
  Lakshmi Puja, Rudrabhishek, Navagraha Shanti, Mundan, Annaprashan, Pitru Paksha Shraddh,
  Vivah Sanskar, Sundarkand Path, and Vahan Puja — each with online and in-temple pricing.
- **Booking flow** — choose online/in-temple mode, date, time slot, priest, and sankalp name.
- **Shubh Muhurat** — indicative auspicious dates & time windows for the current month with
  panchang notes and quality rating.
- **Priests** — verified pandits with specialization, languages, experience, rating, and a live
  online-availability indicator (filterable).
- **Profile** — update profile details (name, phone, city, country, gotra) and view
  **order history** of past and upcoming bookings.

## Tech stack

- Kotlin + Jetpack Compose (Material 3)
- Navigation Compose with bottom navigation (Home / Muhurat / Priests / Profile)
- Single `AppViewModel` state holder with mock in-memory data (`data/MockData.kt`)
- Min SDK 26, target SDK 35, AGP 8.5.2, Kotlin 2.0, Gradle 8.9

## Building & running

1. Open the `Sanskar` folder in **Android Studio** (Koala or newer). It will download the
   Android SDK components and sync Gradle automatically.
2. Run the `app` configuration on an emulator or device (Android 8.0+).

Or from the command line (requires the Android SDK and JDK 17):

```
gradlew.bat assembleDebug
```

The APK lands in `app/build/outputs/apk/debug/`.

## Going to production

The app is fully functional as an offline demo. To productionize, replace:

- `AppViewModel` auth with Firebase Auth / your backend
- `MockData` with a REST/Firestore catalog (pujas, priests, muhurats from a real panchang API)
- Booking with a payments provider (Stripe/Razorpay) and a scheduling backend
- Add push notifications for booking reminders and a video-call SDK (e.g. Agora/Zoom) for live pujas

## Disclaimer

Muhurat data in the app is illustrative. Exact auspicious timings depend on location and
panchang — users are advised to confirm with the priest at booking time.
