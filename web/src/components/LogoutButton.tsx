"use client";

import { useRouter } from "next/navigation";

export default function LogoutButton() {
  const router = useRouter();
  return (
    <button
      onClick={async () => {
        await fetch("/api/auth/logout", { method: "POST" });
        router.push("/login");
        router.refresh();
      }}
      className="rounded-full border border-[#c9a88f] px-5 py-2 text-sm font-medium hover:bg-cream"
    >
      Logout
    </button>
  );
}
