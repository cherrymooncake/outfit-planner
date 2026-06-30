import uvicorn
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Optional
from sentence_transformers import SentenceTransformer, util

app = FastAPI(
    title="Wardrobe AI Stylist Service",
    description="Сервис для семантического подбора образов.",
    version="1.0.0",
)

print("Загрузка модели ИИ-стилиста...")
nlp_model = SentenceTransformer('paraphrase-multilingual-MiniLM-L12-v2')
print("Модель готова!")

class OutfitItem(BaseModel):
    id: str
    text_description: str

class RecommendRequest(BaseModel):
    prompt: str
    weather_context: Optional[str] = None
    outfits: List[OutfitItem]

@app.post("/recommend", tags=["AI"])
async def recommend_outfit(req: RecommendRequest):
    if not req.outfits:
        raise HTTPException(status_code=400, detail="Список образов пуст.")

    query = req.prompt.strip()
    if req.weather_context:
        query += f". Погода: {req.weather_context}. Образ должен соответствовать этим условиям."

    query_embedding = nlp_model.encode(query, convert_to_tensor=True)
    outfit_texts = [o.text_description for o in req.outfits]
    outfit_embeddings = nlp_model.encode(outfit_texts, convert_to_tensor=True)

    cosine_scores = util.cos_sim(query_embedding, outfit_embeddings)[0]
    best_idx = cosine_scores.argmax().item()
    score = cosine_scores[best_idx].item()

    if score < 0.2:
        explanation = "Я не нашел точного совпадения в гардеробе, но этот вариант кажется наиболее подходящим."
    else:
        explanation = "Этот образ лучше всего соответствует вашему запросу и текущей погоде."

    return {
        "recommended_outfit_id": req.outfits[best_idx].id,
        "explanation": explanation,
        "score": float(score)
    }

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8001, reload=True)