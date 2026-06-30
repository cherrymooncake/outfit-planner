<template>
  <div class="preview-container" :style="containerStyle">
    <div class="canvas-content">
      <img
          v-for="item in sortedItems"
          :key="item.id"
          :src="getImageUrl(item.itemImageUrl)"
          class="preview-item"
          :style="getItemStyle(item)"
          draggable="false"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { IMG_BASE_URL } from '../../api';
import type { OutfitItemResponseDto } from '../../types';

const props = defineProps<{
  items: OutfitItemResponseDto[];
  width: number;
  height?: number;

  canvasWidth?: number;
  canvasHeight?: number;
}>();

const DEFAULT_ORIGINAL_WIDTH = 800;
const DEFAULT_ORIGINAL_HEIGHT = 600;

const originalW = computed(() => props.canvasWidth || DEFAULT_ORIGINAL_WIDTH);
const originalH = computed(() => props.canvasHeight || DEFAULT_ORIGINAL_HEIGHT);

const scaleFactor = computed(() => props.width / originalW.value);

const containerHeight = computed(() => {
  if (props.height) return props.height;
  return originalH.value * scaleFactor.value;
});

const containerStyle = computed(() => ({
  width: `${props.width}px`,
  height: `${containerHeight.value}px`,
  backgroundColor: '#fff',
  position: 'relative' as const,
  overflow: 'hidden',
  border: '1px solid #e4e7ed',
  borderRadius: '4px',
  backgroundImage: 'radial-gradient(#f0f0f0 1px, transparent 1px)',
  backgroundSize: '10px 10px'
}));

const sortedItems = computed(() => {
  return [...props.items].sort((a, b) => a.zIndex - b.zIndex);
});

const getImageUrl = (path: string) => {
  if (!path) return '';
  const cleanPath = path.startsWith('/') ? path.substring(1) : path;
  return `${IMG_BASE_URL}/${cleanPath}`;
};

const getItemStyle = (item: OutfitItemResponseDto) => {
  const s = scaleFactor.value;

  return {
    position: 'absolute' as const,

    left: `${item.x * s}px`,
    top: `${item.y * s}px`,

    transform: `translate(-50%, -50%) rotate(${item.rotation}deg) scale(${item.scale * s})`,

    width: 'auto',
    height: 'auto',
    maxWidth: 'none',

    pointerEvents: 'none' as const,
    userSelect: 'none' as const
  };
};
</script>

<style scoped>
.preview-container {
  box-sizing: border-box;
}

.canvas-content {
  width: 100%;
  height: 100%;
  position: relative;
}

.preview-item {
  will-change: transform;
}
</style>
