import Link from "next/link";
import {
  Sparkles,
  ArrowRight,
  ShieldCheck,
  Search,
  Cpu,
  Layers,
  Palette,
  TrendingUp,
  GitMerge,
  Target,
  FileCheck2,
  BrainCircuit
} from "lucide-react";

export default function HomePage() {
  const agents = [
    {
      name: "Chief Strategy Agent",
      role: "Strategic Orchestrator & Contradiction Checker",
      desc: "Cross-checks findings across all domains to detect mismatches (e.g. pricing vs customer tier, MVP scope vs over-engineering).",
      icon: ShieldCheck,
      color: "from-amber-500 to-orange-600",
    },
    {
      name: "Market Research Agent",
      role: "Competitor & Market Intelligence",
      desc: "Autonomously searches the web via Tavily & Firecrawl to ground market sizing, SWOT, and competitor pricing in real-time data.",
      icon: Search,
      color: "from-blue-500 to-cyan-600",
    },
    {
      name: "Product Strategy Agent",
      role: "PRD & User Story Architecture",
      desc: "Transforms customer pain points into concise problem definitions, MVP scope, prioritized feature lists, and roadmaps.",
      icon: Layers,
      color: "from-emerald-500 to-teal-600",
    },
    {
      name: "Technical Architecture Agent",
      role: "System & Database Designer",
      desc: "Formulates full-stack technology choices, PostgreSQL schemas, API contracts, deployment plans, and security safeguards.",
      icon: Cpu,
      color: "from-purple-500 to-indigo-600",
    },
    {
      name: "UI/UX Experience Agent",
      role: "Design System & Wireframe Specs",
      desc: "Outlines end-to-end user journeys, wireframe screen specs, navigation sitemaps, accessibility, and empty/error states.",
      icon: Palette,
      color: "from-pink-500 to-rose-600",
    },
    {
      name: "Business Strategy & GTM Agent",
      role: "Revenue, Pricing & Growth Engine",
      desc: "Models multi-tier SaaS pricing, acquisition funnels, unit economics, GTM marketing channels, and strategic KPI scorecards.",
      icon: TrendingUp,
      color: "from-violet-500 to-purple-600",
    },
  ];

  return (
    <div className="relative overflow-hidden">
      {/* Hero Section */}
      <section className="relative px-4 pt-20 pb-28 sm:px-6 lg:px-8 max-w-7xl mx-auto text-center">
        <div className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full border border-indigo-500/30 bg-indigo-500/10 text-indigo-300 text-xs font-semibold uppercase tracking-wider mb-8">
          <BrainCircuit className="w-4 h-4 text-indigo-400" />
          Autonomous Multi-Agent Startup Engine
        </div>

        <h1 className="text-4xl sm:text-6xl font-extrabold tracking-tight text-white max-w-4xl mx-auto leading-tight sm:leading-none">
          One Startup Idea.{" "}
          <span className="bg-gradient-to-r from-indigo-400 via-purple-300 to-pink-400 bg-clip-text text-transparent">
            6 Specialized AI Agents.
          </span>{" "}
          One Unified Blueprint.
        </h1>

        <p className="mt-6 text-lg sm:text-xl text-zinc-400 max-w-3xl mx-auto leading-relaxed">
          StartupForge AI replaces weeks of disjointed founder legwork. Autonomous web research,
          specialized domain analysis, cross-agent contradiction detection, and comprehensive technical blueprints.
        </p>

        <div className="mt-10 flex flex-col sm:flex-row items-center justify-center gap-4">
          <Link
            href="/register"
            className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-8 py-3.5 rounded-xl font-semibold text-base bg-indigo-600 hover:bg-indigo-500 text-white shadow-xl shadow-indigo-600/30 transition hover:scale-[1.02]"
          >
            Start Forging Now
            <ArrowRight className="w-5 h-5" />
          </Link>
          <Link
            href="/login"
            className="w-full sm:w-auto inline-flex items-center justify-center px-8 py-3.5 rounded-xl font-semibold text-base text-zinc-300 bg-zinc-900/80 hover:bg-zinc-800 border border-zinc-800 transition"
          >
            Explore Dashboard
          </Link>
        </div>

        {/* Workflow Diagram Preview */}
        <div className="mt-16 p-6 rounded-2xl glass-panel border border-zinc-800/80 max-w-5xl mx-auto text-left shadow-2xl">
          <div className="flex items-center justify-between border-b border-zinc-800 pb-4 mb-6">
            <div className="flex items-center gap-2">
              <div className="w-3 h-3 rounded-full bg-rose-500" />
              <div className="w-3 h-3 rounded-full bg-amber-500" />
              <div className="w-3 h-3 rounded-full bg-emerald-500" />
              <span className="text-xs text-zinc-400 font-mono ml-2">langgraph_state_machine.py</span>
            </div>
            <span className="text-xs text-indigo-400 font-medium px-2.5 py-1 rounded bg-indigo-500/10 border border-indigo-500/20">
              Parallel Fan-Out Execution
            </span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-6 gap-3">
            <div className="col-span-1 md:col-span-1 p-3 rounded-xl bg-zinc-900/80 border border-zinc-800 text-center flex flex-col justify-center">
              <span className="text-xs font-mono text-zinc-500">INPUT</span>
              <p className="text-xs font-bold text-zinc-200 mt-1">Founder's Raw Idea</p>
            </div>
            <div className="col-span-1 md:col-span-1 p-3 rounded-xl bg-blue-950/40 border border-blue-800/40 text-center flex flex-col justify-center">
              <span className="text-xs font-mono text-blue-400">RESEARCH</span>
              <p className="text-xs font-bold text-blue-200 mt-1">Tavily + Firecrawl</p>
            </div>
            <div className="col-span-1 md:col-span-2 p-3 rounded-xl bg-purple-950/40 border border-purple-800/40 text-center flex flex-col justify-center">
              <span className="text-xs font-mono text-purple-400">PARALLEL DOMAIN AGENTS</span>
              <p className="text-xs font-bold text-purple-200 mt-1">Product • Tech • UX • Business</p>
            </div>
            <div className="col-span-1 md:col-span-1 p-3 rounded-xl bg-amber-950/40 border border-amber-800/40 text-center flex flex-col justify-center">
              <span className="text-xs font-mono text-amber-400">CROSS-CHECK</span>
              <p className="text-xs font-bold text-amber-200 mt-1">Chief Strategy Agent</p>
            </div>
            <div className="col-span-1 md:col-span-1 p-3 rounded-xl bg-emerald-950/40 border border-emerald-800/40 text-center flex flex-col justify-center">
              <span className="text-xs font-mono text-emerald-400">SYNTHESIS</span>
              <p className="text-xs font-bold text-emerald-200 mt-1">Unified Blueprint</p>
            </div>
          </div>
        </div>
      </section>

      {/* 6 Specialized Agents Breakdown */}
      <section className="px-4 py-20 bg-zinc-900/40 border-t border-zinc-900">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-3xl font-bold text-white tracking-tight">
              The 6 Specialized Multi-Agent Team
            </h2>
            <p className="text-zinc-400 mt-3 max-w-2xl mx-auto">
              Not a basic ChatGPT prompt wrapper. Each agent operates with specialized domain system prompts,
              strict Pydantic schemas, and structured evidence retrieval.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {agents.map((agent, i) => {
              const Icon = agent.icon;
              return (
                <div
                  key={i}
                  className="p-6 rounded-2xl glass-card relative overflow-hidden group hover:border-indigo-500/40 transition"
                >
                  <div className={`w-12 h-12 rounded-xl bg-gradient-to-tr ${agent.color} flex items-center justify-center text-white mb-4 shadow-lg`}>
                    <Icon className="w-6 h-6" />
                  </div>
                  <h3 className="text-lg font-bold text-white group-hover:text-indigo-300 transition">
                    {agent.name}
                  </h3>
                  <p className="text-xs font-semibold text-indigo-400 mt-0.5 mb-3">
                    {agent.role}
                  </p>
                  <p className="text-sm text-zinc-400 leading-relaxed">
                    {agent.desc}
                  </p>
                </div>
              );
            })}
          </div>
        </div>
      </section>
    </div>
  );
}