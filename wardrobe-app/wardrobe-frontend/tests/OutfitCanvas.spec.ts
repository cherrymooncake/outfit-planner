import { mount } from '@vue/test-utils'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import OutfitCanvas from '../src/components/outfit/OutfitCanvas.vue'

vi.mock('fabric', () => {
    return {
        Canvas: vi.fn().mockImplementation(() => ({
            setDimensions: vi.fn(),
            clear: vi.fn(),
            on: vi.fn(),
            toJSON: vi.fn().mockReturnValue({ objects: [] }),
            loadFromJSON: vi.fn((json, cb) => { if (cb) cb(); return Promise.resolve() }),
            requestRenderAll: vi.fn(),
            dispose: vi.fn()
        })),
        FabricImage: {
            fromURL: vi.fn().mockResolvedValue({
                set: vi.fn(),
                scaleToWidth: vi.fn()
            })
        },
        Rect: vi.fn(),
        Textbox: vi.fn(),
        Group: vi.fn()
    }
})

describe('OutfitCanvas History', () => {
    beforeEach(() => {
        vi.clearAllMocks()
    })

    it('initializes canvas and history correctly', async () => {
        const wrapper = mount(OutfitCanvas)
        const vm = wrapper.vm as any

        vm.initCanvas(800, 600)

        expect(vm.canUndo()).toBe(false)
        expect(vm.canRedo()).toBe(false)
    })

    it.each([
        [1, true, false],
        [3, true, false],
        [5, true, false]
    ])('history limits after multiple saves', async (savesCount, expectUndo, expectRedo) => {
        const wrapper = mount(OutfitCanvas)
        const vm = wrapper.vm as any

        vm.initCanvas(800, 600)

        for (let i = 0; i < savesCount; i++) {
            vm.saveHistory()
        }

        expect(vm.canUndo()).toBe(expectUndo)
        expect(vm.canRedo()).toBe(expectRedo)
    })

    it('undo and redo traverse history correctly', async () => {
        const wrapper = mount(OutfitCanvas)
        const vm = wrapper.vm as any

        vm.initCanvas(800, 600)
        vm.saveHistory()
        vm.saveHistory()

        expect(vm.canUndo()).toBe(true)

        await vm.undo()
        expect(vm.canRedo()).toBe(true)

        await vm.redo()
        expect(vm.canRedo()).toBe(false)
    })
})
