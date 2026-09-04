"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { Elements, PaymentElement, useElements, useStripe } from "@stripe/react-stripe-js";
import { getStripeClient } from "@/lib/stripe-client";

export default function CheckoutClient({ bookingId, amountUsd }: { bookingId: string; amountUsd: number }) {
  const [clientSecret, setClientSecret] = useState<string | null>(null);
  const [unavailable, setUnavailable] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch("/api/stripe/create-payment-intent", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ bookingId }),
    })
      .then(async (res) => {
        if (res.status === 503) {
          setUnavailable(true);
          return;
        }
        const data = await res.json();
        if (res.ok) setClientSecret(data.clientSecret);
        else setUnavailable(true);
      })
      .catch(() => setUnavailable(true))
      .finally(() => setLoading(false));
  }, [bookingId]);

  if (loading) {
    return <p className="mt-6 text-center text-sm text-muted">Loading payment options…</p>;
  }

  if (unavailable || !clientSecret) {
    return (
      <div className="mt-6 space-y-3">
        <p className="rounded-lg bg-amber-50 px-3 py-2 text-sm text-amber-800">
          Card payment isn&apos;t set up on this deployment yet. You can still confirm your booking and pay
          later.
        </p>
        <PayLaterButton bookingId={bookingId} />
      </div>
    );
  }

  return (
    <Elements stripe={getStripeClient()} options={{ clientSecret }}>
      <PaymentForm bookingId={bookingId} amountUsd={amountUsd} />
    </Elements>
  );
}

function PaymentForm({ bookingId, amountUsd }: { bookingId: string; amountUsd: number }) {
  const stripe = useStripe();
  const elements = useElements();
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function onPay(e: React.FormEvent) {
    e.preventDefault();
    if (!stripe || !elements) return;
    setSubmitting(true);
    setError(null);
    const { error: stripeError } = await stripe.confirmPayment({
      elements,
      confirmParams: { return_url: `${window.location.origin}/orders?paid=${bookingId}` },
    });
    // confirmPayment only returns if there's an immediate error — success navigates via return_url.
    if (stripeError) {
      setError(stripeError.message ?? "Payment failed. Please try again.");
      setSubmitting(false);
    }
  }

  return (
    <form onSubmit={onPay} className="mt-6 space-y-4">
      <PaymentElement />
      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      <button type="submit" disabled={!stripe || submitting} className="btn-primary">
        {submitting ? "Processing…" : `Pay $${amountUsd}`}
      </button>
      <PayLaterButton bookingId={bookingId} />
    </form>
  );
}

function PayLaterButton({ bookingId }: { bookingId: string }) {
  const router = useRouter();
  const [loading, setLoading] = useState(false);

  async function onPayLater() {
    setLoading(true);
    await fetch("/api/bookings/pay-later", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ bookingId }),
    });
    router.push("/orders");
  }

  return (
    <button
      type="button"
      onClick={onPayLater}
      disabled={loading}
      className="w-full rounded-full border border-[#c9a88f] px-6 py-3 text-sm font-medium hover:bg-cream disabled:opacity-60"
    >
      {loading ? "Saving…" : "Pay Later — keep booking & go to my orders"}
    </button>
  );
}
