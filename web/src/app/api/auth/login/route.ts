import { NextRequest, NextResponse } from "next/server";
import { z } from "zod";
import { prisma } from "@/lib/prisma";
import { verifyPassword } from "@/lib/password";
import { createSession } from "@/lib/session";

const schema = z.object({
  role: z.enum(["devotee", "priest"]),
  email: z.string().email(),
  password: z.string().min(1),
});

export async function POST(req: NextRequest) {
  const parsed = schema.safeParse(await req.json().catch(() => null));
  if (!parsed.success) {
    return NextResponse.json({ error: "Please enter a valid email and password." }, { status: 400 });
  }
  const { role, email, password } = parsed.data;
  const key = email.trim().toLowerCase();

  if (role === "devotee") {
    const user = await prisma.user.findUnique({ where: { email: key } });
    if (!user || !(await verifyPassword(password, user.passwordHash))) {
      return NextResponse.json({ error: "Incorrect email or password." }, { status: 401 });
    }
    if (!user.emailVerified) {
      return NextResponse.json(
        { error: "Please verify your email before logging in.", code: "EMAIL_NOT_VERIFIED" },
        { status: 403 }
      );
    }
    await createSession({ sub: user.id, role: "devotee", email: user.email, name: user.fullName });
    return NextResponse.json({ ok: true });
  }

  const priest = await prisma.priest.findUnique({ where: { email: key } });
  if (!priest || !(await verifyPassword(password, priest.passwordHash))) {
    return NextResponse.json({ error: "Incorrect email or password." }, { status: 401 });
  }
  if (!priest.emailVerified) {
    return NextResponse.json(
      { error: "Please verify your email before logging in.", code: "EMAIL_NOT_VERIFIED" },
      { status: 403 }
    );
  }
  await createSession({ sub: priest.id, role: "priest", email: priest.email, name: priest.name });
  return NextResponse.json({ ok: true });
}
