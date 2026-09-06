import { NextRequest, NextResponse } from "next/server";
import { getSession } from "@/lib/session";
import { prisma } from "@/lib/prisma";

/** Marks one notification (or all, if no id given) as read for this priest. */
export async function POST(req: NextRequest) {
  const session = await getSession();
  if (!session || session.role !== "priest") {
    return NextResponse.json({ error: "Please log in as a priest." }, { status: 401 });
  }

  const body = await req.json().catch(() => ({}));
  const id = body?.id as string | undefined;

  await prisma.notification.updateMany({
    where: id ? { id, priestId: session.sub } : { priestId: session.sub, isRead: false },
    data: { isRead: true },
  });

  return NextResponse.json({ ok: true });
}
