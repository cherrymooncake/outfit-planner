import pytest
from fastapi.testclient import TestClient
from unittest.mock import patch
import torch
from main import app

client = TestClient(app)

@pytest.mark.parametrize("outfits, expected_status", [
    ([], 400),
    ([{"id": "1", "text_description": "a"}], 200),
    ([{"id": "1", "text_description": "a"}, {"id": "2", "text_description": "b"}], 200)
])
def test_recommend_validation(outfits, expected_status):
    response = client.post("/recommend", json={"prompt": "test", "outfits": outfits})
    assert response.status_code == expected_status

@pytest.mark.parametrize("scores, expected_id, expected_substr", [
    ([[0.9, 0.1, 0.0]], "o1", "лучше всего соответствует"),
    ([[0.1, 0.8, 0.2]], "o2", "лучше всего соответствует"),
    ([[0.0, 0.0, 0.99]], "o3", "лучше всего соответствует"),
    ([[0.1, 0.15, 0.05]], "o2", "наиболее подходящим"),
    ([[0.0, 0.0, 0.0]], "o1", "наиболее подходящим"),
    ([[0.19, 0.19, 0.19]], "o1", "наиболее подходящим"),
    ([[0.21, 0.1, 0.1]], "o1", "лучше всего соответствует")
])
@patch('main.util.cos_sim')
@patch('main.nlp_model.encode')
def test_recommend_logic(mock_encode, mock_cos_sim, scores, expected_id, expected_substr):
    mock_encode.return_value = torch.tensor([0.0])
    mock_cos_sim.return_value = torch.tensor(scores)
    req = {
        "prompt": "test",
        "weather_context": "test",
        "outfits": [
            {"id": "o1", "text_description": "a"},
            {"id": "o2", "text_description": "b"},
            {"id": "o3", "text_description": "c"}
        ]
    }
    resp = client.post("/recommend", json=req)
    assert resp.status_code == 200
    data = resp.json()
    assert data["recommended_outfit_id"] == expected_id
    assert expected_substr in data["explanation"]