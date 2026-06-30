import api from './index';
import type { ItemResponseDto, ItemFilterDto, UpdateItemDto } from '../types';

export interface ManualMaskDto {
    contourJson: string;
}

export const itemsApi = {
    getAll: (filters?: ItemFilterDto) => api.get<ItemResponseDto[]>('/items', { params: filters }),

    create: (formData: FormData) => api.post<ItemResponseDto>('/items', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    }),

    update: (id: string, data: UpdateItemDto) => api.put<ItemResponseDto>(`/items/${id}`, data),

    delete: (id: string) => api.delete(`/items/${id}`),

    reprocessMask: (id: string, data: ManualMaskDto) =>
        api.post<{ url: string }>(`/items/${id}/reprocess-mask`, data),

    updateProcessedImage: (id: string, file: Blob) => {
        const formData = new FormData();
        formData.append('file', file);
        return api.post<{ url: string }>(`/items/${id}/update-image`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    },

    restoreAutoMask: (id: string) => api.post<{ url: string }>(`/items/${id}/restore-auto`, {}),

};
