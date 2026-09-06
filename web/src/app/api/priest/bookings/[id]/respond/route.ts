import { NextRequest, NextResponse } from "next/server";
import { getSession } from "@/lib/session";
import { prisma } from "@/lib/prisma";

/**
 * A priest accepts or declines a booking that was specifically requested
 * for them. Declining releases it back to the unassigned queue for another
 * priest to claim, rather than leaving the devotee stuck.
 */
export async function POST(req: NextRequest, { params }: { params: { id: string } }) {
  const session = await getSession();
  if (!session || session.role !== "priest") {
    return NextResponse.json({ error: "Please log in as a priest." }, { status: 401 });
  }

  const body = await req.json().catch(() => null);
  const action = body?.action as "accept" | "decline" | undefined;
  if (action !== "accept" && action !== "decline") {
    return NextResponse.json({ error: "Invalid action." }, { status: 400 });
  }

  const booking = await prisma.booking.findUnique({ where: { id: params.id } });
  if (!booking || booking.priestId !== session.sub) {
    return NextResponse.json({ error: "Booking not found." }, { status: 404 });
  }
  if (booking.priestResponse !== "PENDING") {
    return NextResponse.json({ error: "This booking has already been responded to." }, { status: 409 });
  }

  if (action === "accept") {
    await prisma.booking.update({
      where: { id: booking.id },
      data: { priestResponse: "ACCEPTED" },
    });
  } else {
    // Release back to the unassigned queue for another priest.
    await prisma.booking.update({
      where: { id: booking.id },
      data: { priestId: null, priestName: "To be assigned", priestResponse: "PENDING" },
    });
  }

  return NextResponse.json({ ok: true });
}
