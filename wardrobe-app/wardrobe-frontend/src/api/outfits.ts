import api from './index';
import type {
    OutfitResponseDto,
    CreateOutfitDto,
    UpdateOutfitDto,
    OutfitFilterDto
} from '../types';

export const outfitsApi = {
    getAll: (filters?: OutfitFilterDto) => {
        return api.get<OutfitResponseDto[]>('/outfits', { params: filters });
    },

    create: (data: CreateOutfitDto) => api.post<OutfitResponseDto>('/outfits', data),

    delete: (id: string) => api.delete(`/outfits/${id}`),

    getById: (id: string) => api.get<OutfitResponseDto>(`/outfits/${id}`),

    update: (id: string, data: any) => api.put<OutfitResponseDto>(`/outfits/${id}`, data),
};
