"use client";

import Link from "next/link";
import { useState } from "react";
import AuthShell from "@/components/AuthShell";

export default function PriestSignupPage() {
  const [name, setName] = useState("");
  const [title, setTitle] = useState("");
  const [templeAffiliation, setTemple] = useState("");
  const [specializations, setSpecializations] = useState("");
  const [languages, setLanguages] = useState("");
  const [experienceYears, setExperience] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [done, setDone] = useState(false);
  const [loading, setLoading] = useState(false);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const res = await fetch("/api/auth/priest-signup", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, title, templeAffiliation, specializations, languages, experienceYears, email, password }),
      });
      const data = await res.json();
      if (!res.ok) {
        setError(data.error ?? "Something went wrong. Please try again.");
        return;
      }
      setDone(true);
    } catch {
      setError("Could not reach the server. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  if (done) {
    return (
      <AuthShell title="Check your email">
        <div className="space-y-3 text-center text-sm">
          <p className="text-4xl">📩</p>
          <p>
            We&apos;ve sent a confirmation link to <b>{email}</b>. Click it to activate your priest account, then
            come back and log in.
          </p>
          <Link href="/login" className="btn-link">
            Back to login
          </Link>
        </div>
      </AuthShell>
    );
  }

  return (
    <AuthShell title="🧘 Priest Registration" subtitle="Offer your seva to devotees worldwide">
      <form onSubmit={onSubmit} className="space-y-3">
        <input required placeholder="Full Name (e.g. Pt. Ram Sharma)" className="field" value={name} onChange={(e) => setName(e.target.value)} />
        <input required placeholder="Title (e.g. Vedacharya)" className="field" value={title} onChange={(e) => setTitle(e.target.value)} />
        <input required placeholder="Temple / Parampara affiliation" className="field" value={templeAffiliation} onChange={(e) => setTemple(e.target.value)} />
        <input required placeholder="Specializations (comma separated)" className="field" value={specializations} onChange={(e) => setSpecializations(e.target.value)} />
        <input placeholder="Languages (comma separated)" className="field" value={languages} onChange={(e) => setLanguages(e.target.value)} />
        <input placeholder="Years of experience" className="field" value={experienceYears} onChange={(e) => setExperience(e.target.value)} />
        <input required type="email" placeholder="Email" className="field" value={email} onChange={(e) => setEmail(e.target.value)} />
        <input required type="password" placeholder="Password (min 6 characters)" className="field" value={password} onChange={(e) => setPassword(e.target.value)} />

        {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}

        <button type="submit" disabled={loading} className="btn-primary">
          {loading ? "Registering…" : "Register as Priest"}
        </button>
      </form>

      <div className="mt-4 text-center">
        <Link href="/login" className="btn-link">
          Already registered? Login
        </Link>
      </div>
    </AuthShell>
  );
}
