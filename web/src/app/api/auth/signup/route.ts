import { NextRequest, NextResponse } from "next/server";
import { z } from "zod";
import { prisma } from "@/lib/prisma";
import { hashPassword } from "@/lib/password";
import { generateToken, VERIFICATION_TOKEN_TTL_MS } from "@/lib/tokens";
import { sendVerificationEmail } from "@/lib/email";

const schema = z.object({
  fullName: z.string().min(1, "Please enter your full name."),
  email: z.string().email("Please enter a valid email address."),
  phone: z.string().optional().default(""),
  password: z.string().min(6, "Password must be at least 6 characters."),
});

export async function POST(req: NextRequest) {
  const parsed = schema.safeParse(await req.json().catch(() => null));
  if (!parsed.success) {
    return NextResponse.json({ error: parsed.error.issues[0]?.message ?? "Invalid input." }, { status: 400 });
  }
  const { fullName, phone, password } = parsed.data;
  const email = parsed.data.email.trim().toLowerCase();

  const existing = await prisma.user.findUnique({ where: { email } });
  if (existing) {
    return NextResponse.json({ error: "An account with this email already exists." }, { status: 409 });
  }

  const passwordHash = await hashPassword(password);
  const user = await prisma.user.create({
    data: { email, passwordHash, fullName: fullName.trim(), phone: phone.trim() },
  });

  const token = generateToken();
  await prisma.verificationToken.create({
    data: {
      token,
      role: "DEVOTEE",
      userId: user.id,
      expiresAt: new Date(Date.now() + VERIFICATION_TOKEN_TTL_MS),
    },
  });
  await sendVerificationEmail({ to: email, name: user.fullName, role: "devotee", token });

  return NextResponse.json({ ok: true });
}
