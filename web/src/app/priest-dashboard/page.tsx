import { redirect } from "next/navigation";
import { getSession } from "@/lib/session";
import LogoutButton from "@/components/LogoutButton";

export default async function PriestDashboardPage() {
  const session = await getSession();
  if (!session || session.role !== "priest") redirect("/login");

  return (
    <div className="mx-auto max-w-2xl px-4 py-10">
      <div className="rounded-3xl bg-cream-card p-8">
        <h1 className="text-2xl font-bold text-saffron-dark">🧘 Pranam, {session.name}</h1>
        <p className="mt-2 text-sm text-muted">
          Your Sanskar priest account is signed in and email-verified.
        </p>
      </div>
      <div className="mt-6 rounded-2xl bg-white p-6 shadow-sm">
        <p className="text-sm text-muted">
          This is a starting priest dashboard — bookings, portfolio uploads, and the rest can be built out from
          here, backed by the same Neon database.
        </p>
        <div className="mt-4">
          <LogoutButton />
        </div>
      </div>
    </div>
  );
}
