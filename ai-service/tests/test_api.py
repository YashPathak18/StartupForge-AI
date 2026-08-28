import pytest
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_health_check():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json() == {"status": "ok"}

def test_create_generation_contract():
    payload = {
        "project_id": "101",
        "prompt": "We are building a new social media app for pets."
    }
    # Assuming endpoint is mocked or returns accepted status
    response = client.post("/v1/generations", json=payload)
    
    # Check for either 200 or 202 depending on async processing design
    assert response.status_code in [200, 202]
    assert "generation_id" in response.json() or "status" in response.json()
