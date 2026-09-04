"use client";

import Link from "next/link";
import { useState } from "react";
import AuthShell from "@/components/AuthShell";

export default function SignupPage() {
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [done, setDone] = useState(false);
  const [loading, setLoading] = useState(false);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    if (password !== confirm) {
      setError("Passwords do not match.");
      return;
    }
    setLoading(true);
    try {
      const res = await fetch("/api/auth/signup", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ fullName, email, phone, password }),
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
            We&apos;ve sent a confirmation link to <b>{email}</b>. Click it to activate your account, then come
            back and log in.
          </p>
          <Link href="/login" className="btn-link">
            Back to login
          </Link>
        </div>
      </AuthShell>
    );
  }

  return (
    <AuthShell title="Create Account" subtitle="Book pujas with verified priests from anywhere">
      <form onSubmit={onSubmit} className="space-y-3">
        <input required placeholder="Full Name" className="field" value={fullName} onChange={(e) => setFullName(e.target.value)} />
        <input required type="email" placeholder="Email" className="field" value={email} onChange={(e) => setEmail(e.target.value)} />
        <input placeholder="Phone (with country code)" className="field" value={phone} onChange={(e) => setPhone(e.target.value)} />
        <input required type="password" placeholder="Password (min 6 characters)" className="field" value={password} onChange={(e) => setPassword(e.target.value)} />
        <input required type="password" placeholder="Confirm Password" className="field" value={confirm} onChange={(e) => setConfirm(e.target.value)} />

        {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}

        <button type="submit" disabled={loading} className="btn-primary">
          {loading ? "Creating account…" : "Sign Up"}
        </button>
      </form>

      <div className="mt-4 text-center">
        <Link href="/login" className="btn-link">
          Already have an account? Login
        </Link>
      </div>
    </AuthShell>
  );
}
