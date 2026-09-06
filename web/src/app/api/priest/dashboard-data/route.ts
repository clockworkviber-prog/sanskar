import { NextResponse } from "next/server";
import { getSession } from "@/lib/session";
import { prisma } from "@/lib/prisma";

export async function GET() {
  const session = await getSession();
  if (!session || session.role !== "priest") {
    return NextResponse.json({ error: "Please log in as a priest." }, { status: 401 });
  }

  const [assignedPending, queue, notifications, unreadCount, upcomingAccepted, completed] = await Promise.all([
    // Bookings specifically requested for this priest, awaiting their response.
    prisma.booking.findMany({
      where: { priestId: session.sub, priestResponse: "PENDING", status: "UPCOMING" },
      orderBy: { date: "asc" },
    }),
    // Unassigned bookings any priest could claim.
    prisma.booking.findMany({
      where: { priestId: null, status: "UPCOMING" },
      orderBy: { date: "asc" },
      take: 20,
    }),
    prisma.notification.findMany({
      where: { priestId: session.sub },
      orderBy: { createdAt: "desc" },
      take: 20,
    }),
    prisma.notification.count({ where: { priestId: session.sub, isRead: false } }),
    prisma.booking.findMany({
      where: { priestId: session.sub, priestResponse: "ACCEPTED", status: "UPCOMING" },
      orderBy: { date: "asc" },
    }),
    prisma.booking.count({ where: { priestId: session.sub, status: "COMPLETED" } }),
  ]);

  return NextResponse.json({ assignedPending, queue, notifications, unreadCount, upcomingAccepted, completed });
}
