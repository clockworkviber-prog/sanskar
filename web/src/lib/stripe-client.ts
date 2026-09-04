"use client";

import { loadStripe, Stripe } from "@stripe/stripe-js";

// Client-side only. NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY is intentionally
// exposed to the browser — that's what a Stripe publishable key is for.
let stripePromise: Promise<Stripe | null> | null = null;

export function getStripeClient(): Promise<Stripe | null> {
  if (!stripePromise) {
    const key = process.env.NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY;
    if (!key) {
      console.error("NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY is not set.");
      return Promise.resolve(null);
    }
    stripePromise = loadStripe(key);
  }
  return stripePromise;
}
