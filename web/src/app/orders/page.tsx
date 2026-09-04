import { redirect } from "next/navigation";
import Link from "next/link";
import { getSession } from "@/lib/session";
import { prisma } from "@/lib/prisma";
import AppHeader from "@/components/AppHeader";

export const dynamic = "force-dynamic";

const STATUS_STYLE: Record<string, string> = {
  UPCOMING: "bg-blue-50 text-blue-700",
  COMPLETED: "bg-green-50 text-green-700",
  CANCELLED: "bg-red-50 text-red-700",
};

export default async function OrdersPage() {
  const session = await getSession();
  if (!session || session.role !== "devotee") redirect("/login");

  const bookings = await prisma.booking.findMany({
    where: { userId: session.sub },
    orderBy: { createdAt: "desc" },
    include: { transactions: { orderBy: { createdAt: "desc" }, take: 1 } },
  });

  return (
    <div>
      <AppHeader name={session.name} />
      <div className="mx-auto max-w-2xl px-4 py-8">
        <h1 className="mb-5 text-xl font-bold">Order History</h1>

        {bookings.length === 0 ? (
          <div className="rounded-2xl bg-white p-8 text-center shadow-sm">
            <div className="text-4xl">🪷</div>
            <p className="mt-2 font-semibold">No bookings yet</p>
            <p className="text-sm text-muted">Book your first puja from the catalog.</p>
            <Link href="/dashboard" className="btn-link mt-3 inline-block">
              Browse pujas
            </Link>
          </div>
        ) : (
          <div className="space-y-4">
            {bookings.map((b) => {
              const payStatus = b.transactions[0]?.status ?? "PENDING";
              return (
                <div key={b.id} className="rounded-2xl bg-white p-5 shadow-sm">
                  <div className="flex items-center justify-between">
                    <span className="font-semibold">{b.pujaName}</span>
                    <span className={`rounded-full px-3 py-1 text-xs font-bold ${STATUS_STYLE[b.status]}`}>
                      {b.status}
                    </span>
                  </div>
                  <div className="mt-1 text-xs text-muted">Order {b.id}</div>
                  <div className="mt-1 text-sm">
                    {b.date.toDateString()} • {b.timeSlot}
                  </div>
                  <div className="mt-1 text-sm text-muted">Priest: {b.priestName}</div>
                  <div className="mt-2 flex items-center justify-between text-sm">
                    <span>{b.mode === "ONLINE_LIVE" ? "🖥 Online live puja" : "🛕 Performed at mandir"}</span>
                    <b className="text-saffron">${b.priceUsd}</b>
                  </div>
                  <div className="mt-2 flex items-center justify-between">
                    <span
                      className={`text-xs font-semibold ${
                        payStatus === "PAID" ? "text-green-700" : payStatus === "FAILED" ? "text-red-700" : "text-amber-700"
                      }`}
                    >
                      Payment: {payStatus}
                    </span>
                    {payStatus !== "PAID" && b.status !== "CANCELLED" && (
                      <Link href={`/checkout/${b.id}`} className="btn-link">
                        Complete payment
                      </Link>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
