import Link from "next/link";
import LogoutButton from "@/components/LogoutButton";

export default function AppHeader({ name }: { name: string }) {
  return (
    <header className="border-b border-[#eadbc9] bg-white">
      <div className="mx-auto flex max-w-4xl items-center justify-between px-4 py-3">
        <Link href="/dashboard" className="flex items-center gap-2 font-extrabold text-saffron">
          <span className="flex h-9 w-9 items-center justify-center rounded-full bg-saffron text-lg text-white">
            🪔
          </span>
          Sanskar
        </Link>
        <nav className="flex items-center gap-4 text-sm font-medium">
          <Link href="/dashboard" className="hover:text-saffron">
            Pujas
          </Link>
          <Link href="/orders" className="hover:text-saffron">
            My Orders
          </Link>
          <span className="hidden text-muted sm:inline">Namaste, {name.split(" ")[0]}</span>
          <LogoutButton />
        </nav>
      </div>
    </header>
  );
}
