<template>
  <el-dialog
      v-model="visible"
      title="Свойства образа"
      width="500px"
      @close="handleClose"
  >
    <el-form :model="form" label-position="top">

      <el-form-item label="Название" required>
        <el-input v-model="form.name" placeholder="Название образа" />
      </el-form-item>

      <el-form-item label="Описание">
        <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="Описание..."
        />
      </el-form-item>

      <el-form-item label="Категории">
        <div class="select-row">
          <el-select
              v-model="form.categoryIds"
              multiple
              placeholder="Выберите категории"
              style="flex-grow: 1"
          >
            <el-option
                v-for="cat in categories"
                :key="cat.id"
                :label="cat.name"
                :value="cat.id"
            />
          </el-select>
          <el-button :icon="Setting" circle @click="showCatManager = true" title="Управление категориями" />
        </div>
      </el-form-item>

    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">Отмена</el-button>
        <el-button type="primary" @click="submitUpdate" :loading="loading">
          Сохранить
        </el-button>
      </span>
    </template>

    <CategoryManager
        v-model="showCatManager"
        mode="outfit"
        @change="refreshCategories"
    />

  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue';
import { outfitsApi } from '../api/outfits';
import { commonApi } from '../api/common';
import type { CategoryDto, OutfitResponseDto, UpdateOutfitDto, OutfitItemDto } from '../types';
import { ElMessage } from 'element-plus';
import { Setting } from '@element-plus/icons-vue';

import CategoryManager from './CategoryManager.vue';

const props = defineProps<{
  modelValue: boolean;
  outfit: OutfitResponseDto | null;
}>();

const emit = defineEmits(['update:modelValue', 'outfit-updated']);

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
});

const loading = ref(false);
const categories = ref<CategoryDto[]>([]);
const showCatManager = ref(false);

const form = reactive({
  name: '',
  description: '',
  categoryIds: [] as string[]
});

const refreshCategories = async () => {
  try {
    const res = await commonApi.getCategories();
    categories.value = res.data.filter(c => c.isOutfitCategory);
  } catch (e) {
    console.error(e);
  }
};

onMounted(() => {
  refreshCategories();
});

watch(() => visible.value, (isOpen) => {
  if (isOpen) refreshCategories();
});

watch(() => props.outfit, (newVal) => {
  if (newVal) {
    form.name = newVal.name;
    form.description = newVal.description || '';
    form.categoryIds = newVal.categories ? newVal.categories.map(c => c.id) : [];
  }
}, { immediate: true });

const handleClose = () => {

};

const submitUpdate = async () => {
  if (!props.outfit) return;
  if (!form.name.trim()) {
    ElMessage.warning('Название обязательно');
    return;
  }

  loading.value = true;
  try {

    const itemsPayload: OutfitItemDto[] = props.outfit.items.map(i => ({
      itemId: i.itemId,
      x: i.x,
      y: i.y,
      scale: i.scale,
      rotation: i.rotation,
      zIndex: i.zIndex
    }));

    const updatePayload: UpdateOutfitDto = {
      name: form.name,
      description: form.description,
      canvasWidth: props.outfit.canvasWidth,
      canvasHeight: props.outfit.canvasHeight,
      templateId: props.outfit.templateName ? null : null,
      categoryIds: form.categoryIds,
      items: itemsPayload
    };

    await outfitsApi.update(props.outfit.id, updatePayload);

    ElMessage.success('Свойства образа обновлены');
    emit('outfit-updated');
    visible.value = false;
  } catch (e) {
    console.error(e);
    ElMessage.error('Ошибка при сохранении');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.select-row {
  display: flex;
  gap: 10px;
  width: 100%;
}
</style>
