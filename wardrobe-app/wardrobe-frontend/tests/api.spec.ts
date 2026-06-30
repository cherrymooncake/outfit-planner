import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import axios from 'axios'
import api from '../src/api/index'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '../src/stores/auth'
import router from '../src/router'

vi.mock('../src/router', () => ({
    default: {
        push: vi.fn()
    }
}))

describe('Axios Interceptors', () => {
    beforeEach(() => {
        setActivePinia(createPinia())
    })

    afterEach(() => {
        vi.clearAllMocks()
    })

    it.each([
        ['valid_token_123', 'Bearer valid_token_123'],
        [null, undefined]
    ])('request interceptor attaches token', async (token, expectedHeader) => {
        const store = useAuthStore()
        if (token) store.setTokens(token, 'ref')

        const config = { headers: {} } as any
        const interceptedConfig = await (api.interceptors.request as any).handlers[0].fulfilled(config)

        expect(interceptedConfig.headers.Authorization).toBe(expectedHeader)
    })

    it.each([
        [401, true, '/login'],
        [403, false, null],
        [500, false, null],
        [404, false, null]
    ])('response interceptor handles errors', async (status, shouldLogout, expectedRoute) => {
        const store = useAuthStore()
        store.setTokens('acc', 'ref')
        const storeLogoutSpy = vi.spyInstance(store, 'logout')

        const error = {
            response: { status }
        }

        try {
            await (api.interceptors.response as any).handlers[0].rejected(error)
        } catch (e) {
            expect(e).toBe(error)
        }

        if (shouldLogout) {
            expect(storeLogoutSpy).toHaveBeenCalled()
            expect(router.push).toHaveBeenCalledWith(expectedRoute)
        } else {
            expect(storeLogoutSpy).not.toHaveBeenCalled()
            expect(router.push).not.toHaveBeenCalled()
        }
    })
})
