"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";
import { api } from "@/lib/api";
import { Project, GenerationResponse } from "@/lib/types";
import {
  Sparkles,
  Layers,
  ArrowRight,
  FileText,
  Clock,
  CheckCircle2,
  AlertTriangle,
  Play,
  RotateCcw
} from "lucide-react";

export default function ProjectDetailPage() {
  const params = useParams();
  const projectId = params.id as string;
  const router = useRouter();

  const [project, setProject] = useState<Project | null>(null);
  const [latestGen, setLatestGen] = useState<GenerationResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadData();
  }, [projectId]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [projData, genData] = await Promise.all([
        api.getProject(projectId),
        api.getLatestGeneration(projectId).catch(() => null),
      ]);
      setProject(projData);
      setLatestGen(genData);
    } catch (err: any) {
      setError(err.message || "Failed to load project details.");
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="max-w-5xl mx-auto px-4 py-16 text-center">
        <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-500 mb-4" />
        <p className="text-sm text-zinc-400 font-mono">Loading Project Details...</p>
      </div>
    );
  }

  if (!project) {
    return (
      <div className="max-w-md mx-auto px-4 py-16 text-center">
        <p className="text-rose-400">Project not found.</p>
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
        <div>
          <div className="flex items-center gap-3">
            <h1 className="text-3xl font-bold text-white tracking-tight">{project.name}</h1>
            <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-zinc-800 text-zinc-400 border border-zinc-700">
              {project.status}
            </span>
          </div>
          <p className="text-xs text-zinc-500 mt-1">
            Created on {new Date(project.createdAt).toLocaleDateString()}
          </p>
        </div>

        <div className="flex items-center gap-3">
          {latestGen && latestGen.status === "COMPLETED" ? (
            <Link
              href={`/projects/${project.id}/blueprint`}
              className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl font-semibold text-sm bg-emerald-600 hover:bg-emerald-500 text-white shadow-lg transition"
            >
              <FileText className="w-4 h-4" />
              View Unified Blueprint
            </Link>
          ) : null}

          <Link
            href={`/projects/${project.id}/generate`}
            className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl font-semibold text-sm bg-indigo-600 hover:bg-indigo-500 text-white shadow-lg shadow-indigo-600/20 transition"
          >
            {latestGen ? (
              <>
                <RotateCcw className="w-4 h-4" />
                Regenerate Blueprint
              </>
            ) : (
              <>
                <Play className="w-4 h-4" />
                Run Multi-Agent Engine
              </>
            )}
          </Link>
        </div>
      </div>

      {error && (
        <div className="mb-6 p-4 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-300 text-sm">
          {error}
        </div>
      )}

      {/* Startup Idea Card */}
      <div className="p-6 rounded-2xl glass-panel border border-zinc-800 mb-8">
        <h3 className="text-sm font-semibold text-zinc-300 uppercase tracking-wider mb-2 flex items-center gap-2">
          <Sparkles className="w-4 h-4 text-indigo-400" />
          Raw Startup Concept & Scope
        </h3>
        <p className="text-sm text-zinc-300 whitespace-pre-wrap leading-relaxed">
          {project.startupIdea}
        </p>
      </div>

      {/* Blueprint Status Card */}
      <div className="p-6 rounded-2xl glass-card">
        <h3 className="text-lg font-bold text-white mb-4">Multi-Agent Generation Status</h3>
        {latestGen ? (
          <div className="space-y-4">
            <div className="flex items-center justify-between p-4 rounded-xl bg-zinc-900/60 border border-zinc-800">
              <div className="flex items-center gap-3">
                {latestGen.status === "COMPLETED" ? (
                  <CheckCircle2 className="w-5 h-5 text-emerald-400" />
                ) : latestGen.status === "RUNNING" ? (
                  <Clock className="w-5 h-5 text-amber-400 animate-pulse" />
                ) : (
                  <AlertTriangle className="w-5 h-5 text-rose-400" />
                )}
                <div>
                  <p className="text-sm font-semibold text-white">
                    Generation Status: {latestGen.status}
                  </p>
                  <p className="text-xs text-zinc-500">
                    Started: {latestGen.startedAt ? new Date(latestGen.startedAt).toLocaleTimeString() : "N/A"}
                  </p>
                </div>
              </div>

              {latestGen.status === "COMPLETED" && (
                <Link
                  href={`/projects/${project.id}/blueprint`}
                  className="text-xs font-semibold text-indigo-400 hover:text-indigo-300 flex items-center gap-1"
                >
                  Explore Output <ArrowRight className="w-3.5 h-3.5" />
                </Link>
              )}
            </div>
          </div>
        ) : (
          <div className="text-center py-6 text-zinc-500 text-sm">
            No blueprint generation run yet. Click "Run Multi-Agent Engine" to trigger the 6 domain agents.
          </div>
        )}
      </div>
    </div>
  );
}