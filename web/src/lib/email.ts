import { Resend } from "resend";

function appUrl(): string {
  if (process.env.NEXT_PUBLIC_APP_URL) return process.env.NEXT_PUBLIC_APP_URL;
  if (process.env.VERCEL_URL) return `https://${process.env.VERCEL_URL}`;
  return "http://localhost:3000";
}

const resend = process.env.RESEND_API_KEY ? new Resend(process.env.RESEND_API_KEY) : null;
const FROM = process.env.EMAIL_FROM || "Sanskar <onboarding@resend.dev>";

async function send(to: string, subject: string, html: string, textFallback: string) {
  if (!resend) {
    // No RESEND_API_KEY configured — log instead of failing, so local dev
    // and preview deploys without email credentials still work end-to-end.
    console.log(`\n[email:dev-mode] To: ${to}\nSubject: ${subject}\n${textFallback}\n`);
    return;
  }
  await resend.emails.send({ from: FROM, to, subject, html });
}

export async function sendVerificationEmail(params: {
  to: string;
  name: string;
  role: "devotee" | "priest";
  token: string;
}) {
  const link = `${appUrl()}/verify-email?token=${params.token}`;
  const subject = "🪔 Confirm your Sanskar account";
  const html = `
    <div style="font-family:sans-serif;max-width:480px;margin:0 auto;padding:24px">
      <h2 style="color:#B93E0A">🪔 Sanskar</h2>
      <p>Namaste ${escapeHtml(params.name)},</p>
      <p>Please confirm your email address to activate your ${params.role} account.</p>
      <p style="margin:28px 0">
        <a href="${link}" style="background:#B93E0A;color:#fff;padding:12px 24px;border-radius:24px;text-decoration:none;font-weight:600">
          Confirm my email
        </a>
      </p>
      <p style="color:#6E5A4B;font-size:13px">This link expires in 24 hours. If the button doesn't work, copy this link:<br>${link}</p>
      <p style="color:#6E5A4B;font-size:13px">If you didn't create a Sanskar account, you can safely ignore this email.</p>
    </div>`;
  await send(params.to, subject, html, `Confirm your Sanskar account: ${link}`);
}

export async function sendPasswordResetEmail(params: {
  to: string;
  name: string;
  role: "devotee" | "priest";
  token: string;
}) {
  const link = `${appUrl()}/reset-password?token=${params.token}&role=${params.role}`;
  const subject = "🔒 Reset your Sanskar password";
  const html = `
    <div style="font-family:sans-serif;max-width:480px;margin:0 auto;padding:24px">
      <h2 style="color:#B93E0A">🪔 Sanskar</h2>
      <p>Namaste ${escapeHtml(params.name)},</p>
      <p>We received a request to reset the password on your ${params.role} account.</p>
      <p style="margin:28px 0">
        <a href="${link}" style="background:#B93E0A;color:#fff;padding:12px 24px;border-radius:24px;text-decoration:none;font-weight:600">
          Reset my password
        </a>
      </p>
      <p style="color:#6E5A4B;font-size:13px">This link expires in 1 hour. If the button doesn't work, copy this link:<br>${link}</p>
      <p style="color:#6E5A4B;font-size:13px">If you didn't request this, you can safely ignore this email — your password won't change.</p>
    </div>`;
  await send(params.to, subject, html, `Reset your Sanskar password: ${link}`);
}

function escapeHtml(s: string): string {
  return s.replace(/[&<>"']/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c] as string));
}
