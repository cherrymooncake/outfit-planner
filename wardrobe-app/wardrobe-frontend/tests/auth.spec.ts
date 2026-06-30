import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '../src/stores/auth'
import { describe, it, expect, beforeEach } from 'vitest'

describe('Auth Store', () => {
    beforeEach(() => {
        setActivePinia(createPinia())
        localStorage.clear()
    })

    it.each([
        ['token_123', 'refresh_123', true],
        ['', '', false],
        [null, null, false]
    ])('setTokens and isAuthenticated behavior', (access, refresh, expected) => {
        const store = useAuthStore()

        if (access !== null && refresh !== null) {
            store.setTokens(access, refresh)
        }

        expect(store.isAuthenticated).toBe(expected)
        if (expected) {
            expect(localStorage.getItem('accessToken')).toBe(access)
            expect(localStorage.getItem('refreshToken')).toBe(refresh)
        }
    })

    it('logout clears state and storage', () => {
        const store = useAuthStore()
        store.setTokens('acc', 'ref')

        store.logout()

        expect(store.token).toBeNull()
        expect(store.refreshToken).toBeNull()
        expect(store.isAuthenticated).toBe(false)
        expect(localStorage.getItem('accessToken')).toBeNull()
    })

    it.each([
        ['eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiQWRtaW4ifQ.sig', true],
        ['eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiVXNlciJ9.sig', false],
        ['eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3JvbGUiOiJBZG1pbiJ9.sig', true],
        ['invalid.token.string', false],
        ['', false],
        [null, false]
    ])('isAdmin computed property', (tokenString, expectedAdmin) => {
        const store = useAuthStore()
        if (tokenString) {
            store.setTokens(tokenString, 'ref')
        }

        expect(store.isAdmin).toBe(expectedAdmin)
    })
})
