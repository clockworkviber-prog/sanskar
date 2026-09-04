import { NextRequest, NextResponse } from "next/server";
import { getStripe } from "@/lib/stripe";
import { prisma } from "@/lib/prisma";

/**
 * Stripe webhook — the source of truth for "did this payment actually
 * succeed," since a client can lie or drop offline mid-checkout. Register
 * this endpoint's URL (<your-domain>/api/stripe/webhook) in the Stripe
 * Dashboard, subscribed to at least payment_intent.succeeded and
 * payment_intent.payment_failed, and set STRIPE_WEBHOOK_SECRET from the
 * signing secret Stripe shows you there.
 */
export async function POST(req: NextRequest) {
  const webhookSecret = process.env.STRIPE_WEBHOOK_SECRET;
  if (!webhookSecret) {
    console.error("STRIPE_WEBHOOK_SECRET is not set — refusing to process webhook.");
    return NextResponse.json({ error: "Webhook not configured." }, { status: 503 });
  }

  const signature = req.headers.get("stripe-signature");
  const rawBody = await req.text();
  if (!signature) {
    return NextResponse.json({ error: "Missing Stripe signature." }, { status: 400 });
  }

  let stripe;
  let event;
  try {
    stripe = getStripe();
    event = stripe.webhooks.constructEvent(rawBody, signature, webhookSecret);
  } catch (err) {
    console.error("Stripe webhook signature verification failed:", err);
    return NextResponse.json({ error: "Invalid signature." }, { status: 400 });
  }

  switch (event.type) {
    case "payment_intent.succeeded": {
      const intent = event.data.object as { metadata?: { bookingId?: string } };
      const bookingId = intent.metadata?.bookingId;
      if (bookingId) {
        await prisma.transaction.updateMany({
          where: { bookingId, status: "PENDING" },
          data: { status: "PAID" },
        });
      }
      break;
    }
    case "payment_intent.payment_failed": {
      const intent = event.data.object as { metadata?: { bookingId?: string } };
      const bookingId = intent.metadata?.bookingId;
      if (bookingId) {
        await prisma.transaction.updateMany({
          where: { bookingId, status: "PENDING" },
          data: { status: "FAILED" },
        });
      }
      break;
    }
    default:
      // Other event types are ignored for now.
      break;
  }

  return NextResponse.json({ received: true });
}
