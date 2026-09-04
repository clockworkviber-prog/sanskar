import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Sanskar — Your Mandir, Wherever You Are",
  description:
    "Book Hindu puja rituals online or performed on your behalf at partner mandirs in India.",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body className="min-h-screen bg-cream font-sans text-ink antialiased">{children}</body>
    </html>
  );
}
