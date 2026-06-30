import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { jwtDecode } from 'jwt-decode';

export const useAuthStore = defineStore('auth', () => {
    const token = ref<string | null>(localStorage.getItem('accessToken'));
    const refreshToken = ref<string | null>(localStorage.getItem('refreshToken'));

    const isAuthenticated = computed(() => !!token.value);

    const isAdmin = computed(() => {
        if (!token.value) return false;
        try {
            const decoded: any = jwtDecode(token.value);
            const role = decoded.role || decoded['http://schemas.microsoft.com/ws/2008/06/identity/claims/role'];
            return role === 'Admin';
        } catch (e) {
            return false;
        }
    });

    function setTokens(access: string, refresh: string) {
        token.value = access;
        refreshToken.value = refresh;
        localStorage.setItem('accessToken', access);
        localStorage.setItem('refreshToken', refresh);
    }

    function logout() {
        token.value = null;
        refreshToken.value = null;
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
    }

    return { token, refreshToken, isAuthenticated, isAdmin, setTokens, logout };
});
