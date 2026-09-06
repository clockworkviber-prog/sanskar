"use client";

import { useCallback, useEffect, useState } from "react";

type Booking = {
  id: string;
  pujaName: string;
  mode: "ONLINE_LIVE" | "IN_TEMPLE";
  date: string;
  timeSlot: string;
  priceUsd: number;
  sankalpName: string;
};

type NotificationItem = {
  id: string;
  type: string;
  message: string;
  isRead: boolean;
  createdAt: string;
};

type DashboardData = {
  assignedPending: Booking[];
  queue: Booking[];
  notifications: NotificationItem[];
  unreadCount: number;
  upcomingAccepted: Booking[];
  completed: number;
};

const POLL_MS = 20_000;

function fmtDate(iso: string) {
  return new Date(iso).toLocaleDateString(undefined, { day: "2-digit", month: "short", year: "numeric" });
}

export default function PriestDashboardWidgets() {
  const [data, setData] = useState<DashboardData | null>(null);
  const [busyId, setBusyId] = useState<string | null>(null);
  const [banner, setBanner] = useState<string | null>(null);

  const load = useCallback(async () => {
    const res = await fetch("/api/priest/dashboard-data");
    if (res.ok) setData(await res.json());
  }, []);

  useEffect(() => {
    load();
    // Simple polling stand-in for real push notifications — no email/SMS/
    // web-push provider is wired up yet, so "new booking" alerts only show
    // up here, refreshed every 20s while the dashboard tab is open.
    const interval = setInterval(load, POLL_MS);
    return () => clearInterval(interval);
  }, [load]);

  async function respond(bookingId: string, action: "accept" | "decline") {
    setBusyId(bookingId);
    const res = await fetch(`/api/priest/bookings/${bookingId}/respond`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ action }),
    });
    const result = await res.json();
    setBanner(res.ok ? (action === "accept" ? "Booking accepted." : "Booking declined and returned to the queue.") : result.error);
    setBusyId(null);
    load();
  }

  async function claim(bookingId: string) {
    setBusyId(bookingId);
    const res = await fetch(`/api/priest/queue/${bookingId}/claim`, { method: "POST" });
    const result = await res.json();
    setBanner(res.ok ? "Booking claimed — it's now yours." : result.error);
    setBusyId(null);
    load();
  }

  async function markAllRead() {
    await fetch("/api/priest/notifications/mark-read", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({}),
    });
    load();
  }

  if (!data) {
    return <p className="mt-6 text-center text-sm text-muted">Loading your dashboard…</p>;
  }

  return (
    <div className="mt-6 space-y-6">
      {banner && (
        <div className="rounded-lg bg-cream-card px-4 py-2 text-sm">
          {banner}
          <button className="btn-link ml-3" onClick={() => setBanner(null)}>
            dismiss
          </button>
        </div>
      )}

      <div className="grid grid-cols-3 gap-3">
        <StatBox label="Upcoming" value={data.upcomingAccepted.length} />
        <StatBox label="Awaiting your response" value={data.assignedPending.length} highlight={data.assignedPending.length > 0} />
        <StatBox label="Completed" value={data.completed} />
      </div>

      {/* Notifications */}
      <section className="rounded-2xl bg-white p-5 shadow-sm">
        <div className="flex items-center justify-between">
          <h2 className="font-bold">
            🔔 Notifications
            {data.unreadCount > 0 && (
              <span className="ml-2 rounded-full bg-saffron px-2 py-0.5 text-xs font-bold text-white">
                {data.unreadCount}
              </span>
            )}
          </h2>
          {data.unreadCount > 0 && (
            <button onClick={markAllRead} className="btn-link">
              Mark all read
            </button>
          )}
        </div>
        <p className="mt-1 text-xs text-muted">Refreshes automatically every 20 seconds.</p>
        {data.notifications.length === 0 ? (
          <p className="mt-3 text-sm text-muted">No notifications yet.</p>
        ) : (
          <ul className="mt-3 space-y-2">
            {data.notifications.map((n) => (
              <li
                key={n.id}
                className={`rounded-xl border p-3 text-sm ${n.isRead ? "border-[#eadbc9] text-muted" : "border-saffron bg-cream-card font-medium"}`}
              >
                {n.message}
                <div className="mt-1 text-xs text-muted">{new Date(n.createdAt).toLocaleString()}</div>
              </li>
            ))}
          </ul>
        )}
      </section>

      {/* Requests specifically for this priest */}
      <section className="rounded-2xl bg-white p-5 shadow-sm">
        <h2 className="font-bold">📥 Orders in Queue — requested for you</h2>
        {data.assignedPending.length === 0 ? (
          <p className="mt-3 text-sm text-muted">No pending requests right now.</p>
        ) : (
          <ul className="mt-3 space-y-3">
            {data.assignedPending.map((b) => (
              <li key={b.id} className="rounded-xl border border-[#eadbc9] p-4">
                <BookingSummary booking={b} />
                <div className="mt-3 flex gap-2">
                  <button
                    disabled={busyId === b.id}
                    onClick={() => respond(b.id, "accept")}
                    className="rounded-full bg-green-600 px-4 py-1.5 text-sm font-semibold text-white hover:bg-green-700 disabled:opacity-60"
                  >
                    Accept
                  </button>
                  <button
                    disabled={busyId === b.id}
                    onClick={() => respond(b.id, "decline")}
                    className="rounded-full border border-red-300 px-4 py-1.5 text-sm font-semibold text-red-700 hover:bg-red-50 disabled:opacity-60"
                  >
                    Decline
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </section>

      {/* Open queue — unassigned bookings anyone can claim */}
      <section className="rounded-2xl bg-white p-5 shadow-sm">
        <h2 className="font-bold">🗂 Open Queue — unassigned bookings</h2>
        <p className="mt-1 text-xs text-muted">First to claim gets it.</p>
        {data.queue.length === 0 ? (
          <p className="mt-3 text-sm text-muted">Nothing waiting to be claimed right now.</p>
        ) : (
          <ul className="mt-3 space-y-3">
            {data.queue.map((b) => (
              <li key={b.id} className="rounded-xl border border-[#eadbc9] p-4">
                <BookingSummary booking={b} />
                <button
                  disabled={busyId === b.id}
                  onClick={() => claim(b.id)}
                  className="mt-3 rounded-full bg-saffron px-4 py-1.5 text-sm font-semibold text-white hover:bg-saffron-dark disabled:opacity-60"
                >
                  Claim this booking
                </button>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  );
}

function StatBox({ label, value, highlight }: { label: string; value: number; highlight?: boolean }) {
  return (
    <div className={`rounded-2xl p-4 text-center ${highlight ? "bg-saffron text-white" : "bg-cream-card"}`}>
      <div className="text-xl font-bold">{value}</div>
      <div className="text-xs">{label}</div>
    </div>
  );
}

function BookingSummary({ booking }: { booking: Booking }) {
  return (
    <div>
      <div className="flex items-center justify-between">
        <span className="font-semibold">{booking.pujaName}</span>
        <b className="text-saffron">${booking.priceUsd}</b>
      </div>
      <div className="text-sm text-muted">
        {fmtDate(booking.date)} • {booking.timeSlot} • {booking.mode === "ONLINE_LIVE" ? "🖥 Online" : "🛕 In-temple"}
      </div>
      <div className="text-xs text-muted">Sankalp: {booking.sankalpName || "—"}</div>
    </div>
  );
}
