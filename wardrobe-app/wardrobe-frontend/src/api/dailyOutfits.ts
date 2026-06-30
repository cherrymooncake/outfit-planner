import api from './index';
import type {
    DailyOutfitDto,
    SetDailyOutfitDto,
    OutfitResponseDto,
    AiRecommendationRequestDto,
    AiRecommendationResponseDto
} from '../types';

export const dailyOutfitsApi = {
    getByDate: (date: string) => api.get<DailyOutfitDto | null>(`/daily-outfits/${date}`),

    getMonth: (year: number, month: number) =>
        api.get<DailyOutfitDto[]>(`/daily-outfits/month?year=${year}&month=${month}`),

    setOutfit: (data: SetDailyOutfitDto) => api.post<DailyOutfitDto>('/daily-outfits', data),

    deleteOutfit: (date: string) => api.delete(`/daily-outfits/${date}`),

    getRandomOutfit: () => api.get<OutfitResponseDto>('/daily-outfits/random'),

    getAiRecommendation: (data: AiRecommendationRequestDto) =>
        api.post<AiRecommendationResponseDto>('/daily-outfits/recommend', data)
};
