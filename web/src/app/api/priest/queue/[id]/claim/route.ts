import { NextRequest, NextResponse } from "next/server";
import { getSession } from "@/lib/session";
import { prisma } from "@/lib/prisma";

/** A priest claims an unassigned booking from the open queue. */
export async function POST(req: NextRequest, { params }: { params: { id: string } }) {
  const session = await getSession();
  if (!session || session.role !== "priest") {
    return NextResponse.json({ error: "Please log in as a priest." }, { status: 401 });
  }

  const priest = await prisma.priest.findUnique({ where: { id: session.sub } });
  if (!priest) return NextResponse.json({ error: "Priest not found." }, { status: 404 });

  // Guard against two priests claiming the same booking at once.
  const result = await prisma.booking.updateMany({
    where: { id: params.id, priestId: null, status: "UPCOMING" },
    data: { priestId: priest.id, priestName: priest.name, priestResponse: "ACCEPTED" },
  });

  if (result.count === 0) {
    return NextResponse.json({ error: "This booking is no longer available — someone else may have claimed it." }, { status: 409 });
  }

  return NextResponse.json({ ok: true });
}
