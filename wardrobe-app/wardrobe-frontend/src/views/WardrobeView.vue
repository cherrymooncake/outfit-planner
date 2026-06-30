<template>
  <div class="wardrobe-page">
    <div class="header-row">
      <h1>Мой гардероб</h1>
      <el-button type="primary" :icon="Plus" @click="showAddModal = true">
        Добавить вещь
      </el-button>
    </div>

    <el-card class="filter-card" shadow="never">
      <div class="filters-row">
        <el-input
            v-model="filters.searchTerm"
            placeholder="Поиск по названию..."
            prefix-icon="Search"
            clearable
            @input="handleFilterChange"
            style="width: 300px"
        />

        <el-select
            v-model="filters.tagId"
            placeholder="Фильтр по тегу"
            clearable
            @change="handleFilterChange"
            style="width: 200px"
        >
          <el-option
              v-for="tag in tags"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
          />
        </el-select>

        <el-button @click="resetFilters">Сброс всех фильтров</el-button>
      </div>
    </el-card>

    <div class="categories-nav">
      <div
          class="cat-pill"
          :class="{ active: !filters.categoryId }"
          @click="selectCategory(undefined)"
      >
        Все вещи
      </div>
      <div
          v-for="cat in categories"
          :key="cat.id"
          class="cat-pill"
          :class="{ active: filters.categoryId === cat.id }"
          @click="selectCategory(cat.id)"
      >
        {{ cat.name }}
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>

    <el-empty v-else-if="items.length === 0" description="Ничего не найдено" />

    <div v-else class="items-grid">
      <el-card v-for="item in items" :key="item.id" :body-style="{ padding: '0px' }" class="item-card">
        <div class="image-container" @click="openDetailModal(item)">
          <img :src="getImageUrl(item.processedImageUrl)" class="image" />

          <div class="card-actions">
            <el-button
                circle
                :icon="Edit"
                type="primary"
                @click.stop="openEditModal(item)"
                title="Редактировать описание"
            />

            <el-button
                circle
                :icon="Scissor"
                type="warning"
                @click.stop="openMaskEditor(item)"
                title="Ручная коррекция вырезки"
            />

            <el-popconfirm title="Удалить эту вещь?" @confirm="deleteItem(item.id)">
              <template #reference>
                <el-button circle :icon="Delete" type="danger" title="Удалить" @click.stop />
              </template>
            </el-popconfirm>
          </div>
        </div>

        <div class="card-info">
          <div class="item-name" :title="item.name">{{ item.name }}</div>
          <div class="tags-row">
            <el-tooltip
                v-for="cat in item.categories"
                :key="cat.id"
                content="Категория"
                placement="top"
                :show-after="500"
            >
              <el-tag size="small" effect="plain">{{ cat.name }}</el-tag>
            </el-tooltip>

            <el-tooltip
                v-for="tag in item.tags"
                :key="tag.id"
                content="Тег"
                placement="top"
                :show-after="500"
            >
              <el-tag size="small" type="info">{{ tag.name }}</el-tag>
            </el-tooltip>
          </div>
        </div>
      </el-card>
    </div>

    <AddItemModal
        v-model="showAddModal"
        @item-added="fetchItems"
        @dictionaries-updated="handleDictionariesUpdate"
    />

    <EditItemModal
        v-model="showEditModal"
        :item="selectedItem"
        @item-updated="fetchItems"
        @dictionaries-updated="handleDictionariesUpdate"
    />

    <TryOnModal
        v-model="showTryOnModal"
        :item="tryOnItem"
    />


    <ImageMaskEditor
        v-if="maskingItem"
        v-model:visible="showMaskEditor"
        :item-id="maskingItem.id"
        :original-image-url="getImageUrl(maskingItem.originalImageUrl)"
        :processed-image-url="getImageUrl(maskingItem.processedImageUrl)"
        @mask-saved="handleMaskSaved"
    />

    <el-dialog
        v-model="showDetailModal"
        :title="detailItem?.name"
        width="700px"
        align-center
        destroy-on-close
    >
      <div v-if="detailItem" class="detail-content">
        <div class="detail-image-wrapper">
          <img :src="getImageUrl(detailItem.processedImageUrl)" class="detail-image" />
        </div>

        <div class="detail-info">
          <div class="info-section">
            <h4>Описание</h4>
            <p class="description">{{ detailItem.description || 'Нет описания' }}</p>
          </div>

          <div class="info-section">
            <h4>Категории</h4>
            <div class="tags-row">
              <el-tag v-for="cat in detailItem.categories" :key="cat.id" effect="plain">
                {{ cat.name }}
              </el-tag>
              <span v-if="detailItem.categories.length === 0" class="text-muted">Нет категорий</span>
            </div>
          </div>

          <div class="info-section">
            <h4>Теги</h4>
            <div class="tags-row">
              <el-tag v-for="tag in detailItem.tags" :key="tag.id" type="info">
                {{ tag.name }}
              </el-tag>
              <span v-if="detailItem.tags.length === 0" class="text-muted">Нет тегов</span>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="detail-actions">
          <el-popconfirm
              title="Вы уверены, что хотите удалить эту вещь?"
              confirm-button-text="Да"
              cancel-button-text="Нет"
              @confirm="handleDeleteFromDetail"
          >
            <template #reference>
              <el-button type="danger" plain :icon="Delete">Удалить</el-button>
            </template>
          </el-popconfirm>

          <div class="right-buttons">
            <el-button :icon="Scissor" type="warning" plain @click="handleMaskFromDetail">
              Изменить вырезку
            </el-button>

            <el-button type="primary" :icon="Edit" @click="handleEditFromDetail">
              Редактировать
            </el-button>
          </div>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue';
import { itemsApi } from '../api/items';
import { commonApi } from '../api/common';
import { IMG_BASE_URL } from '../api';
import type { ItemResponseDto, CategoryDto, TagDto, ItemFilterDto } from '../types';
import { Plus, Search, Edit, Delete, Scissor, MagicStick } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

import AddItemModal from '../components/AddItemModal.vue';
import EditItemModal from '../components/EditItemModal.vue';
import ImageMaskEditor from '../components/ImageMaskEditor.vue';

const items = ref<ItemResponseDto[]>([]);
const categories = ref<CategoryDto[]>([]);
const tags = ref<TagDto[]>([]);
const loading = ref(true);

const showAddModal = ref(false);
const showEditModal = ref(false);
const showMaskEditor = ref(false);
const showDetailModal = ref(false);
const showTryOnModal = ref(false);

const selectedItem = ref<ItemResponseDto | null>(null);
const maskingItem = ref<ItemResponseDto | null>(null);
const detailItem = ref<ItemResponseDto | null>(null);
const tryOnItem = ref<ItemResponseDto | null>(null);

const filters = reactive<ItemFilterDto>({
  searchTerm: '',
  categoryId: undefined,
  tagId: undefined
});


const getImageUrl = (path: string) => {
  if (!path) return '';
  const cleanPath = path.startsWith('/') ? path.substring(1) : path;
  return `${IMG_BASE_URL}/${cleanPath}`;
};

const loadDictionaries = async () => {
  try {
    const [catRes, tagRes] = await Promise.all([
      commonApi.getCategories(),
      commonApi.getTags()
    ]);
    categories.value = catRes.data.filter(c => c.isItemCategory);
    tags.value = tagRes.data.filter(t => t.isItemTag);
  } catch (e) {
    console.error('Ошибка загрузки фильтров');
  }
};

const fetchItems = async () => {
  loading.value = true;
  try {
    const response = await itemsApi.getAll(filters);
    items.value = response.data;

    if (detailItem.value) {
      const updatedItem = items.value.find(i => i.id === detailItem.value?.id);
      if (updatedItem) {
        detailItem.value = updatedItem;
      }
    }
  } catch (error) {
    ElMessage.error('Не удалось загрузить вещи');
  } finally {
    loading.value = false;
  }
};

const selectCategory = (id: string | undefined) => {
  filters.categoryId = id;
  fetchItems();
};

const handleDictionariesUpdate = () => {
  loadDictionaries();
  fetchItems();
};

let timeout: number;
const handleFilterChange = () => {
  clearTimeout(timeout);
  timeout = setTimeout(() => {
    fetchItems();
  }, 300);
};

const resetFilters = () => {
  filters.searchTerm = '';
  filters.categoryId = undefined;
  filters.tagId = undefined;
  fetchItems();
};


const deleteItem = async (id: string) => {
  try {
    await itemsApi.delete(id);
    ElMessage.success('Вещь удалена');
    fetchItems();
  } catch (e) {
    ElMessage.error('Ошибка удаления');
  }
};

const openEditModal = (item: ItemResponseDto) => {
  selectedItem.value = item;
  showEditModal.value = true;
};


const openDetailModal = (item: ItemResponseDto) => {
  detailItem.value = item;
  showDetailModal.value = true;
};

const handleEditFromDetail = () => {
  if (detailItem.value) {
    const itemToEdit = detailItem.value;
    openEditModal(itemToEdit);
  }
};

const handleMaskFromDetail = () => {
  if (detailItem.value) {
    showDetailModal.value = false;
    openMaskEditor(detailItem.value);
  }
};

const handleDeleteFromDetail = async () => {
  if (detailItem.value) {
    await deleteItem(detailItem.value.id);
    showDetailModal.value = false;
  }
};


const openMaskEditor = (item: ItemResponseDto) => {
  maskingItem.value = item;
  showMaskEditor.value = true;
};

const handleMaskSaved = () => {
  if (maskingItem.value) {
    fetchItems();
  }
};

const openTryOn = (item: ItemResponseDto) => {
  tryOnItem.value = item;
  showTryOnModal.value = true;
};

onMounted(() => {
  loadDictionaries();
  fetchItems();
});
</script>

<style scoped>
.wardrobe-page {
  padding-bottom: 40px;
}
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.filter-card {
  margin-bottom: 20px;
}
.filters-row {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
  align-items: center;
}

.categories-nav {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 10px;
  margin-bottom: 20px;
  scrollbar-width: thin;
}

.categories-nav::-webkit-scrollbar {
  height: 6px;
}
.categories-nav::-webkit-scrollbar-thumb {
  background-color: #dcdfe6;
  border-radius: 3px;
}

.cat-pill {
  padding: 8px 20px;
  background-color: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 20px;
  cursor: pointer;
  white-space: nowrap;
  font-size: 14px;
  color: #606266;
  transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  user-select: none;
}

.cat-pill:hover {
  color: #d128a1;
  border-color: #c6e2ff;
  background-color: #ecf5ff;
}

.cat-pill.active {
  background-color: #d128a1;
  color: #fff;
  border-color: #d128a1;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
  font-weight: 500;
}

.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

.item-card {
  transition: transform 0.2s;
}
.item-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.image-container {
  height: 220px;
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f8f9fa;
  background-image:
      linear-gradient(45deg, #eee 25%, transparent 25%),
      linear-gradient(-45deg, #eee 25%, transparent 25%),
      linear-gradient(45deg, transparent 75%, #eee 75%),
      linear-gradient(-45deg, transparent 75%, #eee 75%);
  background-size: 20px 20px;
  cursor: pointer;
}

.image {
  max-width: 90%;
  max-height: 90%;
  object-fit: contain;
}

.card-actions {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  opacity: 0;
  transition: opacity 0.3s;
  cursor: default;
}
.card-actions:hover {
  opacity: 1;
  cursor: pointer;
}
.card-actions .el-button {
  cursor: pointer;
}

.image-container:hover .card-actions {
  opacity: 1;
}

.card-info {
  padding: 12px;
}
.item-name {
  font-weight: 600;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.tags-row {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.detail-content {
  display: flex;
  gap: 30px;
}
.detail-image-wrapper {
  flex: 1;
  background-color: #f5f7fa;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  border: 1px solid #e4e7ed;
  background-image: radial-gradient(#ddd 1px, transparent 1px);
  background-size: 15px 15px;
}
.detail-image {
  max-width: 100%;
  max-height: 400px;
  object-fit: contain;
}
.detail-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.info-section h4 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 16px;
}
.description {
  color: #606266;
  line-height: 1.5;
  white-space: pre-wrap;
}
.text-muted {
  color: #909399;
  font-style: italic;
  font-size: 13px;
}
.detail-actions {
  display: flex;
  justify-content: space-between;
  width: 100%;
}
.right-buttons {
  display: flex;
  gap: 10px;
}
</style>
