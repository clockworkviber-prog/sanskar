import { NextRequest, NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";
import { hashPassword } from "@/lib/password";

export async function POST(req: NextRequest) {
  const body = await req.json().catch(() => null);
  const token = body?.token as string | undefined;
  const password = body?.password as string | undefined;
  if (!token || !password || password.length < 6) {
    return NextResponse.json({ error: "Password must be at least 6 characters." }, { status: 400 });
  }

  const record = await prisma.passwordResetToken.findUnique({ where: { token } });
  if (!record || record.usedAt) {
    return NextResponse.json({ error: "This reset link is invalid or has already been used." }, { status: 400 });
  }
  if (record.expiresAt < new Date()) {
    return NextResponse.json({ error: "This reset link has expired. Please request a new one." }, { status: 400 });
  }

  const passwordHash = await hashPassword(password);

  if (record.role === "DEVOTEE" && record.userId) {
    await prisma.user.update({ where: { id: record.userId }, data: { passwordHash } });
  } else if (record.role === "PRIEST" && record.priestId) {
    await prisma.priest.update({ where: { id: record.priestId }, data: { passwordHash } });
  } else {
    return NextResponse.json({ error: "This reset link is invalid." }, { status: 400 });
  }

  await prisma.passwordResetToken.update({ where: { id: record.id }, data: { usedAt: new Date() } });
  return NextResponse.json({ ok: true, role: record.role === "PRIEST" ? "priest" : "devotee" });
}
