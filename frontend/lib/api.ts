import { Project, GenerationResponse, User } from "./types";

const GATEWAY_URL = process.env.NEXT_PUBLIC_GATEWAY_URL || "http://localhost:8080";

function getAuthToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem("startupforge_token");
}

async function request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const token = getAuthToken();
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string> || {}),
  };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const response = await fetch(`${GATEWAY_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (response.status === 204) {
    return {} as T;
  }

  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    const errorMsg = data.message || data.error || `Request failed with status ${response.status}`;
    throw new Error(errorMsg);
  }

  return data as T;
}

export const api = {
  // Auth
  async register(email: string, password: string): Promise<User> {
    return request<User>("/auth/register", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    });
  },

  async login(email: string, password: string): Promise<User> {
    return request<User>("/auth/login", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    });
  },

  // Projects
  async getProjects(): Promise<Project[]> {
    return request<Project[]>("/projects");
  },

  async getProject(projectId: string): Promise<Project> {
    return request<Project>(`/projects/${projectId}`);
  },

  async createProject(name: string, startupIdea: string): Promise<Project> {
    return request<Project>("/projects", {
      method: "POST",
      body: JSON.stringify({ name, startupIdea }),
    });
  },

  async updateProject(projectId: string, name: string, startupIdea: string): Promise<Project> {
    return request<Project>(`/projects/${projectId}`, {
      method: "PUT",
      body: JSON.stringify({ name, startupIdea }),
    });
  },

  async deleteProject(projectId: string): Promise<void> {
    return request<void>(`/projects/${projectId}`, {
      method: "DELETE",
    });
  },

  // Generations
  async triggerGeneration(projectId: string): Promise<GenerationResponse> {
    return request<GenerationResponse>(`/projects/${projectId}/generations`, {
      method: "POST",
    });
  },

  async getLatestGeneration(projectId: string): Promise<GenerationResponse> {
    return request<GenerationResponse>(`/projects/${projectId}/generations/latest`);
  },

  async getGenerations(projectId: string): Promise<GenerationResponse[]> {
    return request<GenerationResponse[]>(`/projects/${projectId}/generations`);
  },

  async getGeneration(projectId: string, generationId: string): Promise<GenerationResponse> {
    return request<GenerationResponse>(`/projects/${projectId}/generations/${generationId}`);
  }
};