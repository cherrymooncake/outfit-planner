import axios from 'axios';
import { useAuthStore } from '../stores/auth';
import router from '../router';

export const API_URL = 'http://localhost:5202/api';
export const IMG_BASE_URL = 'http://localhost:5202';

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.request.use(
    (config) => {
        const authStore = useAuthStore();
        if (authStore.token) {
            config.headers.Authorization = `Bearer ${authStore.token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const authStore = useAuthStore();

        if (error.response && error.response.status === 401) {
            authStore.logout();
            router.push('/login');
        }
        return Promise.reject(error);
    }
);

export default api;
