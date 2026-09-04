"use client";

import Link from "next/link";
import { Suspense, useState } from "react";
import { useSearchParams } from "next/navigation";
import AuthShell from "@/components/AuthShell";

function ForgotPasswordForm() {
  const params = useSearchParams();
  const initialRole = params.get("role") === "priest" ? "priest" : "devotee";
  const [role, setRole] = useState<"devotee" | "priest">(initialRole);
  const [email, setEmail] = useState("");
  const [sent, setSent] = useState(false);
  const [loading, setLoading] = useState(false);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    try {
      await fetch("/api/auth/forgot-password", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ role, email }),
      });
      // Always show the same confirmation, whether or not the account exists.
      setSent(true);
    } finally {
      setLoading(false);
    }
  }

  if (sent) {
    return (
      <AuthShell title="Check your email">
        <div className="space-y-3 text-center text-sm">
          <p className="text-4xl">📩</p>
          <p>
            If an account exists for <b>{email}</b>, we&apos;ve sent a password reset link. It expires in 1 hour.
          </p>
          <Link href="/login" className="btn-link">
            Back to login
          </Link>
        </div>
      </AuthShell>
    );
  }

  return (
    <AuthShell title="Forgot Password" subtitle="We'll email you a link to reset it">
      <div className="mb-5 flex justify-center gap-2">
        <button type="button" onClick={() => setRole("devotee")} className={`chip ${role === "devotee" ? "chip-selected" : ""}`}>
          🙏 Devotee
        </button>
        <button type="button" onClick={() => setRole("priest")} className={`chip ${role === "priest" ? "chip-selected" : ""}`}>
          🧘 Priest
        </button>
      </div>
      <form onSubmit={onSubmit} className="space-y-3">
        <input required type="email" placeholder="Email" className="field" value={email} onChange={(e) => setEmail(e.target.value)} />
        <button type="submit" disabled={loading} className="btn-primary">
          {loading ? "Sending…" : "Send reset link"}
        </button>
      </form>
      <div className="mt-4 text-center">
        <Link href="/login" className="btn-link">
          Back to login
        </Link>
      </div>
    </AuthShell>
  );
}

export default function ForgotPasswordPage() {
  return (
    <Suspense>
      <ForgotPasswordForm />
    </Suspense>
  );
}
