"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import AuthShell from "@/components/AuthShell";

type Role = "devotee" | "priest";

export default function LoginPage() {
  const router = useRouter();
  const [role, setRole] = useState<Role>("devotee");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [needsVerify, setNeedsVerify] = useState(false);
  const [resendSent, setResendSent] = useState(false);
  const [loading, setLoading] = useState(false);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setNeedsVerify(false);
    setResendSent(false);
    setLoading(true);
    try {
      const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ role, email, password }),
      });
      const data = await res.json();
      if (!res.ok) {
        setError(data.error ?? "Something went wrong. Please try again.");
        if (data.code === "EMAIL_NOT_VERIFIED") setNeedsVerify(true);
        return;
      }
      router.push(role === "priest" ? "/priest-dashboard" : "/dashboard");
      router.refresh();
    } catch {
      setError("Could not reach the server. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  async function resendVerification() {
    setResendSent(false);
    await fetch("/api/auth/resend-verification", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ role, email }),
    });
    setResendSent(true);
  }

  return (
    <AuthShell title="Login">
      <div className="mb-5 flex justify-center gap-2">
        <button
          type="button"
          onClick={() => setRole("devotee")}
          className={`chip ${role === "devotee" ? "chip-selected" : ""}`}
        >
          🙏 I am a Devotee
        </button>
        <button
          type="button"
          onClick={() => setRole("priest")}
          className={`chip ${role === "priest" ? "chip-selected" : ""}`}
        >
          🧘 I am a Priest
        </button>
      </div>

      <form onSubmit={onSubmit} className="space-y-3">
        <input
          type="email"
          required
          placeholder="Email"
          className="field"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <input
          type="password"
          required
          placeholder="Password"
          className="field"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <div className="flex justify-end">
          <Link href={`/forgot-password?role=${role}`} className="btn-link">
            Forgot password?
          </Link>
        </div>

        {error && (
          <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">
            {error}
            {needsVerify && (
              <div className="mt-2">
                {resendSent ? (
                  <span className="text-green-700">Verification email sent — check your inbox.</span>
                ) : (
                  <button type="button" onClick={resendVerification} className="btn-link">
                    Resend verification email
                  </button>
                )}
              </div>
            )}
          </div>
        )}

        <button type="submit" disabled={loading} className="btn-primary">
          {loading ? "Logging in…" : role === "priest" ? "Login as Priest" : "Login"}
        </button>
      </form>

      <div className="mt-4 text-center">
        <Link href={role === "priest" ? "/priest-signup" : "/signup"} className="btn-link">
          {role === "priest" ? "New priest? Register to offer seva" : "New to Sanskar? Create an account"}
        </Link>
      </div>
    </AuthShell>
  );
}
