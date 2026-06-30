<template>
  <el-dialog
      v-model="visible"
      title="Управление тегами"
      width="450px"
      append-to-body
  >
    <div class="add-row">
      <el-input
          v-model="newName"
          placeholder="Новый тег..."
          @keyup.enter="addTag"
      />
      <el-button type="primary" :icon="Plus" @click="addTag" :loading="loadingAdd">
        Добавить
      </el-button>
    </div>

    <el-divider content-position="center">Список</el-divider>

    <div v-loading="loadingList" class="list-container">
      <div v-for="tag in tags" :key="tag.id" class="list-item">
        <template v-if="editingId !== tag.id">
          <span>{{ tag.name }}</span>
          <div class="actions">
            <el-button link type="primary" :icon="Edit" @click="startEdit(tag)" />
            <el-popconfirm title="Удалить?" @confirm="deleteTag(tag.id)">
              <template #reference>
                <el-button link type="danger" :icon="Delete" />
              </template>
            </el-popconfirm>
          </div>
        </template>
        <template v-else>
          <el-input v-model="editingName" size="small" style="margin-right: 5px" />
          <el-button type="success" :icon="Check" circle size="small" @click="saveEdit(tag.id)" />
          <el-button :icon="Close" circle size="small" @click="cancelEdit" />
        </template>
      </div>
      <el-empty v-if="tags.length === 0" description="Нет тегов" :image-size="50" />
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { tagsApi } from '../api/tags';
import type { TagDto } from '../types';
import { Plus, Delete, Edit, Check, Close } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const props = defineProps<{ modelValue: boolean }>();
const emit = defineEmits(['update:modelValue', 'change']);

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
});

const tags = ref<TagDto[]>([]);
const newName = ref('');
const loadingAdd = ref(false);
const loadingList = ref(false);

const editingId = ref<string | null>(null);
const editingName = ref('');

const fetchTags = async () => {
  loadingList.value = true;
  try {
    const res = await tagsApi.getAll();
    tags.value = res.data.filter(t => t.isItemTag);
  } finally { loadingList.value = false; }
};

const addTag = async () => {
  if (!newName.value.trim()) return;
  loadingAdd.value = true;
  try {
    await tagsApi.create({ name: newName.value, isItemTag: true, isOutfitTag: false });
    newName.value = '';
    await fetchTags();
    emit('change');
    ElMessage.success('Добавлено');
  } catch { ElMessage.error('Ошибка'); }
  finally { loadingAdd.value = false; }
};

const deleteTag = async (id: string) => {
  try { await tagsApi.delete(id); await fetchTags(); emit('change'); } catch { ElMessage.error('Ошибка'); }
};

const startEdit = (tag: TagDto) => {
  editingId.value = tag.id;
  editingName.value = tag.name;
};
const cancelEdit = () => { editingId.value = null; };
const saveEdit = async (id: string) => {
  try { await tagsApi.update(id, { name: editingName.value }); editingId.value = null; await fetchTags(); emit('change'); } catch { ElMessage.error('Ошибка'); }
};

onMounted(() => fetchTags());
</script>

<style scoped>
.add-row { display: flex; gap: 10px; margin-bottom: 10px; }
.list-container { max-height: 300px; overflow-y: auto; }
.list-item { display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 1px solid #f0f0f0; }
</style>
