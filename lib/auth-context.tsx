"use client";

import React, { createContext, useContext, useState, useEffect } from "react";
import { User } from "./types";
import { api } from "./api";

interface AuthContextType {
  user: User | null;
  token: string | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const savedToken = localStorage.getItem("startupforge_token");
    const savedUser = localStorage.getItem("startupforge_user");
    if (savedToken && savedUser) {
      setToken(savedToken);
      try {
        setUser(JSON.parse(savedUser));
      } catch (e) {
        console.error("Failed to parse saved user", e);
      }
    }
    setLoading(false);
  }, []);

  const login = async (email: string, password: string) => {
    const userRes = await api.login(email, password);
    setUser(userRes);
    setToken(userRes.accessToken);
    localStorage.setItem("startupforge_token", userRes.accessToken);
    localStorage.setItem("startupforge_user", JSON.stringify(userRes));
  };

  const register = async (email: string, password: string) => {
    const userRes = await api.register(email, password);
    setUser(userRes);
    setToken(userRes.accessToken);
    localStorage.setItem("startupforge_token", userRes.accessToken);
    localStorage.setItem("startupforge_user", JSON.stringify(userRes));
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem("startupforge_token");
    localStorage.removeItem("startupforge_user");
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}