"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { api } from "@/lib/api";
import { Sparkles, ArrowRight, Lightbulb, Compass, Rocket } from "lucide-react";

export default function NewProjectPage() {
  const [name, setName] = useState("");
  const [idea, setIdea] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();

  const templates = [
    {
      title: "AI B2B SaaS",
      text: "An AI-powered customer support triage tool for Shopify e-commerce brands that automates 70% of tier-1 support tickets and integrates with Zendesk.",
    },
    {
      title: "Fintech Marketplace",
      text: "A micro-lending and invoice factoring marketplace connecting small freelance agencies with private debt funds with instant risk assessment.",
    },
    {
      title: "Healthtech Platform",
      text: "A remote patient monitoring platform connecting cardiology clinics with smartwatch telemetry for early arrhythmia detection.",
    },
  ];

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (idea.trim().length < 20) {
      setError("Please describe the startup idea in at least 20 characters.");
      return;
    }

    setLoading(true);

    try {
      const project = await api.createProject(name, idea);
      router.push(`/projects/${project.id}`);
    } catch (err: any) {
      setError(err.message || "Failed to create project.");
      setLoading(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto px-4 py-12">
      <div className="glass-panel p-8 rounded-2xl border border-zinc-800 shadow-2xl">
        <div className="flex items-center gap-3 mb-6">
          <div className="p-2.5 rounded-xl bg-indigo-600/20 text-indigo-400 border border-indigo-500/30">
            <Sparkles className="w-6 h-6" />
          </div>
          <div>
            <h1 className="text-2xl font-bold text-white">Forge a New Startup</h1>
            <p className="text-xs text-zinc-400">
              Provide your raw startup concept to activate the 6-agent LangGraph workflow
            </p>
          </div>
        </div>

        {error && (
          <div className="mb-6 p-3.5 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-300 text-xs">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-xs font-semibold text-zinc-300 uppercase tracking-wider mb-2">
              Project / Venture Name
            </label>
            <input
              type="text"
              required
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="e.g. DocuSync AI, OmniFlow, CarePulse"
              className="w-full px-4 py-3 bg-zinc-900/90 border border-zinc-800 rounded-xl text-sm text-white placeholder-zinc-500 focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 transition"
            />
          </div>

          <div>
            <div className="flex items-center justify-between mb-2">
              <label className="block text-xs font-semibold text-zinc-300 uppercase tracking-wider">
                Startup Idea & Target Problem
              </label>
              <span className="text-xs text-zinc-500 font-mono">
                {idea.length} / 10,000 chars
              </span>
            </div>
            <textarea
              required
              rows={6}
              value={idea}
              onChange={(e) => setIdea(e.target.value)}
              placeholder="Describe your startup concept, who the target customers are, the core problem being solved, potential business model, or technical constraints..."
              className="w-full p-4 bg-zinc-900/90 border border-zinc-800 rounded-xl text-sm text-white placeholder-zinc-500 focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 transition resize-none"
            />
          </div>

          {/* Quick Idea Templates */}
          <div>
            <label className="block text-xs font-semibold text-zinc-400 mb-2 flex items-center gap-1.5">
              <Lightbulb className="w-3.5 h-3.5 text-amber-400" />
              Or try a sample template:
            </label>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-2">
              {templates.map((tpl, i) => (
                <button
                  key={i}
                  type="button"
                  onClick={() => {
                    setName(tpl.title);
                    setIdea(tpl.text);
                  }}
                  className="p-2.5 rounded-lg bg-zinc-900/50 hover:bg-zinc-800/80 border border-zinc-800 text-left transition text-xs text-zinc-300 hover:border-zinc-700"
                >
                  <p className="font-semibold text-indigo-300 mb-1">{tpl.title}</p>
                  <p className="text-[11px] text-zinc-500 line-clamp-2">{tpl.text}</p>
                </button>
              ))}
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 font-semibold text-sm text-white shadow-lg shadow-indigo-600/30 transition flex items-center justify-center gap-2 disabled:opacity-50"
          >
            {loading ? "Creating Project..." : "Proceed to Project Workspace"}
            <ArrowRight className="w-4 h-4" />
          </button>
        </form>
      </div>
    </div>
  );
}