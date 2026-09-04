/**
 * DEV-ONLY seed: creates throwaway demo accounts for local testing.
 *
 * DO NOT run this against your production Neon database — it creates a
 * publicly-known login (demo@sanskar.app / demo123) that would let anyone
 * who has ever seen this codebase log into a real account on your live
 * app. Point DATABASE_URL at a local/dev/staging database before running
 * `npm run db:seed:dev`.
 */
import { PrismaClient } from "@prisma/client";
import bcrypt from "bcryptjs";

const prisma = new PrismaClient();

async function main() {
  if (process.env.VERCEL_ENV === "production") {
    throw new Error("Refusing to run seed-dev.ts against a production Vercel environment.");
  }

  const demoPasswordHash = await bcrypt.hash("demo123", 12);
  await prisma.user.upsert({
    where: { email: "demo@sanskar.app" },
    update: {},
    create: {
      email: "demo@sanskar.app",
      passwordHash: demoPasswordHash,
      fullName: "Demo Bhakt",
      phone: "+1 555 010 2244",
      city: "San Jose",
      country: "USA",
      gotra: "Kashyap",
      timezone: "America/Los_Angeles",
      emailVerified: true,
    },
  });

  const priestPasswordHash = await bcrypt.hash("priest123", 12);
  await prisma.priest.upsert({
    where: { email: "priest@sanskar.app" },
    update: {},
    create: {
      email: "priest@sanskar.app",
      passwordHash: priestPasswordHash,
      name: "Pt. Ramesh Shastri (dev test priest)",
      title: "Vedacharya",
      templeAffiliation: "Kashi Vishwanath Seva Samiti, Varanasi",
      specializations: ["Griha Pravesh", "Rudrabhishek", "Navagraha Shanti"],
      languages: ["Hindi", "English", "Sanskrit"],
      experienceYears: 24,
      rating: 4.9,
      pujasPerformed: 3200,
      isOnline: true,
      emailVerified: true,
    },
  });

  console.log("Dev seed complete: demo@sanskar.app / demo123 and priest@sanskar.app / priest123 (pre-verified).");
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
