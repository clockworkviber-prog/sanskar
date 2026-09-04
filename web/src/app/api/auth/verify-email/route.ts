import { NextRequest, NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";

export async function POST(req: NextRequest) {
  const body = await req.json().catch(() => null);
  const token = body?.token as string | undefined;
  if (!token) return NextResponse.json({ error: "Missing verification token." }, { status: 400 });

  const record = await prisma.verificationToken.findUnique({ where: { token } });
  if (!record) {
    return NextResponse.json({ error: "This verification link is invalid or has already been used." }, { status: 400 });
  }
  if (record.expiresAt < new Date()) {
    await prisma.verificationToken.delete({ where: { id: record.id } });
    return NextResponse.json(
      { error: "This verification link has expired. Please request a new one from the login page." },
      { status: 400 }
    );
  }

  if (record.role === "DEVOTEE" && record.userId) {
    await prisma.user.update({ where: { id: record.userId }, data: { emailVerified: true } });
  } else if (record.role === "PRIEST" && record.priestId) {
    await prisma.priest.update({ where: { id: record.priestId }, data: { emailVerified: true } });
  } else {
    return NextResponse.json({ error: "This verification link is invalid." }, { status: 400 });
  }

  await prisma.verificationToken.delete({ where: { id: record.id } });
  return NextResponse.json({ ok: true, role: record.role === "PRIEST" ? "priest" : "devotee" });
}
