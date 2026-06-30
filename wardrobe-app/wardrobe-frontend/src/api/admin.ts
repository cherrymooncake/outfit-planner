import api from './index';
import type { HealthStatusDto, GlobalStatsDto, UserStatDto } from '../types';

export const adminApi = {
    getHealth: () => api.get<HealthStatusDto>('/admin/health'),

    getGlobalStats: () => api.get<GlobalStatsDto>('/admin/stats/global'),

    getUsers: () => api.get<UserStatDto[]>('/admin/users'),

    changeRole: (id: string, newRole: string) =>
        api.put(`/admin/users/${id}/role`, { newRole }),

    downloadBackup: async (type: 'db' | 'files' | 'full') => {
        const response = await api.get(`/admin/backup/${type}`, {
            responseType: 'blob'
        });

        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;

        const dateStr = new Date().toISOString().replace(/[:.]/g, '-').slice(0, 19);
        const ext = type === 'db' ? 'sql' : 'zip';
        link.setAttribute('download', `backup_${type}_${dateStr}.${ext}`);

        document.body.appendChild(link);
        link.click();

        link.remove();
        window.URL.revokeObjectURL(url);
    }
};
