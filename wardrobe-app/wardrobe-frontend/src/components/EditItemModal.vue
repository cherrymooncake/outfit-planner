<template>
  <el-dialog
      v-model="visible"
      title="Редактировать вещь"
      width="500px"
      @close="handleClose"
  >
    <el-form :model="form" label-position="top">

      <el-form-item label="Название">
        <el-input v-model="form.name" />
      </el-form-item>

      <el-form-item label="Описание">
        <el-input v-model="form.description" type="textarea" :rows="3" />
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

      <el-form-item label="Теги">
        <div class="select-row">
          <el-select
              v-model="form.tagIds"
              multiple
              placeholder="Выберите теги"
              style="flex-grow: 1"
          >
            <el-option
                v-for="tag in tags"
                :key="tag.id"
                :label="tag.name"
                :value="tag.id"
            />
          </el-select>
          <el-button :icon="Setting" circle @click="showTagManager = true" title="Управление тегами" />
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
        mode="item"
        @change="refreshCategories"
    />

    <TagManager
        v-model="showTagManager"
        @change="refreshTags"
    />

  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue';
import { itemsApi } from '../api/items';
import { commonApi } from '../api/common';
import type { CategoryDto, TagDto, ItemResponseDto } from '../types';
import { ElMessage } from 'element-plus';
import { Setting } from '@element-plus/icons-vue';

import CategoryManager from './CategoryManager.vue';
import TagManager from './TagManager.vue';

const props = defineProps<{
  modelValue: boolean;
  item: ItemResponseDto | null;
}>();

const emit = defineEmits(['update:modelValue', 'item-updated', 'dictionaries-updated']);

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
});

const loading = ref(false);
const categories = ref<CategoryDto[]>([]);
const tags = ref<TagDto[]>([]);

const showCatManager = ref(false);
const showTagManager = ref(false);

const form = reactive({
  name: '',
  description: '',
  categoryIds: [] as string[],
  tagIds: [] as string[]
});


const refreshCategories = async () => {
  try {
    const res = await commonApi.getCategories();
    categories.value = res.data.filter(c => c.isItemCategory);
    emit('dictionaries-updated');
  } catch (e) {
    console.error('Ошибка обновления категорий', e);
  }
};

const refreshTags = async () => {
  try {
    const res = await commonApi.getTags();
    tags.value = res.data.filter(t => t.isItemTag);
    emit('dictionaries-updated');
  } catch (e) {
    console.error('Ошибка обновления тегов', e);
  }
};

onMounted(() => {
  refreshCategories();
  refreshTags();
});

watch(() => visible.value, (isOpen) => {
  if (isOpen) {
    refreshCategories();
    refreshTags();
  }
});


watch(() => props.item, (newItem) => {
  if (newItem) {
    form.name = newItem.name;
    form.description = newItem.description || '';
    form.categoryIds = newItem.categories.map(c => c.id);
    form.tagIds = newItem.tags.map(t => t.id);
  }
}, { immediate: true });

const handleClose = () => {
};

const submitUpdate = async () => {
  if (!props.item) return;
  if (!form.name) {
    ElMessage.warning('Название обязательно');
    return;
  }

  loading.value = true;
  try {
    await itemsApi.update(props.item.id, {
      name: form.name,
      description: form.description,
      categoryIds: form.categoryIds,
      tagIds: form.tagIds
    });

    ElMessage.success('Вещь обновлена');
    emit('item-updated');
    visible.value = false;
  } catch (error) {
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
