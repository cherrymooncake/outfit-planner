<template>
  <div class="outfits-page">

    <div class="header-row">
      <h1>Мои образы</h1>
      <el-button type="primary" size="large" :icon="Plus" @click="openTemplateDialog">
        Создать новый образ
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
        <el-button @click="resetFilters">Сброс поиска</el-button>
      </div>
    </el-card>

    <div class="categories-nav">
      <div
          class="cat-pill"
          :class="{ active: !filters.categoryId }"
          @click="selectCategory(undefined)"
      >
        Все образы
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
      <el-skeleton :rows="3" animated count="3" style="width: 300px; display: inline-block; margin-right: 20px"/>
    </div>

    <el-empty
        v-else-if="outfits.length === 0"
        description="У вас пока нет образов (или ничего не найдено)."
    >
      <el-button type="primary" @click="openTemplateDialog">Создать</el-button>
    </el-empty>

    <div v-else class="outfits-grid">
      <el-card
          v-for="outfit in outfits"
          :key="outfit.id"
          class="outfit-card"
          :body-style="{ padding: '0px' }"
          shadow="hover"
      >
        <div class="preview-wrapper" @click="openDetails(outfit)">
          <div class="preview-inner">
            <OutfitPreview
                :items="outfit.items"
                :width="260"
                :canvas-width="outfit.canvasWidth"
                :canvas-height="outfit.canvasHeight"
            />
          </div>

          <div class="card-actions">
            <el-button
                circle
                :icon="EditPen"
                type="primary"
                @click.stop="openMetaEditor(outfit)"
                title="Свойства образа"
            />
            <el-button
                circle
                :icon="Edit"
                type="warning"
                @click.stop="editOutfitDirectly(outfit)"
                title="Конструктор"
            />
            <el-popconfirm title="Удалить?" @confirm="deleteOutfitById(outfit.id)">
              <template #reference>
                <el-button circle :icon="Delete" type="danger" @click.stop/>
              </template>
            </el-popconfirm>
          </div>
        </div>

        <div class="card-footer" @click="openDetails(outfit)">
          <div class="outfit-info">
            <h3 class="outfit-name" :title="outfit.name">{{ outfit.name }}</h3>
            <div class="meta-row">
              <span class="item-count">{{ outfit.items.length }} вещей</span>


              <el-tooltip
                  v-if="outfit.categories && outfit.categories.length > 0"
                  content="Категория"
                  placement="top"
                  :show-after="500"
              >
                <el-tag
                    size="small"
                    effect="plain"
                    style="margin-left: 10px"
                >
                  {{ outfit.categories[0].name }}
                  <span v-if="outfit.categories.length > 1">...</span>
                </el-tag>
              </el-tooltip>

            </div>
          </div>
          <el-icon class="arrow-icon">
            <ArrowRight/>
          </el-icon>
        </div>
      </el-card>
    </div>

    <el-dialog
        v-model="showTemplateDialog"
        title="Выберите шаблон"
        width="800px"
        align-center
    >
      <div v-loading="loadingTemplates" class="templates-grid">
        <div class="template-card special-card" @click="createOutfit(null)">
          <div class="template-preview blank">
            <el-icon size="30" color="#909399">
              <Plus/>
            </el-icon>
          </div>
          <h4>Пустой холст</h4>
          <p>Начать с чистого листа</p>
        </div>

        <div
            v-for="tpl in templates"
            :key="tpl.id"
            class="template-card"
            @click="createOutfit(tpl.id)"
        >
          <div class="template-preview css-preview">
            <div
                v-for="item in tpl.items"
                :key="item.id"
                class="preview-slot"
                :style="getSlotStyle(item)"
                :title="item.categoryName || 'Слот'"
            ></div>
          </div>

          <div class="template-info">
            <h4>{{ tpl.name }}</h4>
            <p>{{ tpl.description || `${tpl.items.length} слотов` }}</p>
          </div>

          <div class="template-actions" @click.stop>
            <div class="action-btn edit-btn" @click="editTemplate(tpl.id)" title="Редактировать шаблон">
              <el-icon>
                <Edit/>
              </el-icon>
            </div>
            <el-popconfirm title="Удалить этот шаблон?" @confirm="deleteTemplate(tpl.id)" width="200">
              <template #reference>
                <div class="action-btn delete-btn" title="Удалить шаблон">
                  <el-icon>
                    <Delete/>
                  </el-icon>
                </div>
              </template>
            </el-popconfirm>
          </div>
        </div>
      </div>
    </el-dialog>

    <el-dialog
        v-model="showDetails"
        :title="selectedOutfit?.name"
        width="700px"
        align-center
        destroy-on-close
    >
      <div v-if="selectedOutfit" class="details-content">
        <div class="large-preview-container">
          <OutfitPreview
              :items="selectedOutfit.items"
              :width="600"
              :canvas-width="selectedOutfit.canvasWidth"
              :canvas-height="selectedOutfit.canvasHeight"
          />
        </div>

        <div class="details-text">
          <p class="description" v-if="selectedOutfit.description">
            {{ selectedOutfit.description }}
          </p>
          <p class="meta text-muted" v-else>Нет описания</p>

          <p v-if="selectedOutfit.templateName" class="meta text-muted" style="margin-top: 5px">
            Создано по шаблону: {{ selectedOutfit.templateName }}
          </p>

          <div class="tags-list" v-if="selectedOutfit.categories?.length">
            <el-tag
                v-for="cat in selectedOutfit.categories"
                :key="cat.id"
                size="small"
                effect="plain"
            >
              {{ cat.name }}
            </el-tag>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer-actions">
          <el-button @click="openMetaEditor(selectedOutfit!)" :icon="EditPen">
            Свойства
          </el-button>
          <el-button type="primary" :icon="Edit" @click="editOutfitDirectly(selectedOutfit!)">
            Конструктор
          </el-button>
          <el-popconfirm
              title="Удалить этот образ?"
              confirm-button-text="Да"
              cancel-button-text="Нет"
              @confirm="deleteOutfitFromDetails"
          >
            <template #reference>
              <el-button type="danger" plain :icon="Delete">Удалить</el-button>
            </template>
          </el-popconfirm>
        </div>
      </template>
    </el-dialog>


    <EditOutfitMetaModal
        v-model="showMetaEditor"
        :outfit="editingOutfit"
        @outfit-updated="handleOutfitUpdated"
    />

  </div>
</template>

<script setup lang="ts">
import {ref, reactive, onMounted} from 'vue';
import {useRouter} from 'vue-router';
import {ElMessage} from 'element-plus';
import {Plus, Delete, Edit, EditPen, ArrowRight, Search} from '@element-plus/icons-vue';

import {outfitsApi} from '../api/outfits';
import {templatesApi} from '../api/templates';
import {commonApi} from '../api/common';
import type {
  OutfitResponseDto,
  CategoryDto,
  OutfitFilterDto,
  TemplateResponseDto,
  TemplateItemResponseDto
} from '../types';

import OutfitPreview from '../components/outfit/OutfitPreview.vue';
import EditOutfitMetaModal from '../components/EditOutfitMetaModal.vue';

const router = useRouter();

const outfits = ref<OutfitResponseDto[]>([]);
const templates = ref<TemplateResponseDto[]>([]);
const categories = ref<CategoryDto[]>([]);
const loading = ref(true);
const loadingTemplates = ref(false);

const filters = reactive<OutfitFilterDto>({
  searchTerm: '',
  categoryId: undefined
});


const showTemplateDialog = ref(false);
const showDetails = ref(false);
const showMetaEditor = ref(false);

const selectedOutfit = ref<OutfitResponseDto | null>(null);
const editingOutfit = ref<OutfitResponseDto | null>(null);

const loadOutfits = async () => {
  loading.value = true;
  try {
    const res = await outfitsApi.getAll(filters);
    outfits.value = res.data;

    if (selectedOutfit.value) {
      const updated = outfits.value.find(o => o.id === selectedOutfit.value!.id);
      if (updated) selectedOutfit.value = updated;
    }
  } catch (e) {
    console.error(e);
    ElMessage.error('Не удалось загрузить список');
  } finally {
    loading.value = false;
  }
};

const loadTemplates = async () => {
  loadingTemplates.value = true;
  try {
    const res = await templatesApi.getAll();
    templates.value = res.data;
  } catch (e) {
    console.error(e);
    ElMessage.error('Ошибка загрузки шаблонов');
  } finally {
    loadingTemplates.value = false;
  }
};

const loadCategories = async () => {
  try {
    const res = await commonApi.getCategories();
    categories.value = res.data.filter(c => c.isOutfitCategory);
  } catch (e) {
    console.error(e);
  }
};

const selectCategory = (id: string | undefined) => {
  filters.categoryId = id;
  loadOutfits();
};

let timeout: number;
const handleFilterChange = () => {
  clearTimeout(timeout);
  timeout = setTimeout(() => loadOutfits(), 300);
};

const resetFilters = () => {
  filters.searchTerm = '';
  filters.categoryId = undefined;
  loadOutfits();
};

const openTemplateDialog = () => {
  showTemplateDialog.value = true;
  loadTemplates();
};

const createOutfit = (templateId: string | null) => {
  showTemplateDialog.value = false;
  router.push({name: 'outfit-create', query: templateId ? {templateId} : {}});
};

const editTemplate = (templateId: string) => {
  showTemplateDialog.value = false;
  router.push({name: 'outfit-create', query: {templateId, mode: 'edit_template'}});
};

const deleteTemplate = async (id: string) => {
  try {
    await templatesApi.delete(id);
    ElMessage.success('Шаблон удален');
    await loadTemplates();
  } catch (e) {
    ElMessage.error('Ошибка удаления');
  }
};

const getSlotStyle = (item: TemplateItemResponseDto) => {
  const BASE_W = 800;
  const BASE_H = 600;
  const SLOT_BASE_SIZE = 200;

  const widthPct = ((SLOT_BASE_SIZE * item.scale) / BASE_W) * 100;
  const heightPct = ((SLOT_BASE_SIZE * item.scale) / BASE_H) * 100;
  const leftPct = (item.x / BASE_W) * 100;
  const topPct = (item.y / BASE_H) * 100;

  return {
    left: `${leftPct}%`,
    top: `${topPct}%`,
    width: `${widthPct}%`,
    height: `${heightPct}%`,
    transform: `translate(-50%, -50%) rotate(${item.rotation}deg)`
  };
};

const openDetails = (outfit: OutfitResponseDto) => {
  selectedOutfit.value = outfit;
  showDetails.value = true;
};

const editOutfitDirectly = (outfit: OutfitResponseDto) => {
  router.push({name: 'outfit-edit', params: {id: outfit.id}});
};

const openMetaEditor = (outfit: OutfitResponseDto) => {
  editingOutfit.value = outfit;
  showMetaEditor.value = true;
};

const handleOutfitUpdated = () => loadOutfits();

const deleteOutfitById = async (id: string) => {
  try {
    await outfitsApi.delete(id);
    ElMessage.success('Образ удален');
    loadOutfits();
  } catch (e) {
    ElMessage.error('Ошибка удаления');
  }
};

const deleteOutfitFromDetails = async () => {
  if (selectedOutfit.value) {
    await deleteOutfitById(selectedOutfit.value.id);
    showDetails.value = false;
  }
};

onMounted(() => {
  loadCategories();
  loadOutfits();
});
</script>

<style scoped>
.outfits-page {
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

.outfits-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(290px, 1fr));
  gap: 25px;
}

.outfit-card {
  transition: transform 0.2s, box-shadow 0.2s;
  border: 1px solid #ebeef5;
}

.outfit-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.preview-wrapper {
  background-color: #f9fafc;
  border-bottom: 1px solid #ebeef5;
  position: relative;
  height: 220px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  overflow: hidden;
}

.preview-inner {
  max-width: 100%;
  max-height: 100%;
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
}

.preview-wrapper:hover .card-actions {
  opacity: 1;
}

.card-footer {
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.outfit-name {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}

.meta-row {
  display: flex;
  align-items: center;
}

.item-count {
  font-size: 13px;
  color: #909399;
}

.arrow-icon {
  color: #c0c4cc;
}

.details-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.large-preview-container {
  display: flex;
  justify-content: center;
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.details-text {
  padding: 0 10px;
}

.description {
  font-size: 15px;
  line-height: 1.5;
  color: #303133;
}

.text-muted {
  color: #909399;
  font-style: italic;
}

.tags-list {
  margin-top: 10px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.dialog-footer-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.templates-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  max-height: 500px;
  overflow-y: auto;
  padding: 5px;
}

.template-card {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
  background: #fff;
  position: relative;
}

.template-card:hover {
  border-color: #d128a1;
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.template-card.special-card {
  text-align: center;
  padding-top: 20px;
}

.template-preview {
  height: 140px;
  background: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
  position: relative;
  overflow: hidden;
}

.template-preview.blank {
  display: flex;
  align-items: center;
  justify-content: center;
}

.template-preview.css-preview {
  background-image: radial-gradient(#ddd 1px, transparent 1px);
  background-size: 10px 10px;
}

.preview-slot {
  position: absolute;
  border: 1px dashed #909399;
  background-color: rgba(230, 162, 60, 0.2);
  box-sizing: border-box;
}

.template-info {
  padding: 10px;
  text-align: center;
}

.template-info h4 {
  margin: 0 0 5px 0;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.template-info p {
  margin: 0;
  font-size: 12px;
  color: #909399;
  height: 32px;
  overflow: hidden;
  line-height: 16px;
}

.template-actions {
  position: absolute;
  top: 5px;
  right: 5px;
  display: flex;
  gap: 5px;
  opacity: 0;
  transition: opacity 0.2s;
}

.template-card:hover .template-actions {
  opacity: 1;
}

.action-btn {
  background: white;
  border-radius: 50%;
  width: 28px;
  height: 28px;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  cursor: pointer;
  transition: all 0.2s;
}

.edit-btn {
  color: #d128a1;
}

.edit-btn:hover {
  background: #ecf5ff;
}

.delete-btn {
  color: #f56c6c;
}

.delete-btn:hover {
  background: #fef0f0;
}
</style>
