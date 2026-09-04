import { NextRequest, NextResponse } from "next/server";
import { z } from "zod";
import { prisma } from "@/lib/prisma";
import { hashPassword } from "@/lib/password";
import { generateToken, VERIFICATION_TOKEN_TTL_MS } from "@/lib/tokens";
import { sendVerificationEmail } from "@/lib/email";

const schema = z.object({
  name: z.string().min(1, "Please enter your full name."),
  title: z.string().min(1, "Please enter your title (e.g. Vedacharya)."),
  templeAffiliation: z.string().min(1, "Please enter your temple/parampara affiliation."),
  specializations: z.string().min(1, "Please list at least one puja you specialize in."),
  languages: z.string().optional().default(""),
  experienceYears: z.string().optional().default("0"),
  email: z.string().email("Please enter a valid email address."),
  password: z.string().min(6, "Password must be at least 6 characters."),
});

export async function POST(req: NextRequest) {
  const parsed = schema.safeParse(await req.json().catch(() => null));
  if (!parsed.success) {
    return NextResponse.json({ error: parsed.error.issues[0]?.message ?? "Invalid input." }, { status: 400 });
  }
  const d = parsed.data;
  const email = d.email.trim().toLowerCase();

  const existing = await prisma.priest.findUnique({ where: { email } });
  if (existing) {
    return NextResponse.json({ error: "A priest account with this email already exists." }, { status: 409 });
  }

  const passwordHash = await hashPassword(d.password);
  const priest = await prisma.priest.create({
    data: {
      email,
      passwordHash,
      name: d.name.trim(),
      title: d.title.trim(),
      templeAffiliation: d.templeAffiliation.trim(),
      specializations: d.specializations.split(",").map((s) => s.trim()).filter(Boolean),
      languages: d.languages.split(",").map((s) => s.trim()).filter(Boolean).length
        ? d.languages.split(",").map((s) => s.trim()).filter(Boolean)
        : ["Hindi"],
      experienceYears: Number.parseInt(d.experienceYears, 10) || 0,
    },
  });

  const token = generateToken();
  await prisma.verificationToken.create({
    data: {
      token,
      role: "PRIEST",
      priestId: priest.id,
      expiresAt: new Date(Date.now() + VERIFICATION_TOKEN_TTL_MS),
    },
  });
  await sendVerificationEmail({ to: email, name: priest.name, role: "priest", token });

  return NextResponse.json({ ok: true });
}
