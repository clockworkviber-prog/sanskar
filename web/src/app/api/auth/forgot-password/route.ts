import { NextRequest, NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";
import { generateToken, RESET_TOKEN_TTL_MS } from "@/lib/tokens";
import { sendPasswordResetEmail } from "@/lib/email";

export async function POST(req: NextRequest) {
  const body = await req.json().catch(() => null);
  const role = body?.role as "devotee" | "priest" | undefined;
  const email = (body?.email as string | undefined)?.trim().toLowerCase();
  if (!role || !email) {
    return NextResponse.json({ error: "Missing role or email." }, { status: 400 });
  }

  // Always respond success — never reveal whether an account exists for this email.
  if (role === "devotee") {
    const user = await prisma.user.findUnique({ where: { email } });
    if (user) {
      const token = generateToken();
      await prisma.passwordResetToken.create({
        data: { token, role: "DEVOTEE", userId: user.id, expiresAt: new Date(Date.now() + RESET_TOKEN_TTL_MS) },
      });
      await sendPasswordResetEmail({ to: email, name: user.fullName, role: "devotee", token });
    }
  } else {
    const priest = await prisma.priest.findUnique({ where: { email } });
    if (priest) {
      const token = generateToken();
      await prisma.passwordResetToken.create({
        data: { token, role: "PRIEST", priestId: priest.id, expiresAt: new Date(Date.now() + RESET_TOKEN_TTL_MS) },
      });
      await sendPasswordResetEmail({ to: email, name: priest.name, role: "priest", token });
    }
  }

  return NextResponse.json({ ok: true });
}
