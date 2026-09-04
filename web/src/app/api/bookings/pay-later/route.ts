import { NextRequest, NextResponse } from "next/server";
import { getSession } from "@/lib/session";
import { prisma } from "@/lib/prisma";

/** Records that the devotee chose to defer payment. Booking stays confirmed. */
export async function POST(req: NextRequest) {
  const session = await getSession();
  if (!session || session.role !== "devotee") {
    return NextResponse.json({ error: "Please log in." }, { status: 401 });
  }

  const body = await req.json().catch(() => null);
  const bookingId = body?.bookingId as string | undefined;
  if (!bookingId) return NextResponse.json({ error: "Missing bookingId." }, { status: 400 });

  const booking = await prisma.booking.findUnique({ where: { id: bookingId } });
  if (!booking || booking.userId !== session.sub) {
    return NextResponse.json({ error: "Booking not found." }, { status: 404 });
  }

  await prisma.transaction.create({
    data: { bookingId, amountUsd: booking.priceUsd, method: "pay_later", status: "PENDING" },
  });

  return NextResponse.json({ ok: true });
}
