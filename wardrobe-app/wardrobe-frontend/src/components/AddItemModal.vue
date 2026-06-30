<template>
  <el-dialog
      v-model="visible"
      title="Добавить новую вещь"
      width="500px"
      @close="handleClose"
  >
    <el-form :model="form" label-position="top">

      <el-form-item label="Фотография">
        <el-upload
            ref="uploadRef"
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :on-exceed="handleExceed"
            list-type="picture"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            Перетащите файл или <em>нажмите для загрузки</em>
          </div>
        </el-upload>
      </el-form-item>

      <el-form-item label="Название">
        <el-input v-model="form.name" placeholder="Например: Красное платье" />
      </el-form-item>

      <el-form-item label="Описание">
        <el-input v-model="form.description" type="textarea" placeholder="Краткое описание" />
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
                v-for="item in categories"
                :key="item.id"
                :label="item.name"
                :value="item.id"
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
                v-for="item in tags"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            />
          </el-select>
          <el-button :icon="Setting" circle @click="showTagManager = true" title="Управление тегами" />
        </div>
      </el-form-item>

    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">Отмена</el-button>
        <el-button type="primary" @click="submitUpload" :loading="uploading">
          Загрузить
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
import { ref, reactive, computed, onMounted } from 'vue';
import { UploadFilled, Setting } from '@element-plus/icons-vue';
import { itemsApi } from '../api/items';
import { commonApi } from '../api/common';
import type { CategoryDto, TagDto } from '../types';
import { ElMessage, type UploadFile, type UploadInstance, type UploadProps } from 'element-plus';

import CategoryManager from './CategoryManager.vue';
import TagManager from './TagManager.vue';

const props = defineProps<{ modelValue: boolean }>();
const emit = defineEmits(['update:modelValue', 'item-added', 'dictionaries-updated']);

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
});

const uploadRef = ref<UploadInstance>();

const uploading = ref(false);
const categories = ref<CategoryDto[]>([]);
const tags = ref<TagDto[]>([]);
const selectedFile = ref<File | null>(null);

const showCatManager = ref(false);
const showTagManager = ref(false);

const form = reactive({
  name: '',
  description: '',
  categoryIds: [] as string[],
  tagIds: [] as string[],
});


const refreshCategories = async () => {
  try {
    const res = await commonApi.getCategories();
    categories.value = res.data.filter(c => c.isItemCategory);
    emit('dictionaries-updated');
  } catch (e) { console.error(e); }
};

const refreshTags = async () => {
  try {
    const res = await commonApi.getTags();
    tags.value = res.data.filter(t => t.isItemTag);
    emit('dictionaries-updated');
  } catch (e) { console.error(e); }
};

onMounted(() => {
  refreshCategories();
  refreshTags();
});

const handleFileChange = (uploadFile: UploadFile) => {
  if (uploadFile.raw) {
    selectedFile.value = uploadFile.raw;
    if (!form.name) {
      form.name = uploadFile.name.replace(/\.[^/.]+$/, "");
    }
  }
};

const handleFileRemove = () => {
  selectedFile.value = null;
};

const handleExceed: UploadProps['onExceed'] = (files) => {
  uploadRef.value!.clearFiles();
  const file = files[0] as any;
  uploadRef.value!.handleStart(file);
};

const handleClose = () => {
  form.name = '';
  form.description = '';
  form.categoryIds = [];
  form.tagIds = [];
  selectedFile.value = null;

  uploadRef.value?.clearFiles();
};

const submitUpload = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('Пожалуйста, выберите изображение');
    return;
  }
  if (!form.name) {
    ElMessage.warning('Введите название вещи');
    return;
  }

  uploading.value = true;
  try {
    const formData = new FormData();
    formData.append('Name', form.name);
    if (form.description) formData.append('Description', form.description);
    formData.append('Image', selectedFile.value);

    form.categoryIds.forEach(id => formData.append('CategoryIds', id));
    form.tagIds.forEach(id => formData.append('TagIds', id));

    await itemsApi.create(formData);

    ElMessage.success('Вещь успешно добавлена!');
    emit('item-added');
    visible.value = false;
  } catch (error) {
    console.error(error);
    ElMessage.error('Ошибка при загрузке');
  } finally {
    uploading.value = false;
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
