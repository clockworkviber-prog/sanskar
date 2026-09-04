import { NextRequest, NextResponse } from "next/server";
import { z } from "zod";
import { getSession } from "@/lib/session";
import { getStripe } from "@/lib/stripe";
import { prisma } from "@/lib/prisma";

const schema = z.object({
  bookingId: z.string().min(1),
});

/**
 * Creates a Stripe PaymentIntent for a booking that already exists in the
 * database, so the charged amount always comes from the server-side
 * booking record — never from a client-supplied number. Requires a signed-
 * in devotee session and ownership of the booking.
 */
export async function POST(req: NextRequest) {
  const session = await getSession();
  if (!session || session.role !== "devotee") {
    return NextResponse.json({ error: "Please log in to pay." }, { status: 401 });
  }

  const parsed = schema.safeParse(await req.json().catch(() => null));
  if (!parsed.success) {
    return NextResponse.json({ error: "Missing bookingId." }, { status: 400 });
  }

  const booking = await prisma.booking.findUnique({ where: { id: parsed.data.bookingId } });
  if (!booking || booking.userId !== session.sub) {
    return NextResponse.json({ error: "Booking not found." }, { status: 404 });
  }
  if (booking.status === "CANCELLED") {
    return NextResponse.json({ error: "This booking has been cancelled." }, { status: 409 });
  }

  let stripe;
  try {
    stripe = getStripe();
  } catch (e) {
    console.error(e);
    return NextResponse.json(
      { error: "Payments are not configured yet. Please try again later." },
      { status: 503 }
    );
  }

  const paymentIntent = await stripe.paymentIntents.create({
    amount: booking.priceUsd * 100, // Stripe expects the smallest currency unit (cents)
    currency: "usd",
    metadata: { bookingId: booking.id, userId: session.sub },
    automatic_payment_methods: { enabled: true },
  });

  // Avoid piling up duplicate PENDING rows if the checkout page re-renders
  // or the devotee retries before paying.
  const existingPending = await prisma.transaction.findFirst({
    where: { bookingId: booking.id, method: "stripe", status: "PENDING" },
  });
  if (!existingPending) {
    await prisma.transaction.create({
      data: {
        bookingId: booking.id,
        amountUsd: booking.priceUsd,
        method: "stripe",
        status: "PENDING",
      },
    });
  }

  return NextResponse.json({ clientSecret: paymentIntent.client_secret });
}
