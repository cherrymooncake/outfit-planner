<script setup lang="ts">
import {onMounted, onUnmounted, ref, markRaw} from 'vue';
import {Canvas, FabricImage, Rect, Textbox, Group} from 'fabric';
import type {OutfitItemResponseDto, TemplateItemResponseDto} from '../../types';
import {IMG_BASE_URL} from '../../api';

const emit = defineEmits([
  'object-modified',
  'selection-created',
  'selection-cleared',
  'items-changed',
  'slot-selected',
  'history-changed'
]);

const canvasRef = ref<HTMLCanvasElement | null>(null);
const wrapperRef = ref<HTMLDivElement | null>(null);
let canvasInstance: Canvas | null = null;

const history: string[] = [];
const historyIndex = ref(-1);
let isHistoryProcessing = false;

const getImageUrl = (path: string) => {
  if (!path) return '';
  const cleanPath = path.startsWith('/') ? path.substring(1) : path;
  return `${IMG_BASE_URL}/${cleanPath}`;
};

const initCanvas = (width?: number, height?: number) => {
  if (!canvasRef.value || !wrapperRef.value) return;

  const initialWidth = width || wrapperRef.value.clientWidth;
  const initialHeight = height || wrapperRef.value.clientHeight;

  if (canvasInstance) {
    canvasInstance.setDimensions({width: initialWidth, height: initialHeight});
    canvasInstance.clear();
    canvasInstance.backgroundColor = '#ffffff';
    history.length = 0;
    historyIndex.value = -1;
    saveHistory();
    return;
  }

  const newCanvas = new Canvas(canvasRef.value, {
    preserveObjectStacking: true,
    backgroundColor: '#ffffff',
    width: initialWidth,
    height: initialHeight,
  });

  canvasInstance = markRaw(newCanvas);

  const handleSelection = () => {
    const activeObj = canvasInstance?.getActiveObject();
    if (activeObj) {
      emit('selection-created');
      // @ts-ignore
      if (activeObj.data?.isSlot) {
        // @ts-ignore
        emit('slot-selected', activeObj.data.categoryIdHint);
      }
      // @ts-ignore
      else if (activeObj.data?.isSlotFilled) {
                // @ts-ignore
        emit('slot-selected', activeObj.data.categoryIdHint);
      }
    } else {
      emit('selection-cleared');
    }
  };

  canvasInstance.on('selection:created', handleSelection);
  canvasInstance.on('selection:updated', handleSelection);
  canvasInstance.on('selection:cleared', handleSelection);

  const handleChange = () => {
    if (!isHistoryProcessing) {
      saveHistory();
      emit('object-modified');
      emit('items-changed');
    }
  };

  canvasInstance.on('object:modified', handleChange);
  canvasInstance.on('object:added', handleChange);
  canvasInstance.on('object:removed', handleChange);

  saveHistory();
};

const saveHistory = () => {
  if (!canvasInstance) return;

  if (historyIndex.value < history.length - 1) {
    history.splice(historyIndex.value + 1);
  }

  const json = JSON.stringify(canvasInstance.toJSON(['data']));
  history.push(json);
  historyIndex.value++;
  emit('history-changed', {canUndo: canUndo(), canRedo: canRedo()});
};

const undo = async () => {
  if (historyIndex.value <= 0 || !canvasInstance) return;
  isHistoryProcessing = true;
  historyIndex.value--;
  await loadHistoryState();
  isHistoryProcessing = false;
  emit('history-changed', {canUndo: canUndo(), canRedo: canRedo()});
};

const redo = async () => {
  if (historyIndex.value >= history.length - 1 || !canvasInstance) return;
  isHistoryProcessing = true;
  historyIndex.value++;
  await loadHistoryState();
  isHistoryProcessing = false;
  emit('history-changed', {canUndo: canUndo(), canRedo: canRedo()});
};

const loadHistoryState = async () => {
  if (!canvasInstance) return;
  const json = history[historyIndex.value];
  await canvasInstance.loadFromJSON(json);
  canvasInstance.requestRenderAll();
};

const canUndo = () => historyIndex.value > 0;
const canRedo = () => historyIndex.value < history.length - 1;


const setSize = (width: number, height: number) => {
  if (!canvasInstance) return;
  canvasInstance.setDimensions({width, height});
  canvasInstance.renderAll();
  saveHistory();
};

const bringForward = () => {
  const obj = canvasInstance?.getActiveObject();
  if (obj) {
    canvasInstance?.bringObjectForward(obj);
    canvasInstance?.requestRenderAll();
    saveHistory();
  }
};

const sendBackwards = () => {
  const obj = canvasInstance?.getActiveObject();
  if (obj) {
    canvasInstance?.sendObjectBackwards(obj);
    canvasInstance?.requestRenderAll();
    saveHistory();
  }
};

const removeActiveObject = () => {
  const obj = canvasInstance?.getActiveObject();
  if (obj) {
    canvasInstance?.remove(obj);
    canvasInstance?.discardActiveObject();
    canvasInstance?.requestRenderAll();
  }
};

const removeObject = (obj: any) => {
  if (canvasInstance) {
    canvasInstance.remove(obj);
    canvasInstance.requestRenderAll();
  }
};

const hasItem = (itemId: string): boolean => {
  if (!canvasInstance) return false;
  // @ts-ignore
  return canvasInstance.getObjects().some(obj => obj.data?.itemId === itemId);
};

const getActiveSlot = () => {
  const obj = canvasInstance?.getActiveObject();
  // @ts-ignore
  if (obj && obj.data?.isSlot) return obj;
  return null;
};

const getActiveFilledSlot = () => {
  const obj = canvasInstance?.getActiveObject();
  // @ts-ignore
  if (obj && obj.data?.isSlotFilled) return obj;
  return null;
};

const replaceActiveObjectImage = async (url: string, newItemId: string) => {
  const obj = canvasInstance?.getActiveObject();
  if (!obj || !(obj instanceof FabricImage)) return;

  try {
    const separator = url.includes('?') ? '&' : '?';
    const safeUrl = `${url}${separator}cors_bypass=${new Date().getTime()}`;

    await obj.setSrc(safeUrl, {crossOrigin: 'anonymous'});

    // @ts-ignore
    if (obj.data) obj.data.itemId = newItemId;

    canvasInstance?.requestRenderAll();
    saveHistory();
  } catch (e) {
    console.error('Error replacing image', e);
  }
};

const loadItems = async (items: OutfitItemResponseDto[]) => {
  if (!canvasInstance) return;
  isHistoryProcessing = true;

  canvasInstance.clear();
  canvasInstance.backgroundColor = '#ffffff';

  const sortedItems = [...items].sort((a, b) => a.zIndex - b.zIndex);

  for (const item of sortedItems) {
    const fullUrl = getImageUrl(item.itemImageUrl);
    await addImage(fullUrl, item.itemId, {
      left: item.x,
      top: item.y,
      scale: item.scale,
      rotation: item.rotation,
      noSetActive: true
    });
  }
  canvasInstance.requestRenderAll();

  history.length = 0;
  historyIndex.value = -1;
  saveHistory();
  isHistoryProcessing = false;
};

const addImage = async (url: string, itemId: string, options: any = {}) => {
  if (!canvasInstance || !url) return;

  try {
    const separator = url.includes('?') ? '&' : '?';
    const safeUrl = `${url}${separator}cors_bypass=${new Date().getTime()}`;
    const img = await FabricImage.fromURL(safeUrl, {crossOrigin: 'anonymous'});

    img.set({
      left: options.left || 100,
      top: options.top || 100,
      scaleX: options.scale || 0.5,
      scaleY: options.scale || 0.5,
      angle: options.rotation || 0,
      originX: 'center',
      originY: 'center',
      cornerSize: 10,
      transparentCorners: false,
      cornerColor: '#d128a1',
      borderColor: '#d128a1',
      // @ts-ignore
      data: {
        itemId: itemId,
        isSlot: false,
        isSlotFilled: options.isSlotFilled || false,
        categoryIdHint: options.categoryIdHint || undefined
      }
    });

    if (options.scale === undefined && img.width && img.width > 300) {
      img.scaleToWidth(300);
    }

    canvasInstance.add(img);
    if (!options.noSetActive) {
      canvasInstance.setActiveObject(img);
    }
    canvasInstance.requestRenderAll();
  } catch (err) {
    console.error("Error adding image:", err);
  }
};

const loadTemplate = (items: TemplateItemResponseDto[]) => {
  if (!canvasInstance) return;
  isHistoryProcessing = true;
  canvasInstance.clear();
  canvasInstance.backgroundColor = '#ffffff';

  const sortedItems = [...items].sort((a, b) => a.zIndex - b.zIndex);

  for (const item of sortedItems) {
    addSlot(item.categoryName || 'Любая вещь', item.categoryIdHint, {
      left: item.x,
      top: item.y,
      scale: item.scale,
      rotation: item.rotation
    });
  }
  canvasInstance.requestRenderAll();

  history.length = 0;
  historyIndex.value = -1;
  saveHistory();
  isHistoryProcessing = false;
};

const addSlot = (categoryName: string, categoryId: string | undefined, options: any = {}) => {
  if (!canvasInstance) return;

  const baseWidth = 200;
  const baseHeight = 200;

  const rect = new Rect({
    width: baseWidth,
    height: baseHeight,
    fill: 'rgba(200, 200, 200, 0.3)',
    stroke: '#909399',
    strokeWidth: 2,
    strokeDashArray: [10, 5],
    originX: 'center',
    originY: 'center'
  });

  const text = new Textbox(categoryName, {
    fontSize: 24,
    fill: '#606266',
    textAlign: 'center',
    originX: 'center',
    originY: 'center',
    width: baseWidth - 20,
    splitByGrapheme: true
  });

  const group = new Group([rect, text], {
    left: options.left || 100,
    top: options.top || 100,
    scaleX: options.scale || 1,
    scaleY: options.scale || 1,
    angle: options.rotation || 0,
    originX: 'center',
    originY: 'center',
    cornerSize: 10,
    transparentCorners: false,
    cornerColor: '#e6a23c',
    borderColor: '#e6a23c',
    // @ts-ignore
    data: {
      isSlot: true,
      categoryIdHint: categoryId
    }
  });

  canvasInstance.add(group);
};

const getAllItems = () => {
  if (!canvasInstance) return [];

  return canvasInstance.getObjects().map((obj, index) => {
    // @ts-ignore
    if (obj.data?.isSlot) return null;

    // @ts-ignore
    const storedId = obj.data?.itemId;
    if (!storedId) return null;

    return {
      itemId: storedId,
      x: obj.left || 0,
      y: obj.top || 0,
      scale: obj.scaleX || 1,
      rotation: obj.angle || 0,
      zIndex: index
    };
  }).filter(i => i !== null);
};

const handleKeyDown = (e: KeyboardEvent) => {

  const target = e.target as HTMLElement;
  const isInput = ['INPUT', 'TEXTAREA', 'SELECT'].includes(target.tagName) || target.isContentEditable;

  if (isInput) {
    return;
  }

  if (e.key === 'Delete' || e.key === 'Backspace') {
    removeActiveObject();
  }
  // Ctrl+Z
  if ((e.ctrlKey || e.metaKey) && e.key === 'z') {
    e.preventDefault();
    undo();
  }
  // Ctrl+Y (Windows) or Cmd+Shift+Z (Mac)
  if ((e.ctrlKey || e.metaKey) && (e.key === 'y' || (e.shiftKey && e.key === 'z'))) {
    e.preventDefault();
    redo();
  }
};

defineExpose({
  initCanvas,
  addImage,
  addSlot,
  getAllItems,
  loadItems,
  loadTemplate,
  setSize,
  bringForward,
  sendBackwards,
  removeActiveObject,
  removeObject,
  hasItem,
  getActiveSlot,
  getActiveFilledSlot,
  replaceActiveObjectImage,
  undo,
  redo,
  canUndo,
  canRedo,
  getCanvas: () => canvasInstance
});

onMounted(() => {
  window.addEventListener('keydown', handleKeyDown);
});

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyDown);
  if (canvasInstance) {
    canvasInstance.dispose();
  }
});
</script>

<template>
  <div class="canvas-wrapper" ref="wrapperRef" tabindex="0">
    <canvas ref="canvasRef"></canvas>
  </div>
</template>

<style scoped>
.canvas-wrapper {
  width: 100%;
  height: 100%;
  background-color: transparent;
  box-shadow: none;

  overflow: auto;
  display: flex;
  justify-content: center;
  align-items: center;
  outline: none;
}
:deep(.canvas-container) {
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
  margin: 20px;
}
</style>
