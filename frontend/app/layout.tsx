import type { Metadata } from "next";
import "./globals.css";
import { AuthProvider } from "@/lib/auth-context";
import Navbar from "@/components/Navbar";

export const metadata: Metadata = {
  title: "StartupForge AI — Multi-Agent Startup Analysis & Blueprint Platform",
  description: "Transform a single startup idea into an exhaustive research-backed business, technical, and product blueprint using 6 coordinated AI agents.",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className="dark">
      <body className="antialiased min-h-screen flex flex-col bg-zinc-950 text-zinc-100">
        <AuthProvider>
          <Navbar />
          <main className="flex-1">
            {children}
          </main>
          <footer className="border-t border-zinc-900 py-6 text-center text-xs text-zinc-600">
            StartupForge AI © 2026 — 6-Agent LangGraph Multi-Agent Architecture
          </footer>
        </AuthProvider>
      </body>
    </html>
  );
}