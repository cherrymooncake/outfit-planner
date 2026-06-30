import api from './index';
import type {
    TemplateResponseDto,
    CreateTemplateDto,
    UpdateTemplateDto
} from '../types';

export const templatesApi = {
    getAll: () => {
        return api.get<TemplateResponseDto[]>('/templates');
    },

    getById: (id: string) => {
        return api.get<TemplateResponseDto>(`/templates/${id}`);
    },

    create: (data: CreateTemplateDto) => {
        return api.post<TemplateResponseDto>('/templates', data);
    },

    update: (id: string, data: UpdateTemplateDto) => {
        return api.put<TemplateResponseDto>(`/templates/${id}`, data);
    },

    delete: (id: string) => {
        return api.delete(`/templates/${id}`);
    }
};
