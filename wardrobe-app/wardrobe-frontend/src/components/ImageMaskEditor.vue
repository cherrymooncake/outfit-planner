<script setup lang="ts">
import {ref, computed, onUnmounted, watch, nextTick} from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {itemsApi} from '../api/items';
import {
  Pointer, EditPen, DeleteFilled, Loading,
  RefreshLeft, RefreshRight, MagicStick
} from '@element-plus/icons-vue';

const props = defineProps<{
  itemId: string;
  originalImageUrl: string;
  processedImageUrl?: string;
  visible: boolean;
}>();

const emit = defineEmits(['update:visible', 'mask-saved']);


const loading = ref(false);
const activeTool = ref<'lasso' | 'eraser'>('lasso');


const points = ref<{ x: number; y: number }[]>([]);
const isClosed = ref(false);
const isDrawing = ref(false);
const draggingPointIndex = ref<number | null>(null);
const svgImageRef = ref<HTMLImageElement | null>(null);

const displayDims = ref({w: 0, h: 0});
const naturalDims = ref({w: 0, h: 0});

const svgHistory = ref<{ x: number, y: number }[][]>([]);
const svgHistoryIndex = ref(-1);


const getSqSegDist = (p: { x: number, y: number }, p1: { x: number, y: number }, p2: { x: number, y: number }) => {
  let x = p1.x, y = p1.y;
  let dx = p2.x - x, dy = p2.y - y;
  if (dx !== 0 || dy !== 0) {
    const t = ((p.x - x) * dx + (p.y - y) * dy) / (dx * dx + dy * dy);
    if (t > 1) {
      x = p2.x;
      y = p2.y;
    } else if (t > 0) {
      x += dx * t;
      y += dy * t;
    }
  }
  dx = p.x - x;
  dy = p.y - y;
  return dx * dx + dy * dy;
};

const simplifyPoints = (pts: { x: number, y: number }[], tolerance: number) => {
  if (pts.length <= 2) return pts;
  const sqTolerance = tolerance * tolerance;

  let maxSqDist = 0;
  let index = 0;

  for (let i = 1; i < pts.length - 1; i++) {
    const sqDist = getSqSegDist(pts[i], pts[0], pts[pts.length - 1]);
    if (sqDist > maxSqDist) {
      index = i;
      maxSqDist = sqDist;
    }
  }

  if (maxSqDist > sqTolerance) {
    const leftPart = simplifyPoints(pts.slice(0, index + 1), tolerance);
    const rightPart = simplifyPoints(pts.slice(index), tolerance);
    return [...leftPart.slice(0, -1), ...rightPart];
  } else {
    return [pts[0], pts[pts.length - 1]];
  }
};

const saveSvgState = () => {
  if (svgHistoryIndex.value < svgHistory.value.length - 1) {
    svgHistory.value.splice(svgHistoryIndex.value + 1);
  }
  const snapshot = points.value.map(p => ({...p}));
  svgHistory.value.push(snapshot);
  svgHistoryIndex.value++;
};

const undoSvg = () => {
  if (svgHistoryIndex.value > 0) {
    svgHistoryIndex.value--;
    points.value = svgHistory.value[svgHistoryIndex.value].map(p => ({...p}));
    if (points.value.length < 3) isClosed.value = false;
  } else {
    resetLasso(false);
  }
};

const redoSvg = () => {
  if (svgHistoryIndex.value < svgHistory.value.length - 1) {
    svgHistoryIndex.value++;
    points.value = svgHistory.value[svgHistoryIndex.value].map(p => ({...p}));
    if (points.value.length > 3) isClosed.value = true;
  }
};

const canUndoSvg = computed(() => svgHistoryIndex.value >= 0);
const canRedoSvg = computed(() => svgHistoryIndex.value < svgHistory.value.length - 1);


const onSvgImageLoad = () => {
  if (svgImageRef.value) {
    naturalDims.value.w = svgImageRef.value.naturalWidth;
    naturalDims.value.h = svgImageRef.value.naturalHeight;
    updateDisplayDimensions();
  }
};

const handleMouseDown = (event: MouseEvent) => {
  if (draggingPointIndex.value !== null) return;
  if (isClosed.value) return;

  const x = event.offsetX;
  const y = event.offsetY;

  isDrawing.value = true;
  points.value.push({x, y});
};

const handlePointMouseDown = (index: number, event: MouseEvent) => {
  event.stopPropagation();
  draggingPointIndex.value = index;
};

const handleMouseMove = (event: MouseEvent) => {
  const x = event.offsetX;
  const y = event.offsetY;

  if (draggingPointIndex.value !== null) {
    points.value[draggingPointIndex.value] = {x, y};
    return;
  }

  if (isDrawing.value && !isClosed.value) {
    const lastPoint = points.value[points.value.length - 1];
    if (lastPoint) {
      const distance = Math.hypot(x - lastPoint.x, y - lastPoint.y);
      if (distance < 3) return;
    }
    points.value.push({x, y});
  }
};

const handleMouseUp = () => {
  let stateChanged = false;

  if (draggingPointIndex.value !== null) {
    draggingPointIndex.value = null;
    stateChanged = true;
  }

  if (isDrawing.value) {
    isDrawing.value = false;

    if (points.value.length > 5) {
      points.value = simplifyPoints(points.value, 2.5);
    }
    stateChanged = true;
  }

  if (stateChanged) saveSvgState();
};

const closePath = (event?: Event) => {
  if (event) event.preventDefault();
  if (points.value.length < 3) {
    ElMessage.warning('Нужно минимум 3 точки для контура');
    return;
  }
  isClosed.value = true;
  isDrawing.value = false;
  saveSvgState();
};

const resetLasso = (clearHistory = true) => {
  points.value = [];
  isClosed.value = false;
  isDrawing.value = false;
  draggingPointIndex.value = null;
  if (clearHistory) {
    svgHistory.value = [];
    svgHistoryIndex.value = -1;
  }
};

const svgPoints = computed(() => points.value.map(p => `${p.x},${p.y}`).join(' '));


const canvasRef = ref<HTMLCanvasElement | null>(null);
const ctx = ref<CanvasRenderingContext2D | null>(null);
const isErasing = ref(false);
const brushSize = ref(30);
const canvasImageLoaded = ref(false);

const canvasHistory = ref<ImageData[]>([]);
const canvasHistoryIndex = ref(-1);

const initCanvas = async (forceUrl?: string | Event) => {
  await nextTick();
  if (!canvasRef.value) return;

  let urlToLoad = (typeof forceUrl === 'string') ? forceUrl : props.processedImageUrl;

  if (!urlToLoad) {
    ElMessage.error('Обработанное изображение не найдено');
    return;
  }
  const isResetClick = (typeof forceUrl === 'object');

  if (!isResetClick && !forceUrl && canvasImageLoaded.value && canvasHistory.value.length > 0) return;

  canvasImageLoaded.value = false;
  const c = canvasRef.value;
  ctx.value = c.getContext('2d', { willReadFrequently: true });

  const img = new Image();
  img.crossOrigin = "Anonymous";

  const sep = urlToLoad.includes('?') ? '&' : '?';
  img.src = `${urlToLoad}${sep}t=${Date.now()}`;

  img.onload = () => {
    c.width = img.naturalWidth;
    c.height = img.naturalHeight;
    ctx.value?.drawImage(img, 0, 0);
    canvasImageLoaded.value = true;

    canvasHistory.value = [];
    canvasHistoryIndex.value = -1;
    saveCanvasState();
  };

  img.onerror = () => {
    canvasImageLoaded.value = true;
    ElMessage.error('Ошибка загрузки изображения');
  };
};

const saveCanvasState = () => {
  if (!ctx.value || !canvasRef.value) return;
  if (canvasHistoryIndex.value < canvasHistory.value.length - 1) {
    canvasHistory.value.splice(canvasHistoryIndex.value + 1);
  }
  const data = ctx.value.getImageData(0, 0, canvasRef.value.width, canvasRef.value.height);
  canvasHistory.value.push(data);
  canvasHistoryIndex.value++;
};

const undoCanvas = () => {
  if (canvasHistoryIndex.value > 0) {
    canvasHistoryIndex.value--;
    putHistory();
  }
};

const redoCanvas = () => {
  if (canvasHistoryIndex.value < canvasHistory.value.length - 1) {
    canvasHistoryIndex.value++;
    putHistory();
  }
};

const putHistory = () => {
  if (!ctx.value || canvasHistoryIndex.value < 0) return;
  ctx.value.putImageData(canvasHistory.value[canvasHistoryIndex.value], 0, 0);
};

const canUndoCanvas = computed(() => canvasHistoryIndex.value > 0);
const canRedoCanvas = computed(() => canvasHistoryIndex.value < canvasHistory.value.length - 1);

const getCanvasCoordinates = (e: MouseEvent) => {
  if (!canvasRef.value) return {x: 0, y: 0};
  const rect = canvasRef.value.getBoundingClientRect();
  const scaleX = canvasRef.value.width / rect.width;
  const scaleY = canvasRef.value.height / rect.height;
  return {
    x: (e.clientX - rect.left) * scaleX,
    y: (e.clientY - rect.top) * scaleY
  };
};

const handleEraserDown = (e: MouseEvent) => {
  isErasing.value = true;
  erase(e);
};
const handleEraserMove = (e: MouseEvent) => {
  if (isErasing.value) erase(e);
};
const handleEraserUp = () => {
  if (isErasing.value) {
    isErasing.value = false;
    saveCanvasState();
  }
};

const erase = (e: MouseEvent) => {
  if (!ctx.value) return;
  const {x, y} = getCanvasCoordinates(e);
  ctx.value.globalCompositeOperation = 'destination-out';
  ctx.value.beginPath();
  ctx.value.arc(x, y, brushSize.value, 0, Math.PI * 2);
  ctx.value.fill();
};


const updateDisplayDimensions = () => {
  if (activeTool.value === 'lasso' && svgImageRef.value) {
    displayDims.value.w = svgImageRef.value.clientWidth;
    displayDims.value.h = svgImageRef.value.clientHeight;
  }
};

window.addEventListener('resize', updateDisplayDimensions);
onUnmounted(() => window.removeEventListener('resize', updateDisplayDimensions));

watch(activeTool, async (newVal) => {
  if (newVal === 'eraser') {
    await initCanvas();
  } else {
    nextTick(() => updateDisplayDimensions());
  }
});

watch(() => props.visible, (val) => {
  if (val) {
    activeTool.value = 'lasso';
    resetLasso();
    canvasHistory.value = [];
    canvasHistoryIndex.value = -1;
    loading.value = false;
  }
});

const handleRestoreAuto = async () => {
  try {
    await ElMessageBox.confirm(
        'Это отменит все ручные правки и запустит автоматическую вырезку заново. Продолжить?',
        'Сброс к оригиналу',
        { confirmButtonText: 'Да, сбросить', cancelButtonText: 'Отмена', type: 'warning' }
    );

    loading.value = true;
    await itemsApi.restoreAutoMask(props.itemId);

    ElMessage.success('Авто-вырезка восстановлена');

    emit('mask-saved');

    close();

  } catch (e) {
    if (e !== 'cancel') console.error(e);
  } finally {
    loading.value = false;
  }
};


const save = async () => {
  loading.value = true;
  try {
    if (activeTool.value === 'lasso') {
      if (points.value.length < 3) {
        ElMessage.warning('Создайте контур перед сохранением');
        loading.value = false;
        return;
      }
      const scaleX = naturalDims.value.w / displayDims.value.w;
      const scaleY = naturalDims.value.h / displayDims.value.h;
      const mappedPoints = points.value.map(p => [Math.round(p.x * scaleX), Math.round(p.y * scaleY)]);

      await itemsApi.reprocessMask(props.itemId, {contourJson: JSON.stringify(mappedPoints)});

    } else {
      if (!canvasRef.value) return;
      const blob = await new Promise<Blob | null>(r => canvasRef.value!.toBlob(r, 'image/png'));
      if (blob) {
        await itemsApi.updateProcessedImage(props.itemId, blob);
      }
    }
    ElMessage.success('Изображение обновлено!');
    emit('mask-saved');
    emit('update:visible', false);
  } catch (e: any) {
    ElMessage.error('Ошибка сохранения');
  } finally {
    loading.value = false;
  }
};

const close = () => emit('update:visible', false);
const cursorStyle = computed(() => {
  if (draggingPointIndex.value !== null) return 'grabbing';
  if (isClosed.value) return 'default';
  return 'crosshair';
});
</script>

<template>
  <el-dialog
      :model-value="visible"
      title="Редактор вырезки"
      width="950px"
      top="3vh"
      @close="close"
      :close-on-click-modal="false"
      align-center
  >
    <div class="top-controls">
      <el-radio-group v-model="activeTool" size="large">
        <el-radio-button value="lasso">
          <el-icon class="mr-1">
            <EditPen/>
          </el-icon>
          Лассо
        </el-radio-button>
        <el-radio-button value="eraser">
          <el-icon class="mr-1">
            <DeleteFilled/>
          </el-icon>
          Ластик
        </el-radio-button>
      </el-radio-group>

      <div v-if="activeTool === 'eraser'" class="tool-group">
        <span class="label">Размер:</span>
        <el-slider v-model="brushSize" :min="5" :max="150" style="width: 100px;" size="small"/>
        <div class="brush-preview" :style="{ width: brushSize + 'px', height: brushSize + 'px' }"></div>

        <el-button-group class="history-btns">
          <el-tooltip content="Отменить" placement="bottom" :show-after="500">
            <el-button :disabled="!canUndoCanvas" @click="undoCanvas" :icon="RefreshLeft" size="small"/>
          </el-tooltip>
          <el-tooltip content="Повторить" placement="bottom" :show-after="500">
            <el-button :disabled="!canRedoCanvas" @click="redoCanvas" :icon="RefreshRight" size="small"/>
          </el-tooltip>
        </el-button-group>
      </div>

      <div v-else class="tool-group">
        <el-button-group class="history-btns">
          <el-tooltip content="Отменить последнюю точку" placement="bottom" :show-after="500">
            <el-button :disabled="!canUndoSvg" @click="undoSvg" :icon="RefreshLeft" size="small"/>
          </el-tooltip>
          <el-tooltip content="Вернуть точку" placement="bottom" :show-after="500">
            <el-button :disabled="!canRedoSvg" @click="redoSvg" :icon="RefreshRight" size="small"/>
          </el-tooltip>
        </el-button-group>
      </div>

      <div class="auto-restore-group">
        <el-tooltip content="Сбросить все правки и запустить авто-удаление фона" placement="bottom" :show-after="500">
          <el-button type="info" plain :icon="MagicStick" @click="handleRestoreAuto" :loading="loading">
            Авто-вырезка
          </el-button>
        </el-tooltip>
      </div>

      <div class="hint">
        <span v-if="activeTool === 'lasso'">Рисуйте контур. Точки можно двигать. ПКМ — замкнуть.</span>
        <span v-else>Сотрите лишнее.</span>
      </div>
    </div>

    <div class="editor-viewport">
      <div v-show="activeTool === 'lasso'" class="image-container" @contextmenu.prevent="closePath">
        <img ref="svgImageRef" :src="props.originalImageUrl" @load="onSvgImageLoad" class="target-image"/>
        <svg
            class="overlay-svg"
            :width="displayDims.w"
            :height="displayDims.h"
            :style="{ cursor: cursorStyle }"
            @mousedown="handleMouseDown"
            @mousemove="handleMouseMove"
            @mouseup="handleMouseUp"
            @mouseleave="handleMouseUp"
        >
          <polygon v-if="isClosed" :points="svgPoints" fill="rgba(64, 158, 255, 0.4)" stroke="#d128a1"
                   stroke-width="2"/>
          <polyline v-else :points="svgPoints" fill="none" stroke="#d128a1" stroke-width="2" stroke-linecap="round"
                    stroke-linejoin="round"/>

          <template v-if="!isDrawing">
            <circle
                v-for="(p, index) in points"
                :key="index"
                :cx="p.x"
                :cy="p.y"
                r="5"
                class="interactive-point"
                :class="{ 'is-dragging': draggingPointIndex === index }"
                @mousedown.stop="handlePointMouseDown(index, $event)"
            />
          </template>
        </svg>
      </div>

      <div v-show="activeTool === 'eraser'" class="canvas-container">
        <canvas
            ref="canvasRef"
            class="editor-canvas"
            @mousedown="handleEraserDown"
            @mousemove="handleEraserMove"
            @mouseup="handleEraserUp"
            @mouseleave="handleEraserUp"
        ></canvas>
        <div v-if="!canvasImageLoaded" class="loading-overlay">
          <el-icon class="is-loading">
            <Loading/>
          </el-icon>
          Загрузка...
        </div>
      </div>
    </div>

    <template #footer>
      <div class="footer-row">
        <div>
          <el-button v-if="activeTool === 'lasso'" @click="resetLasso()">Сброс контура</el-button>
          <el-button v-if="activeTool === 'lasso' && !isClosed" @click="closePath" type="warning" plain
                     :disabled="points.length < 3">Замкнуть
          </el-button>
          <el-button v-if="activeTool === 'eraser'" @click="initCanvas" type="warning" plain>Сбросить</el-button>
        </div>
        <div>
          <el-button @click="close">Отмена</el-button>
          <el-button type="primary" @click="save" :loading="loading">Сохранить</el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.top-controls {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 15px;
  flex-wrap: wrap;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 15px;
}

.tool-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.history-btns {
  margin-left: 10px;
}

.brush-preview {
  background-color: #333;
  border-radius: 50%;
  border: 1px solid #ccc;
  max-width: 30px;
  max-height: 30px;
}

.label {
  font-size: 13px;
  color: #606266;
}

.hint {
  margin-left: auto;
  color: #909399;
  font-size: 13px;
}

.mr-1 {
  margin-right: 5px;
}

.editor-viewport {
  background-color: #eee;
  background-image: linear-gradient(45deg, #ccc 25%, transparent 25%), linear-gradient(-45deg, #ccc 25%, transparent 25%), linear-gradient(45deg, transparent 75%, #ccc 75%), linear-gradient(-45deg, transparent 75%, #ccc 75%);
  background-size: 20px 20px;
  background-position: 0 0, 0 10px, 10px -10px, -10px 0px;
  min-height: 400px;
  max-height: 65vh;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 4px;
  overflow: auto;
  padding: 20px;
  position: relative;
}

.image-container {
  position: relative;
  line-height: 0;
  user-select: none;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.target-image {
  max-height: 60vh;
  max-width: 100%;
  display: block;
  pointer-events: none;
}

.overlay-svg {
  position: absolute;
  top: 0;
  left: 0;
}

.interactive-point {
  fill: white;
  stroke: #d128a1;
  stroke-width: 1.5;
  cursor: grab;
  transition: r 0.1s;
}

.interactive-point:hover {
  r: 7;
  fill: #d128a1;
}

.interactive-point.is-dragging {
  cursor: grabbing;
  fill: #e6a23c;
  stroke: #e6a23c;
  r: 7;
}

.canvas-container {
  position: relative;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.editor-canvas {
  display: block;
  max-height: 60vh;
  max-width: 100%;
  background-color: transparent;
  cursor: crosshair;
}

.auto-restore-group {
  margin-left: 10px;
  border-left: 1px solid #dcdfe6;
  padding-left: 15px;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  color: #d128a1;
}

.footer-row {
  display: flex;
  justify-content: space-between;
}
</style>
