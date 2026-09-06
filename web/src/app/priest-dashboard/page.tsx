import { redirect } from "next/navigation";
import { getSession } from "@/lib/session";
import LogoutButton from "@/components/LogoutButton";
import PriestDashboardWidgets from "@/components/PriestDashboardWidgets";

export default async function PriestDashboardPage() {
  const session = await getSession();
  if (!session || session.role !== "priest") redirect("/login");

  return (
    <div className="mx-auto max-w-3xl px-4 py-8">
      <div className="flex items-center justify-between rounded-3xl bg-cream-card p-6">
        <div>
          <h1 className="text-2xl font-bold text-saffron-dark">🧘 Pranam, {session.name}</h1>
          <p className="mt-1 text-sm text-muted">Your priest dashboard — orders, notifications, and requests.</p>
        </div>
        <LogoutButton />
      </div>

      <PriestDashboardWidgets />
    </div>
  );
}
