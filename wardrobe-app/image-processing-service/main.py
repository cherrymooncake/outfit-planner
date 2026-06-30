import json
from typing import Literal

import cv2
import numpy as np
import uvicorn
from fastapi import FastAPI, File, HTTPException, Query, UploadFile
from fastapi.responses import Response

from rembg import new_session, remove

app = FastAPI(
    title="Wardrobe Image Processing Service",
    description="Сервис для выделения предметов одежды из изображений.",
    version="1.4.0 (Final Stable)",
)


def post_process_smart_crop(image_bytes: bytes) -> bytes:
    try:
        nparr = np.frombuffer(image_bytes, np.uint8)
        img = cv2.imdecode(nparr, cv2.IMREAD_UNCHANGED)
        if img is None or len(img.shape) < 3 or img.shape[2] != 4:
            return image_bytes
        alpha = img[:, :, 3]
        _, thresh = cv2.threshold(alpha, 10, 255, cv2.THRESH_BINARY)
        contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        if not contours:
            return image_bytes
        main_contour = max(contours, key=cv2.contourArea)
        x, y, w, h = cv2.boundingRect(main_contour)
        cropped_img = img[y:y + h, x:x + w]
        is_success, output_image_bytes = cv2.imencode('.png', cropped_img)
        if not is_success:
            return image_bytes
        return output_image_bytes.tobytes()
    except Exception:
        return image_bytes


@app.post(
    "/remove-background",
    tags=["Image Processing"],
    summary="Автоматически удалить фон с изображения",
)
async def remove_background(
        model_name: Literal["u2net", "u2netp", "silueta"] = Query(
            "u2net",
            description="Модель для удаления фона. 'u2net' - стандартная, 'silueta' - может быть лучше для объектов с резкими краями.",
        ),
        file: UploadFile = File(..., description="Файл изображения для обработки."),
):
    if file.content_type and not file.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="Загруженный файл не является изображением.")
    try:
        input_image_bytes = await file.read()

        session = new_session(model_name=model_name)

        output_rembg_bytes = remove(input_image_bytes, session=session)

        final_image_bytes = post_process_smart_crop(output_rembg_bytes)
        return Response(content=final_image_bytes, media_type="image/png")
    except Exception as e:
        import traceback
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=f"Произошла внутренняя ошибка сервера: {str(e)}")


@app.post(
    "/apply-manual-mask",
    tags=["Image Processing"],
    summary="Применить ручную маску (контур) к изображению",
)
async def apply_manual_mask(
        file: UploadFile = File(..., description="Оригинальный файл изображения."),
        contour_json: str = File(..., description='JSON-строка с массивом точек контура.'),
):
    try:
        input_image_bytes = await file.read()
        nparr = np.frombuffer(input_image_bytes, np.uint8)
        original_img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
        if original_img is None:
            raise HTTPException(status_code=400, detail="Не удалось прочитать изображение.")

        contour_points = json.loads(contour_json)
        contour_np = np.array(contour_points, dtype=np.int32)

        mask = np.zeros(original_img.shape[:2], dtype=np.uint8)
        cv2.fillPoly(mask, [contour_np], (255))

        b, g, r = cv2.split(original_img)
        masked_img = cv2.merge([b, g, r, mask])

        x, y, w, h = cv2.boundingRect(contour_np)
        cropped_img = masked_img[y:y + h, x:x + w]

        is_success, output_image_bytes = cv2.imencode('.png', cropped_img)
        if not is_success:
            raise HTTPException(status_code=500, detail="Не удалось закодировать финальное изображение.")

        return Response(content=output_image_bytes.tobytes(), media_type="image/png")
    except Exception as e:
        import traceback
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=f"Произошла ошибка при применении маски: {str(e)}")


if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)