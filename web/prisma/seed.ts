/**
 * Production-safe seed: real catalog content only (puja services).
 * No fake accounts, no fake bookings — safe to run against your live
 * Neon database. Run once after `db:push`, and again any time you add
 * a new puja to this list.
 *
 * For local/dev testing with a throwaway demo account, use
 * `npm run db:seed:dev` instead (see prisma/seed-dev.ts).
 */
import { PrismaClient } from "@prisma/client";

const prisma = new PrismaClient();

const pujaServices = [
  { id: "griha_pravesh", name: "Griha Pravesh", sanskritName: "गृह प्रवेश", emoji: "🏠", description: "House-warming ceremony performed before moving into a new home.", benefits: "Removes Vastu dosha, invites peace and prosperity.", durationMinutes: 120, priceOnlineUsd: 79, priceInTempleUsd: 149 },
  { id: "satyanarayan", name: "Satyanarayan Katha", sanskritName: "सत्यनारायण कथा", emoji: "🙏", description: "Puja and katha dedicated to Lord Vishnu.", benefits: "Fulfilment of wishes, family harmony.", durationMinutes: 150, priceOnlineUsd: 69, priceInTempleUsd: 129 },
  { id: "ganesh", name: "Ganesh Puja", sanskritName: "गणेश पूजा", emoji: "🐘", description: "Invocation of Lord Ganesha before new beginnings.", benefits: "Removes obstacles, blesses new beginnings.", durationMinutes: 60, priceOnlineUsd: 39, priceInTempleUsd: 79 },
  { id: "lakshmi", name: "Lakshmi Puja", sanskritName: "लक्ष्मी पूजा", emoji: "🪔", description: "Worship of Goddess Lakshmi for wealth and abundance.", durationMinutes: 90, benefits: "Attracts wealth and financial stability.", priceOnlineUsd: 49, priceInTempleUsd: 99 },
  { id: "rudrabhishek", name: "Rudrabhishek", sanskritName: "रुद्राभिषेक", emoji: "🔱", description: "Sacred abhishek of Lord Shiva.", benefits: "Health, protection, spiritual upliftment.", durationMinutes: 120, priceOnlineUsd: 89, priceInTempleUsd: 169 },
  { id: "navagraha", name: "Navagraha Shanti", sanskritName: "नवग्रह शांति", emoji: "🪐", description: "Pacification of the nine planetary deities.", benefits: "Reduces planetary doshas.", durationMinutes: 150, priceOnlineUsd: 99, priceInTempleUsd: 189 },
  { id: "mundan", name: "Mundan Sanskar", sanskritName: "मुंडन संस्कार", emoji: "👶", description: "First hair-shaving ceremony of a child.", benefits: "Child's wellbeing and purification.", durationMinutes: 90, priceOnlineUsd: 59, priceInTempleUsd: 119 },
  { id: "annaprashan", name: "Annaprashan", sanskritName: "अन्नप्राशन", emoji: "🍚", description: "First rice-feeding ceremony of an infant.", benefits: "Blesses the child with health and strength.", durationMinutes: 60, priceOnlineUsd: 49, priceInTempleUsd: 99 },
  { id: "pitru", name: "Pitru Paksha Shraddh", sanskritName: "पितृ पक्ष श्राद्ध", emoji: "🪷", description: "Tarpan and shraddh rituals honouring ancestors.", benefits: "Peace for departed souls.", durationMinutes: 120, priceOnlineUsd: 79, priceInTempleUsd: 139 },
  { id: "vivah", name: "Vivah Sanskar", sanskritName: "विवाह संस्कार", emoji: "💞", description: "Complete Vedic wedding ceremony.", benefits: "A sacred, complete Vedic wedding.", durationMinutes: 240, priceOnlineUsd: 299, priceInTempleUsd: 499, availableOnline: false },
  { id: "consult", name: "Online Consultation", sanskritName: "ऑनलाइन परामर्श", emoji: "🗣️", description: "One-on-one 30-min video consultation with an acharya.", benefits: "Personalised guidance for your questions.", durationMinutes: 30, priceOnlineUsd: 20, priceInTempleUsd: 20, inTempleAvailable: false },
  { id: "sundarkand", name: "Sundarkand Path", sanskritName: "सुंदरकांड पाठ", emoji: "🏹", description: "Recitation of the Sundarkand from Ramcharitmanas.", benefits: "Courage, removal of fear.", durationMinutes: 180, priceOnlineUsd: 59, priceInTempleUsd: 109 },
  { id: "vahan", name: "Vahan Puja", sanskritName: "वाहन पूजा", emoji: "🚗", description: "Blessing ceremony for a newly purchased vehicle.", benefits: "Safe travels and protection.", durationMinutes: 45, priceOnlineUsd: 29, priceInTempleUsd: 59 },
];

async function main() {
  for (const p of pujaServices) {
    await prisma.pujaService.upsert({ where: { id: p.id }, update: p, create: p });
  }
  console.log(`Seed complete: ${pujaServices.length} puja services. No accounts were created.`);
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
