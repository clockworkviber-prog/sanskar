"use client";

import { useRouter } from "next/navigation";
import { useMemo, useState } from "react";
import type { PujaService } from "@prisma/client";

type PriestOption = {
  id: string;
  name: string;
  isOnline: boolean;
  rating: number;
  languages: string[];
};

const TIME_SLOTS = ["06:00 AM", "08:00 AM", "10:30 AM", "05:00 PM", "07:30 PM"];

function tomorrowIso() {
  const d = new Date();
  d.setDate(d.getDate() + 1);
  return d.toISOString().slice(0, 10);
}

export default function BookingForm({
  puja,
  priests,
  sankalpDefault,
}: {
  puja: PujaService;
  priests: PriestOption[];
  sankalpDefault: string;
}) {
  const router = useRouter();
  const [mode, setMode] = useState<"ONLINE_LIVE" | "IN_TEMPLE">(
    puja.availableOnline ? "ONLINE_LIVE" : "IN_TEMPLE"
  );
  const [date, setDate] = useState(tomorrowIso());
  const [timeSlot, setTimeSlot] = useState(TIME_SLOTS[1]);
  const [priestId, setPriestId] = useState<string>("");
  const [sankalpName, setSankalpName] = useState(sankalpDefault);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const price = mode === "ONLINE_LIVE" ? puja.priceOnlineUsd : puja.priceInTempleUsd;
  const eligiblePriests = useMemo(
    () => (mode === "ONLINE_LIVE" ? priests.filter((p) => p.isOnline) : priests),
    [priests, mode]
  );

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const res = await fetch("/api/bookings", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          pujaId: puja.id,
          mode,
          date,
          timeSlot,
          priestId: priestId || undefined,
          sankalpName,
        }),
      });
      const data = await res.json();
      if (!res.ok) {
        setError(data.error ?? "Could not create the booking. Please try again.");
        return;
      }
      router.push(`/checkout/${data.id}`);
    } catch {
      setError("Could not reach the server. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <form onSubmit={onSubmit} className="mt-6 space-y-5 rounded-2xl bg-white p-5 shadow-sm">
      <div>
        <div className="mb-2 text-sm font-semibold">How would you like the puja?</div>
        <div className="flex gap-2">
          {puja.availableOnline && (
            <button
              type="button"
              onClick={() => { setMode("ONLINE_LIVE"); setPriestId(""); }}
              className={`chip ${mode === "ONLINE_LIVE" ? "chip-selected" : ""}`}
            >
              Online live — ${puja.priceOnlineUsd}
            </button>
          )}
          {puja.inTempleAvailable && (
            <button
              type="button"
              onClick={() => { setMode("IN_TEMPLE"); setPriestId(""); }}
              className={`chip ${mode === "IN_TEMPLE" ? "chip-selected" : ""}`}
            >
              At mandir for you — ${puja.priceInTempleUsd}
            </button>
          )}
        </div>
        <p className="mt-2 text-xs text-muted">
          {mode === "ONLINE_LIVE"
            ? "Join live over video call — the priest guides you step by step."
            : "Performed on your behalf at a partner mandir, with sankalp in your name. Prasad shipping included."}
        </p>
      </div>

      <div>
        <label className="mb-1 block text-sm font-semibold" htmlFor="date">
          Choose date
        </label>
        <input
          id="date"
          type="date"
          required
          min={tomorrowIso()}
          className="field"
          value={date}
          onChange={(e) => setDate(e.target.value)}
        />
      </div>

      <div>
        <div className="mb-2 text-sm font-semibold">Choose time slot</div>
        <div className="flex flex-wrap gap-2">
          {TIME_SLOTS.map((s) => (
            <button
              key={s}
              type="button"
              onClick={() => setTimeSlot(s)}
              className={`chip ${timeSlot === s ? "chip-selected" : ""}`}
            >
              {s}
            </button>
          ))}
        </div>
      </div>

      <div>
        <div className="mb-2 text-sm font-semibold">Choose priest (optional)</div>
        {eligiblePriests.length === 0 ? (
          <p className="text-xs text-muted">
            No priests are available for this mode yet — your booking will show &quot;To be assigned&quot; and
            we&apos;ll confirm one shortly.
          </p>
        ) : (
          <div className="space-y-2">
            <label className="flex items-center gap-2 rounded-xl border border-[#c9a88f] p-3 text-sm">
              <input type="radio" name="priest" checked={priestId === ""} onChange={() => setPriestId("")} />
              No preference — assign the best available priest
            </label>
            {eligiblePriests.map((p) => (
              <label
                key={p.id}
                className={`flex items-center gap-2 rounded-xl border p-3 text-sm ${priestId === p.id ? "border-saffron bg-cream-card" : "border-[#c9a88f]"}`}
              >
                <input type="radio" name="priest" checked={priestId === p.id} onChange={() => setPriestId(p.id)} />
                <span className="flex-1">
                  <b>{p.name}</b> <span className="text-muted">· {p.languages.join(", ")} · ★ {p.rating}</span>
                </span>
                {p.isOnline && <span className="text-xs font-semibold text-green-700">Online</span>}
              </label>
            ))}
          </div>
        )}
      </div>

      <div>
        <label className="mb-1 block text-sm font-semibold" htmlFor="sankalp">
          Name for sankalp
        </label>
        <input
          id="sankalp"
          required
          className="field"
          value={sankalpName}
          onChange={(e) => setSankalpName(e.target.value)}
        />
      </div>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}

      <button type="submit" disabled={loading} className="btn-primary">
        {loading ? "Booking…" : `Book for $${price} • ${date}, ${timeSlot}`}
      </button>
    </form>
  );
}
