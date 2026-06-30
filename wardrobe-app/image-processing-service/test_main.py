import pytest
from fastapi.testclient import TestClient
from unittest.mock import patch
from main import app

client = TestClient(app)

@pytest.mark.parametrize("content_type, expected_status", [
    ("application/json", 400),
    ("text/html", 400),
    ("video/mp4", 400),
    ("image/jpeg", 200),
    ("image/png", 200),
    ("image/webp", 200),
])
@patch('main.remove')
@patch('main.new_session')
@patch('main.post_process_smart_crop')
def test_remove_background(mock_crop, mock_session, mock_remove, content_type, expected_status):
    mock_remove.return_value = b"image"
    mock_crop.return_value = b"image"
    files = {"file": ("test.jpg", b"fake", content_type)}
    resp = client.post("/remove-background?model_name=u2net", files=files)
    assert resp.status_code == expected_status

@pytest.mark.parametrize("contour, fake_img, expected_status", [
    ("invalid", b"fake", 500),
    ("[[0,0]]", b"fake", 500),
    ("[[0,0], [10,10], [0,10]]", b"fake", 500),
    ("[[0,0], [10,10], [0,10]]", b'\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR\x00\x00\x00\x01\x00\x00\x00\x01\x08\x06\x00\x00\x00\x1f\x15\xc4\x89\x00\x00\x00\nIDATx\x9cc\xfa\x0f\x00\x01\x05\x01\x02\xcf\xa0.\xeb\x00\x00\x00\x00IEND\xaeB`\x82', 200),
    ("[]", b'\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR\x00\x00\x00\x01\x00\x00\x00\x01\x08\x06\x00\x00\x00\x1f\x15\xc4\x89\x00\x00\x00\nIDATx\x9cc\xfa\x0f\x00\x01\x05\x01\x02\xcf\xa0.\xeb\x00\x00\x00\x00IEND\xaeB`\x82', 500),
    ("[[0,0], [1,1]]", b'\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR\x00\x00\x00\x01\x00\x00\x00\x01\x08\x06\x00\x00\x00\x1f\x15\xc4\x89\x00\x00\x00\nIDATx\x9cc\xfa\x0f\x00\x01\x05\x01\x02\xcf\xa0.\xeb\x00\x00\x00\x00IEND\xaeB`\x82', 500),
])
def test_apply_manual_mask(contour, fake_img, expected_status):
    files = {
        "file": ("test.png", fake_img, "image/png"),
        "contour_json": (None, contour, "text/plain")
    }
    resp = client.post("/apply-manual-mask", files=files)
    assert resp.status_code == expected_status