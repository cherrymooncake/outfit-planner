import { mount } from '@vue/test-utils'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import ImageMaskEditor from '../src/components/ImageMaskEditor.vue'

describe('ImageMaskEditor Algorithm', () => {
    beforeEach(() => {
        vi.stubGlobal('ResizeObserver', vi.fn(() => ({
            observe: vi.fn(),
            unobserve: vi.fn(),
            disconnect: vi.fn()
        })))
    })

    it.each([
        [3, 3],
        [10, 2],
        [50, 2],
        [100, 2]
    ])('Ramer-Douglas-Peucker simplifies points', async (inputPointsCount, expectedMaxPoints) => {
        const wrapper = mount(ImageMaskEditor, {
            props: {
                itemId: '1',
                originalImageUrl: '/test.jpg',
                visible: true
            }
        })

        const svg = wrapper.find('svg')

        await svg.trigger('mousedown', { offsetX: 0, offsetY: 0 })

        for (let i = 1; i < inputPointsCount; i++) {
            await svg.trigger('mousemove', { offsetX: i, offsetY: i })
        }

        await svg.trigger('mouseup')

        const pointsArray = (wrapper.vm as any).points

        expect(pointsArray.length).toBeLessThanOrEqual(expectedMaxPoints)
        if (inputPointsCount > 5) {
            expect(pointsArray.length).toBeLessThan(inputPointsCount)
        }
    })

    it.each([
        [true, false],
        [false, true]
    ])('SVG state history boundaries', async (drawFirst, expectEmpty) => {
        const wrapper = mount(ImageMaskEditor, {
            props: {
                itemId: '1',
                originalImageUrl: '/test.jpg',
                visible: true
            }
        })

        const vm = wrapper.vm as any

        if (drawFirst) {
            await wrapper.find('svg').trigger('mousedown', { offsetX: 10, offsetY: 10 })
            await wrapper.find('svg').trigger('mouseup')
        }

        vm.undoSvg()

        expect(vm.points.length === 0).toBe(expectEmpty)
    })
})
