# Sanskar Web (Next.js + Neon + Vercel)

The Vercel-hosted frontend **and** backend for Sanskar, replacing the Android
app's local Room database with a shared Neon Postgres database. Built with
Next.js 14 (App Router), Prisma, and email-based auth (double opt-in signup +
forgot-password), matching the Android app's devotee/priest role split.

## What's implemented

- **Login** for both roles ("I am a Devotee" / "I am a Priest"), each with
  its own **Forgot password** flow.
- **Signup** (devotee) and **Priest Registration**, each triggering a
  **double opt-in verification email** — accounts can't log in until the
  link is clicked.
- **Forgot password** — request a reset link by email, set a new password;
  links expire (1h) and are single-use.
- **Puja catalog** (`/dashboard`) — pulled live from the database.
- **Booking flow** (`/puja/[id]`) — pick online/in-temple, date, time slot,
  an optional priest (filtered to online priests for online pujas), and a
  sankalp name. The price is always computed server-side from the puja
  record — the client never gets to say what something costs.
- **Checkout** (`/checkout/[bookingId]`) — real Stripe PaymentElement when
  `STRIPE_SECRET_KEY`/`NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY` are set, with a
  graceful "not configured yet" fallback when they aren't. **Pay Later** is
  always available as an alternative.
- **Order history** (`/orders`) — real bookings with live payment status,
  and a "Complete payment" link back to checkout for anything still unpaid.
- **Priest dashboard** (`/priest-dashboard`) — three live widgets, polling
  `/api/priest/dashboard-data` every 20s:
  - **Notifications** — a priest is notified when a devotee books them by
    name; unread count badge, mark-all-read.
  - **Orders in queue — requested for you** — bookings a devotee assigned
    to this priest specifically, with **Accept** / **Decline**. Declining
    releases the booking back to the open queue rather than leaving the
    devotee stuck.
  - **Open queue** — unassigned bookings ("To be assigned") any priest can
    **claim** on a first-come basis; claiming is done via an atomic
    `updateMany` guard so two priests can't both claim the same booking.
  - This is polling, not real push notifications — see "What's not yet
    built" below.
- Full Prisma schema for the rest of the app's data — priest portfolio,
  prep checklists, muhurats — so those screens are ready to be built the
  same way.

## What's not yet built

Priest portfolio uploads, the SanskarAI chatbot, Muhurat browsing, and
ritual prep checklists still only exist in the Android app and the static
`website/` mockup. The data model for all of it already exists in
`prisma/schema.prisma` — porting a screen is "build the page + a couple of
API routes," not a redesign.

**Notifications are polling-based, not push.** The priest dashboard
re-fetches every 20 seconds while the tab is open — there's no email/SMS/
web-push alert if a priest isn't looking at the dashboard. Wiring a real
push channel (web push, or an email via Resend, already set up for auth)
is a natural next step once this matters for real usage.

## Schema changed — re-run `db:push`

This update added `Notification`, `PriestResponse`, and a `priestResponse`
column on `Booking`. Run `npm run db:push` again (pointed at your Neon
database) before deploying, or the new priest dashboard API routes will
fail against the old schema.

## Local setup

1. **Create a Neon project** at [neon.tech](https://neon.tech) (free tier is
   fine). From the dashboard, copy the **pooled** connection string and the
   **direct** connection string.
2. **Create a Resend account** at [resend.com](https://resend.com) (free
   tier) and get an API key — needed for verification/reset emails to
   actually send. Without it, emails are logged to the server console
   instead (fine for local testing).
3. Copy `.env.example` to `.env.local` and fill in `DATABASE_URL`,
   `DIRECT_URL`, `AUTH_SECRET` (`openssl rand -hex 32`), and optionally
   `RESEND_API_KEY` / `EMAIL_FROM`.
4. Install and push the schema:
   ```
   npm install
   npm run db:push
   npm run db:seed
   ```
   `db:seed` loads only the real puja catalog — **no fake accounts**. If
   you want a throwaway login for local testing, also run
   `npm run db:seed:dev` (creates `demo@sanskar.app` / `demo123` and
   `priest@sanskar.app` / `priest123`, pre-verified). **Never run
   `db:seed:dev` against your production database** — see the warning at
   the top of `prisma/seed-dev.ts`.
5. Run it:
   ```
   npm run dev
   ```
   Open http://localhost:3000 and sign up for a real account (or use the
   dev demo login above if you ran `db:seed:dev`).

## Deploying to Vercel

1. Push this repo (or just the `web/` folder as its own repo) to GitHub.
2. In Vercel: **New Project** → import the repo → if `web/` isn't already
   the repo root, set **Root Directory** to `web`.
3. Vercel auto-detects Next.js. Add the same environment variables from
   `.env.example` in **Project Settings → Environment Variables**
   (`DATABASE_URL`, `DIRECT_URL`, `AUTH_SECRET`, `RESEND_API_KEY`,
   `EMAIL_FROM`, and `NEXT_PUBLIC_APP_URL` set to your production domain
   once you have one — verification/reset email links are built from it).
4. Deploy. The build runs `prisma generate && next build` automatically
   (see `vercel.json` / `package.json`).
5. Run `npm run db:push` and `npm run db:seed` (the **catalog-only** one)
   once **locally**, pointed at your production Neon database via
   `.env.local` (or `.env.production.local`), to create the schema and
   real content — Vercel's build step does not run migrations or seeding
   for you. **Do not run `db:seed:dev` here.**

## Stripe (payments)

`src/lib/stripe.ts` (server) and `src/lib/stripe-client.ts` (browser) are
wired up, plus two working API routes:

- `POST /api/stripe/create-payment-intent` — takes a `bookingId`, looks the
  booking up server-side (never trusts a client-supplied amount), and
  creates a Stripe PaymentIntent for its real price.
- `POST /api/stripe/webhook` — Stripe's source of truth for whether a
  payment actually succeeded; marks the matching transaction PAID/FAILED.

Neither is wired into a checkout **page** yet — that needs a real
booking-creation flow first (see "What's not yet ported" above). Once that
exists, a checkout page just needs Stripe Elements calling
`getStripeClient()` and POSTing to `create-payment-intent`.

**Setup**: get your keys from
[dashboard.stripe.com/apikeys](https://dashboard.stripe.com/apikeys) — use
the **test-mode** keys (`pk_test_...` / `sk_test_...`) until checkout is
fully built and verified end-to-end; switch to live keys only when you're
ready to take real payments. Add a webhook endpoint at
`https://<your-domain>/api/stripe/webhook` in the Dashboard, subscribed to
`payment_intent.succeeded` and `payment_intent.payment_failed`, and copy
its signing secret into `STRIPE_WEBHOOK_SECRET`.

## Notes on the auth design

- Passwords are hashed with bcrypt (12 rounds) — never stored in plain
  text, matching the Android app's approach (which uses PBKDF2 instead;
  either is fine, they're not required to match).
- Sessions are a signed JWT in an httpOnly, `SameSite=Lax` cookie — no
  separate session table needed for this scale.
- Verification and password-reset tokens are single-use, random 256-bit
  hex strings stored server-side with an expiry (24h / 1h respectively).
- The forgot-password and resend-verification endpoints always return a
  generic success response whether or not the email exists, so the API
  can't be used to enumerate registered accounts.
