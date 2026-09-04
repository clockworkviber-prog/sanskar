import Stripe from "stripe";

// Server-side only — this file must never be imported from a "use client"
// component. STRIPE_SECRET_KEY has no NEXT_PUBLIC_ prefix, so Next.js keeps
// it out of the client bundle by default; importing this from client code
// would still be a mistake to avoid regardless.
let _stripe: Stripe | null = null;

export function getStripe(): Stripe {
  if (_stripe) return _stripe;
  const key = process.env.STRIPE_SECRET_KEY;
  if (!key) {
    throw new Error(
      "STRIPE_SECRET_KEY is not set. Add it as an environment variable " +
        "(Vercel project settings, or .env.local for dev) — never commit it " +
        "or paste it in chat."
    );
  }
  _stripe = new Stripe(key, { apiVersion: "2026-08-26.dahlia" });
  return _stripe;
}
