import api from './index';
import type { CategoryDto, TagDto } from '../types';

export const commonApi = {
    getCategories: () => api.get<CategoryDto[]>('/categories'),
    getTags: () => api.get<TagDto[]>('/tags'),
};
