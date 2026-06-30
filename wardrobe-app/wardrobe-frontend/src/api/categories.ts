import api from './index';
import type { CategoryDto, CreateCategoryDto, UpdateCategoryDto } from '../types';

export const categoriesApi = {
    getAll: () => api.get<CategoryDto[]>('/categories'),
    create: (data: CreateCategoryDto) => api.post<CategoryDto>('/categories', data),
    update: (id: string, data: UpdateCategoryDto) => api.put<CategoryDto>(`/categories/${id}`, data),
    delete: (id: string) => api.delete(`/categories/${id}`),
};
