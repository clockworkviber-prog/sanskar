import { redirect, notFound } from "next/navigation";
import { getSession } from "@/lib/session";
import { prisma } from "@/lib/prisma";
import AppHeader from "@/components/AppHeader";
import CheckoutClient from "@/components/CheckoutClient";

export const dynamic = "force-dynamic";

export default async function CheckoutPage({ params }: { params: { bookingId: string } }) {
  const session = await getSession();
  if (!session || session.role !== "devotee") redirect("/login");

  const booking = await prisma.booking.findUnique({ where: { id: params.bookingId } });
  if (!booking || booking.userId !== session.sub) notFound();

  return (
    <div>
      <AppHeader name={session.name} />
      <div className="mx-auto max-w-md px-4 py-8">
        <div className="rounded-2xl bg-white p-5 shadow-sm">
          <div className="flex items-center gap-2 text-sm font-semibold">🔒 Secure Checkout</div>
          <div className="mt-3 rounded-xl bg-cream-card p-4 text-sm">
            <div className="flex justify-between">
              <span>{booking.pujaName}</span>
              <b>${booking.priceUsd}</b>
            </div>
            <div className="mt-1 text-xs text-muted">
              {booking.date.toDateString()} • {booking.timeSlot} • Order {booking.id}
            </div>
          </div>
          <CheckoutClient bookingId={booking.id} amountUsd={booking.priceUsd} />
        </div>
      </div>
    </div>
  );
}
