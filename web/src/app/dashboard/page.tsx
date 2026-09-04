import { redirect } from "next/navigation";
import Link from "next/link";
import { getSession } from "@/lib/session";
import { prisma } from "@/lib/prisma";
import AppHeader from "@/components/AppHeader";

export const dynamic = "force-dynamic";

export default async function DashboardPage() {
  const session = await getSession();
  if (!session || session.role !== "devotee") redirect("/login");

  const pujas = await prisma.pujaService.findMany({ orderBy: { name: "asc" } });

  return (
    <div>
      <AppHeader name={session.name} />
      <div className="mx-auto max-w-4xl px-4 py-8">
        <div className="rounded-3xl bg-gradient-to-r from-saffron to-maroon p-8 text-white">
          <h1 className="text-2xl font-bold">🙏 Namaste, {session.name.split(" ")[0]}</h1>
          <p className="mt-2 text-sm text-[#ffe8cc]">
            Book pujas with verified priests — live online or performed on your behalf at partner mandirs in
            India.
          </p>
        </div>

        <h2 className="mb-4 mt-8 text-lg font-bold">Puja Services</h2>
        {pujas.length === 0 ? (
          <p className="text-sm text-muted">No puja services are available yet — check back soon.</p>
        ) : (
          <div className="grid grid-cols-2 gap-4 sm:grid-cols-3">
            {pujas.map((p) => (
              <Link
                key={p.id}
                href={`/puja/${p.id}`}
                className="rounded-2xl bg-white p-4 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md"
              >
                <div className="text-3xl">{p.emoji}</div>
                <div className="mt-2 text-sm font-semibold leading-tight">{p.name}</div>
                <div className="text-xs text-muted">{p.sanskritName}</div>
                <div className="mt-2 text-sm font-bold text-saffron">From ${p.priceOnlineUsd}</div>
                <div className="text-xs text-muted">
                  {p.availableOnline && p.inTempleAvailable
                    ? "Online • In-temple"
                    : p.availableOnline
                      ? "Online only"
                      : "In-temple only"}
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
