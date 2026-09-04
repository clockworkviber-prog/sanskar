export default function AuthShell({
  title,
  subtitle,
  children,
}: {
  title: string;
  subtitle?: string;
  children: React.ReactNode;
}) {
  return (
    <div className="flex min-h-screen items-center justify-center px-4 py-10">
      <div className="w-full max-w-md">
        <div className="mb-6 flex flex-col items-center text-center">
          <div className="flex h-20 w-20 items-center justify-center rounded-full bg-saffron text-4xl">🪔</div>
          <h1 className="mt-3 text-3xl font-extrabold text-saffron">Sanskar</h1>
          <p className="mt-1 text-sm text-muted">{subtitle ?? "Your Mandir, Wherever You Are"}</p>
        </div>
        <div className="rounded-3xl bg-white p-6 shadow-sm">
          <h2 className="mb-5 text-center text-lg font-bold">{title}</h2>
          {children}
        </div>
      </div>
    </div>
  );
}
