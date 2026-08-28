"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { api } from "@/lib/api";
import { Project, GenerationResponse } from "@/lib/types";
import {
  Sparkles,
  ShieldCheck,
  Search,
  Cpu,
  Layers,
  Palette,
  TrendingUp,
  CheckCircle2,
  Clock,
  AlertCircle,
  ArrowRight,
  BrainCircuit
} from "lucide-react";

export default function GenerateProgressPage() {
  const params = useParams();
  const projectId = params.id as string;
  const router = useRouter();

  const [project, setProject] = useState<Project | null>(null);
  const [generation, setGeneration] = useState<GenerationResponse | null>(null);
  const [running, setRunning] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [elapsed, setElapsed] = useState(0);

  const agentList = [
    { key: "market_agent", name: "Market Research Agent", icon: Search, role: "Tavily + Firecrawl Intelligence" },
    { key: "product_agent", name: "Product Strategy Agent", icon: Layers, role: "PRD & User Stories" },
    { key: "technical_agent", name: "Technical Architecture Agent", icon: Cpu, role: "Tech Stack & Schemas" },
    { key: "ux_agent", name: "UI/UX Experience Agent", icon: Palette, role: "Screen & Flow Design" },
    { key: "business_agent", name: "Business Strategy Agent", icon: TrendingUp, role: "Revenue & Pricing" },
    { key: "strategy_agent", name: "Chief Strategy Agent", icon: ShieldCheck, role: "Contradiction Cross-Check" },
  ];

  useEffect(() => {
    init();
  }, [projectId]);

  useEffect(() => {
    let timer: any;
    if (running) {
      timer = setInterval(() => setElapsed((prev) => prev + 1), 1000);
    }
    return () => clearInterval(timer);
  }, [running]);

  const init = async () => {
    try {
      const proj = await api.getProject(projectId);
      setProject(proj);
      startGeneration();
    } catch (err: any) {
      setError(err.message || "Failed to start generation workflow.");
    }
  };

  const startGeneration = async () => {
    setRunning(true);
    setError(null);
    try {
      const res = await api.triggerGeneration(projectId);
      setGeneration(res);
      if (res.status === "COMPLETED") {
        setRunning(false);
      }
    } catch (err: any) {
      setError(err.message || "Multi-agent execution encountered an error.");
      setRunning(false);
    }
  };

  const isAgentDone = (agentKey: string) => {
    if (!generation || !generation.agentResults) return false;
    return generation.agentResults.some(
      (r) => r.agentName.toLowerCase().includes(agentKey.replace("_agent", "")) && r.status === "completed"
    );
  };

  return (
    <div className="max-w-4xl mx-auto px-4 py-12">
      <div className="text-center mb-10">
        <div className="inline-flex items-center gap-2 px-4 py-1 rounded-full bg-indigo-500/10 border border-indigo-500/20 text-indigo-400 text-xs font-semibold mb-3">
          <BrainCircuit className="w-4 h-4 animate-spin" />
          LangGraph Multi-Agent Orchestrator
        </div>
        <h1 className="text-3xl font-bold text-white">Forging Startup Blueprint</h1>
        <p className="text-sm text-zinc-400 mt-2 max-w-xl mx-auto">
          6 autonomous agents are concurrently searching the web, planning architecture, and cross-validating strategic alignment for <span className="text-indigo-300 font-semibold">{project?.name || "your startup"}</span>.
        </p>
        <div className="mt-3 text-xs font-mono text-zinc-500">
          Elapsed Time: {elapsed}s
        </div>
      </div>

      {error && (
        <div className="mb-8 p-4 rounded-xl bg-rose-500/10 border border-rose-500/30 flex items-center gap-3 text-rose-300 text-sm">
          <AlertCircle className="w-5 h-5 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* Agents Status Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
        {agentList.map((agent, i) => {
          const Icon = agent.icon;
          const completed = isAgentDone(agent.key) || (!running && generation?.status === "COMPLETED");

          return (
            <div
              key={i}
              className={`p-4 rounded-xl border transition flex items-center justify-between ${
                completed
                  ? "bg-emerald-950/20 border-emerald-800/40"
                  : running
                  ? "bg-zinc-900/80 border-indigo-500/30 animate-pulse"
                  : "bg-zinc-900/40 border-zinc-800"
              }`}
            >
              <div className="flex items-center gap-3">
                <div
                  className={`p-2.5 rounded-lg ${
                    completed ? "bg-emerald-500/20 text-emerald-400" : "bg-indigo-600/20 text-indigo-400"
                  }`}
                >
                  <Icon className="w-5 h-5" />
                </div>
                <div>
                  <h4 className="text-sm font-bold text-white">{agent.name}</h4>
                  <p className="text-xs text-zinc-400">{agent.role}</p>
                </div>
              </div>

              <div>
                {completed ? (
                  <span className="flex items-center gap-1 text-xs font-semibold text-emerald-400">
                    <CheckCircle2 className="w-4 h-4" /> Ready
                  </span>
                ) : running ? (
                  <span className="flex items-center gap-1 text-xs font-semibold text-indigo-400">
                    <Clock className="w-4 h-4 animate-spin" /> Analyzing
                  </span>
                ) : (
                  <span className="text-xs text-zinc-600">Pending</span>
                )}
              </div>
            </div>
          );
        })}
      </div>

      {/* Action Button */}
      {generation?.status === "COMPLETED" && (
        <div className="text-center">
          <button
            onClick={() => router.push(`/projects/${projectId}/blueprint`)}
            className="inline-flex items-center gap-2 px-8 py-3.5 rounded-xl font-bold text-base bg-emerald-600 hover:bg-emerald-500 text-white shadow-xl shadow-emerald-600/30 transition hover:scale-[1.02]"
          >
            Open Complete Startup Blueprint
            <ArrowRight className="w-5 h-5" />
          </button>
        </div>
      )}
    </div>
  );
}