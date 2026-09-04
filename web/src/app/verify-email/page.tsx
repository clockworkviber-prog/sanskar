"use client";

import Link from "next/link";
import { Suspense, useEffect, useState } from "react";
import { useSearchParams } from "next/navigation";
import AuthShell from "@/components/AuthShell";

function VerifyEmailInner() {
  const params = useSearchParams();
  const token = params.get("token") ?? "";
  const [status, setStatus] = useState<"pending" | "ok" | "error">("pending");
  const [message, setMessage] = useState("Confirming your email…");

  useEffect(() => {
    if (!token) {
      setStatus("error");
      setMessage("This verification link is missing its token.");
      return;
    }
    fetch("/api/auth/verify-email", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ token }),
    })
      .then(async (res) => {
        const data = await res.json();
        if (!res.ok) {
          setStatus("error");
          setMessage(data.error ?? "This link is invalid or has expired.");
          return;
        }
        setStatus("ok");
        setMessage("Your email is confirmed! You can now log in.");
      })
      .catch(() => {
        setStatus("error");
        setMessage("Could not reach the server. Please try again.");
      });
  }, [token]);

  return (
    <AuthShell title={status === "ok" ? "Email confirmed 🎉" : "Email verification"}>
      <div className="space-y-4 text-center text-sm">
        <p className="text-4xl">{status === "ok" ? "✅" : status === "error" ? "⚠️" : "⏳"}</p>
        <p>{message}</p>
        <Link href="/login" className="btn-link">
          Go to login
        </Link>
      </div>
    </AuthShell>
  );
}

export default function VerifyEmailPage() {
  return (
    <Suspense>
      <VerifyEmailInner />
    </Suspense>
  );
}
