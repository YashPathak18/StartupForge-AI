"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/auth-context";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { api } from "@/lib/api";
import { Project } from "@/lib/types";
import {
  PlusCircle,
  Sparkles,
  Layers,
  Clock,
  CheckCircle2,
  AlertTriangle,
  ArrowUpRight,
  FolderOpen,
  Trash2
} from "lucide-react";

export default function DashboardPage() {
  const { user, loading: authLoading } = useAuth();
  const [projects, setProjects] = useState<Project[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();

  useEffect(() => {
    if (!authLoading && !user) {
      router.push("/login");
      return;
    }

    if (user) {
      loadProjects();
    }
  }, [user, authLoading]);

  const loadProjects = async () => {
    try {
      setLoading(true);
      const data = await api.getProjects();
      setProjects(data);
    } catch (err: any) {
      setError(err.message || "Failed to load projects.");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: string, e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (!confirm("Are you sure you want to delete this startup project?")) return;

    try {
      await api.deleteProject(id);
      setProjects((prev) => prev.filter((p) => p.id !== id));
    } catch (err: any) {
      alert("Failed to delete project: " + err.message);
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "READY":
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
            <CheckCircle2 className="w-3 h-3" /> Blueprint Ready
          </span>
        );
      case "GENERATING":
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-amber-500/10 text-amber-400 border border-amber-500/20 animate-pulse">
            <Clock className="w-3 h-3" /> Generating Agents
          </span>
        );
      case "FAILED":
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-rose-500/10 text-rose-400 border border-rose-500/20">
            <AlertTriangle className="w-3 h-3" /> Generation Failed
          </span>
        );
      default:
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-zinc-800 text-zinc-400 border border-zinc-700">
            Draft
          </span>
        );
    }
  };

  if (authLoading || (loading && projects.length === 0)) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-16 text-center">
        <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-500 mb-4" />
        <p className="text-sm text-zinc-400 font-mono">Loading StartupForge Workspace...</p>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
        <div>
          <h1 className="text-3xl font-bold text-white tracking-tight">Startup Workspace</h1>
          <p className="text-sm text-zinc-400 mt-1">
            Manage your startup ventures and multi-agent generated blueprints
          </p>
        </div>
        <Link
          href="/projects/new"
          className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl font-semibold text-sm bg-indigo-600 hover:bg-indigo-500 text-white shadow-lg shadow-indigo-600/20 transition"
        >
          <PlusCircle className="w-4 h-4" />
          Forge New Startup
        </Link>
      </div>

      {error && (
        <div className="mb-6 p-4 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-300 text-sm">
          {error}
        </div>
      )}

      {/* Projects Grid */}
      {projects.length === 0 ? (
        <div className="p-12 text-center rounded-2xl glass-panel border border-zinc-800">
          <div className="inline-flex p-4 rounded-2xl bg-indigo-600/10 text-indigo-400 border border-indigo-500/20 mb-4">
            <FolderOpen className="w-8 h-8" />
          </div>
          <h3 className="text-lg font-bold text-white">No Startup Projects Yet</h3>
          <p className="text-sm text-zinc-400 max-w-md mx-auto mt-2 mb-6">
            Enter your first startup idea to let our 6 specialized AI agents perform comprehensive market research, UX design, and technical architecture planning.
          </p>
          <Link
            href="/projects/new"
            className="inline-flex items-center gap-2 px-6 py-3 rounded-xl font-semibold text-sm bg-indigo-600 hover:bg-indigo-500 text-white shadow-lg transition"
          >
            <Sparkles className="w-4 h-4" />
            Forge Your First Startup
          </Link>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {projects.map((project) => (
            <Link
              key={project.id}
              href={`/projects/${project.id}`}
              className="p-6 rounded-2xl glass-card flex flex-col justify-between group cursor-pointer relative"
            >
              <div>
                <div className="flex items-center justify-between gap-2 mb-3">
                  {getStatusBadge(project.status)}
                  <button
                    onClick={(e) => handleDelete(project.id, e)}
                    className="text-zinc-600 hover:text-rose-400 transition p-1"
                    title="Delete project"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
                <h3 className="text-xl font-bold text-white group-hover:text-indigo-300 transition mb-2">
                  {project.name}
                </h3>
                <p className="text-xs text-zinc-400 line-clamp-3 leading-relaxed">
                  {project.startupIdea}
                </p>
              </div>

              <div className="mt-6 pt-4 border-t border-zinc-800/80 flex items-center justify-between text-xs text-zinc-500">
                <span>Updated {new Date(project.updatedAt).toLocaleDateString()}</span>
                <span className="flex items-center gap-1 text-indigo-400 group-hover:translate-x-0.5 transition font-semibold">
                  Open Project <ArrowUpRight className="w-3.5 h-3.5" />
                </span>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}