import { redirect, notFound } from "next/navigation";
import { getSession } from "@/lib/session";
import { prisma } from "@/lib/prisma";
import AppHeader from "@/components/AppHeader";
import BookingForm from "@/components/BookingForm";

export const dynamic = "force-dynamic";

export default async function PujaDetailPage({ params }: { params: { id: string } }) {
  const session = await getSession();
  if (!session || session.role !== "devotee") redirect("/login");

  const puja = await prisma.pujaService.findUnique({ where: { id: params.id } });
  if (!puja) notFound();

  const priests = await prisma.priest.findMany({
    where: { emailVerified: true },
    orderBy: { rating: "desc" },
    select: { id: true, name: true, isOnline: true, rating: true, languages: true },
  });

  return (
    <div>
      <AppHeader name={session.name} />
      <div className="mx-auto max-w-2xl px-4 py-8">
        <div className="text-2xl">
          {puja.emoji} <span className="ml-1 text-lg font-bold">{puja.sanskritName}</span>
        </div>
        <h1 className="mt-1 text-xl font-bold">{puja.name}</h1>
        <p className="mt-2 text-sm text-ink">{puja.description}</p>
        <p className="mt-1 text-sm text-muted">Benefits: {puja.benefits}</p>
        <p className="mt-1 text-sm text-muted">Duration: ~{Math.round(puja.durationMinutes / 60)}h</p>

        <BookingForm puja={puja} priests={priests} sankalpDefault={session.name} />
      </div>
    </div>
  );
}
