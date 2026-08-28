"""Smoke tests for the StartupForge AI workflow."""

import pytest

from app.workflow.conditions import specialists_ready, strategy_ready, workflow_failed
from app.workflow.graph import workflow


def test_workflow_compiles():
    """The graph should compile successfully from the application imports."""
    assert workflow is not None


def test_specialists_ready_only_after_all_five_complete():
    state = {
        "agent_results": {
            "market": {"status": "completed"},
            "product": {"status": "completed"},
            "technical": {"status": "completed"},
            "ux": {"status": "completed"},
            "business": {"status": "completed"},
        }
    }

    assert specialists_ready(state) is True


def test_specialists_not_ready_when_one_agent_failed():
    state = {
        "agent_results": {
            "market": {"status": "completed"},
            "product": {"status": "completed"},
            "technical": {"status": "failed"},
            "ux": {"status": "completed"},
            "business": {"status": "completed"},
        }
    }

    assert specialists_ready(state) is False
    assert workflow_failed(state) is True


def test_strategy_ready_after_success():
    state = {
        "agent_results": {
            "strategy": {"status": "completed"},
        }
    }

    assert strategy_ready(state) is True
