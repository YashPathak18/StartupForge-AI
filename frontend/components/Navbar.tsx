"use client";

import Link from "next/link";
import { useAuth } from "@/lib/auth-context";
import { Sparkles, Layers, LogOut, PlusCircle, Compass } from "lucide-react";
import { useRouter } from "next/navigation";

export default function Navbar() {
  const { user, logout } = useAuth();
  const router = useRouter();

  const handleLogout = () => {
    logout();
    router.push("/login");
  };

  return (
    <header className="sticky top-0 z-50 w-full glass-panel border-b border-zinc-800/80">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        <Link href="/" className="flex items-center gap-2.5 font-bold text-xl tracking-tight">
          <div className="p-2 rounded-xl bg-gradient-to-tr from-indigo-600 via-indigo-500 to-purple-500 text-white shadow-lg shadow-indigo-500/20">
            <Sparkles className="w-5 h-5 animate-pulse" />
          </div>
          <span className="bg-gradient-to-r from-white via-zinc-200 to-indigo-300 bg-clip-text text-transparent">
            StartupForge <span className="text-indigo-400 font-extrabold">AI</span>
          </span>
        </Link>

        <nav className="flex items-center gap-4">
          {user ? (
            <>
              <Link
                href="/dashboard"
                className="flex items-center gap-1.5 px-3.5 py-1.5 rounded-lg text-sm text-zinc-300 hover:text-white hover:bg-zinc-800/60 transition"
              >
                <Layers className="w-4 h-4 text-indigo-400" />
                Dashboard
              </Link>
              <Link
                href="/projects/new"
                className="flex items-center gap-1.5 px-3.5 py-1.5 rounded-lg text-sm font-medium bg-indigo-600 text-white hover:bg-indigo-500 transition shadow-md shadow-indigo-600/20"
              >
                <PlusCircle className="w-4 h-4" />
                Forge Startup
              </Link>
              <div className="h-4 w-px bg-zinc-800" />
              <div className="flex items-center gap-3">
                <span className="text-xs text-zinc-400 font-mono hidden md:inline-block">
                  {user.email}
                </span>
                <button
                  onClick={handleLogout}
                  className="p-1.5 text-zinc-400 hover:text-rose-400 hover:bg-zinc-800/60 rounded-lg transition"
                  title="Logout"
                >
                  <LogOut className="w-4 h-4" />
                </button>
              </div>
            </>
          ) : (
            <div className="flex items-center gap-3">
              <Link
                href="/login"
                className="text-sm text-zinc-300 hover:text-white px-3 py-1.5 rounded-lg hover:bg-zinc-800/50 transition"
              >
                Sign In
              </Link>
              <Link
                href="/register"
                className="text-sm font-medium bg-indigo-600 text-white hover:bg-indigo-500 px-4 py-1.5 rounded-lg shadow-md shadow-indigo-600/20 transition"
              >
                Get Started
              </Link>
            </div>
          )}
        </nav>
      </div>
    </header>
  );
}