import api from './index';
import type { LoginDto, RegisterDto, TokenResponseDto } from '../types';

export interface ChangePasswordDto {
    oldPassword: string;
    newPassword: string;
}

export const authApi = {
    login: (data: LoginDto) => api.post<TokenResponseDto>('/auth/login', data),
    register: (data: RegisterDto) => api.post('/auth/register', data),

    changePassword: (data: ChangePasswordDto) => api.post('/auth/change-password', data),
    deleteAccount: () => api.delete('/auth/account'),
};
