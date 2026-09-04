import { NextRequest, NextResponse } from "next/server";
import { z } from "zod";
import { getSession } from "@/lib/session";
import { prisma } from "@/lib/prisma";

const schema = z.object({
  pujaId: z.string().min(1),
  mode: z.enum(["ONLINE_LIVE", "IN_TEMPLE"]),
  date: z.string().min(1), // yyyy-mm-dd from a <input type="date">
  timeSlot: z.string().min(1),
  priestId: z.string().optional(),
  sankalpName: z.string().min(1, "Please enter a name for the sankalp."),
});

/** Creates a booking. The price always comes from the server-side puja record. */
export async function POST(req: NextRequest) {
  const session = await getSession();
  if (!session || session.role !== "devotee") {
    return NextResponse.json({ error: "Please log in to book a puja." }, { status: 401 });
  }

  const parsed = schema.safeParse(await req.json().catch(() => null));
  if (!parsed.success) {
    return NextResponse.json({ error: parsed.error.issues[0]?.message ?? "Invalid booking." }, { status: 400 });
  }
  const d = parsed.data;

  const puja = await prisma.pujaService.findUnique({ where: { id: d.pujaId } });
  if (!puja) return NextResponse.json({ error: "This puja could not be found." }, { status: 404 });
  if (d.mode === "ONLINE_LIVE" && !puja.availableOnline) {
    return NextResponse.json({ error: "This puja is not available online." }, { status: 400 });
  }
  if (d.mode === "IN_TEMPLE" && !puja.inTempleAvailable) {
    return NextResponse.json({ error: "This puja is not available in-temple." }, { status: 400 });
  }

  const date = new Date(d.date);
  if (Number.isNaN(date.getTime()) || date < new Date(new Date().toDateString())) {
    return NextResponse.json({ error: "Please choose a valid, upcoming date." }, { status: 400 });
  }

  let priestName = "To be assigned";
  let priestId: string | undefined;
  if (d.priestId) {
    const priest = await prisma.priest.findUnique({ where: { id: d.priestId } });
    if (priest) {
      priestId = priest.id;
      priestName = priest.name;
    }
  }

  const priceUsd = d.mode === "ONLINE_LIVE" ? puja.priceOnlineUsd : puja.priceInTempleUsd;

  const booking = await prisma.booking.create({
    data: {
      userId: session.sub,
      priestId,
      pujaName: puja.name,
      priestName,
      mode: d.mode,
      date,
      timeSlot: d.timeSlot,
      priceUsd,
      sankalpName: d.sankalpName.trim(),
      status: "UPCOMING",
    },
  });

  return NextResponse.json({ id: booking.id });
}
