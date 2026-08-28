# AI Orchestration

StartupForge AI uses a graph-based multi-agent architecture built around LangGraph.

## Specialist Agents

The workflow separates startup planning into focused responsibilities:

1. Market Analysis
2. Product Planning
3. Technical Architecture
4. UI/UX Design
5. Business Strategy

A strategy/integration stage combines the outputs into a unified startup blueprint.

## Why Multi-Agent Orchestration?

Each agent has a focused responsibility instead of requiring one general-purpose prompt to perform the entire planning process. The workflow state allows intermediate results to be carried between stages.

## Research

The market-analysis workflow can use external web research to provide current market context. Research output is passed into the relevant agent rather than being treated as a static application document.

## Current Scope

The repository currently focuses on LangGraph orchestration and specialized agents. Advanced learning/reinforcement mechanisms are outside the current implementation scope.
