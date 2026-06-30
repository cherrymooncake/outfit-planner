<template>
  <el-dialog
      v-model="visible"
      :title="dialogTitle"
      width="450px"
      append-to-body
      @close="handleClose"
  >
    <div class="add-row">
      <el-input
          v-model="newCategoryName"
          :placeholder="placeholderText"
          @keyup.enter="addCategory"
      />
      <el-button type="primary" :icon="Plus" @click="addCategory" :loading="loadingAdd">
        Добавить
      </el-button>
    </div>

    <el-divider content-position="center">Список</el-divider>

    <div v-loading="loadingList" class="list-container">
      <div v-for="cat in categories" :key="cat.id" class="list-item">

        <template v-if="editingId !== cat.id">
          <span>{{ cat.name }}</span>
          <div class="actions">
            <el-button link type="primary" :icon="Edit" @click="startEdit(cat)" />
            <el-popconfirm title="Удалить?" @confirm="deleteCategory(cat.id)">
              <template #reference>
                <el-button link type="danger" :icon="Delete" />
              </template>
            </el-popconfirm>
          </div>
        </template>

        <template v-else>
          <el-input v-model="editingName" size="small" style="margin-right: 5px" />
          <el-button type="success" :icon="Check" circle size="small" @click="saveEdit(cat.id)" />
          <el-button :icon="Close" circle size="small" @click="cancelEdit" />
        </template>

      </div>
      <el-empty v-if="categories.length === 0" description="Нет категорий" :image-size="50" />
    </div>

  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { categoriesApi } from '../api/categories';
import type { CategoryDto } from '../types';
import { Plus, Delete, Edit, Check, Close } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const props = withDefaults(defineProps<{
  modelValue: boolean;
  mode?: 'item' | 'outfit';
}>(), {
  mode: 'item'
});

const emit = defineEmits(['update:modelValue', 'change']);

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
});

const dialogTitle = computed(() =>
    props.mode === 'item' ? 'Категории вещей' : 'Категории образов'
);

const placeholderText = computed(() =>
    props.mode === 'item' ? 'Новая категория вещей...' : 'Новая категория образов...'
);

const categories = ref<CategoryDto[]>([]);
const newCategoryName = ref('');
const loadingAdd = ref(false);
const loadingList = ref(false);

const editingId = ref<string | null>(null);
const editingName = ref('');

const fetchCategories = async () => {
  loadingList.value = true;
  try {
    const res = await categoriesApi.getAll();
    if (props.mode === 'outfit') {
      categories.value = res.data.filter(c => c.isOutfitCategory);
    } else {
      categories.value = res.data.filter(c => c.isItemCategory);
    }
  } catch (e) {
    console.error(e);
    ElMessage.error('Ошибка загрузки категорий');
  } finally {
    loadingList.value = false;
  }
};

const addCategory = async () => {
  if (!newCategoryName.value.trim()) return;
  loadingAdd.value = true;
  try {
    await categoriesApi.create({
      name: newCategoryName.value,
      isItemCategory: props.mode === 'item',
      isOutfitCategory: props.mode === 'outfit'
    });
    newCategoryName.value = '';
    await fetchCategories();
    emit('change');
    ElMessage.success('Добавлено');
  } catch (e) {
    ElMessage.error('Ошибка добавления');
  } finally {
    loadingAdd.value = false;
  }
};

const deleteCategory = async (id: string) => {
  try {
    await categoriesApi.delete(id);
    await fetchCategories();
    emit('change');
  } catch (e) {
    ElMessage.error('Ошибка удаления');
  }
};

const startEdit = (cat: CategoryDto) => {
  editingId.value = cat.id;
  editingName.value = cat.name;
};

const cancelEdit = () => {
  editingId.value = null;
  editingName.value = '';
};

const saveEdit = async (id: string) => {
  try {
    await categoriesApi.update(id, { name: editingName.value });
    editingId.value = null;
    await fetchCategories();
    emit('change');
  } catch (e) {
    ElMessage.error('Ошибка обновления');
  }
};

const handleClose = () => {
  newCategoryName.value = '';
  cancelEdit();
};

watch(() => visible.value, (isOpen) => {
  if (isOpen) {
    fetchCategories();
  }
});

onMounted(() => {
  if (visible.value) {
    fetchCategories();
  }
});
</script>

<style scoped>
.add-row {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}
.list-container {
  max-height: 300px;
  overflow-y: auto;
}
.list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}
.actions {
  display: flex;
  gap: 5px;
}
</style>
