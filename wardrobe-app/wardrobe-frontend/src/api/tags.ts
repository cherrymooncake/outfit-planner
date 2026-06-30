import api from './index';
import type { TagDto, CreateTagDto, UpdateTagDto } from '../types';

export const tagsApi = {
    getAll: () => api.get<TagDto[]>('/tags'),
    create: (data: CreateTagDto) => api.post<TagDto>('/tags', data),
    update: (id: string, data: UpdateTagDto) => api.put<TagDto>(`/tags/${id}`, data),
    delete: (id: string) => api.delete(`/tags/${id}`),
};
