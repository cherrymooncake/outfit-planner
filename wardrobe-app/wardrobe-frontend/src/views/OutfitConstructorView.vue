<template>
  <div class="constructor-page">

    <div class="sidebar">
      <div class="sidebar-header">
        <h3>Гардероб</h3>

        <div class="filters-stack">
          <el-input
              v-model="filters.searchTerm"
              placeholder="Поиск..."
              prefix-icon="Search"
              clearable
              size="default"
          />

          <el-select
              v-model="filters.categoryId"
              placeholder="Все категории"
              clearable
              size="default"
              class="filter-select"
          >
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id"/>
          </el-select>

          <el-select
              v-model="filters.tagId"
              placeholder="Все теги"
              clearable
              size="default"
              class="filter-select"
          >
            <el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id"/>
          </el-select>

          <el-button
              v-if="hasActiveFilters"
              type="info"
              link
              size="small"
              @click="resetFilters"
              class="reset-btn"
          >
            Сбросить фильтры
          </el-button>
        </div>
      </div>

      <div class="items-list" v-loading="loading">
        <div
            v-for="item in filteredItems"
            :key="item.id"
            class="draggable-item"
            :class="{ 'is-used': isItemOnCanvas(item.id) }"
            draggable="true"
            @dragstart="onDragStart($event, item)"
            @click="onItemClick(item)"
            title="Нажмите, чтобы добавить (или заменить выделенный слот/вещь)"
        >
          <img :src="getImageUrl(item.processedImageUrl)" draggable="false"/>
          <span class="item-name">{{ item.name }}</span>
          <div v-if="isItemOnCanvas(item.id)" class="used-badge">✔</div>
        </div>

        <el-empty
            v-if="filteredItems.length === 0"
            description="Ничего не найдено"
            :image-size="60"
        />
      </div>
    </div>

    <div class="workspace">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-tooltip content="Назад к списку" placement="bottom" :show-after="500">
            <el-button @click="$router.push('/outfits')" :icon="Back" circle />
          </el-tooltip>

          <div class="title-block">
            <h3>{{ pageTitle }}</h3>
          </div>
        </div>

        <div class="toolbar-center">
          <el-button-group>
            <el-tooltip content="Отменить (Ctrl+Z)" placement="bottom" :show-after="500">
              <el-button size="small" @click="canvasAction('undo')" :disabled="!canUndo">
                <el-icon><RefreshLeft/></el-icon>
              </el-button>
            </el-tooltip>

            <el-tooltip content="Повторить (Ctrl+Y)" placement="bottom" :show-after="500">
              <el-button size="small" @click="canvasAction('redo')" :disabled="!canRedo">
                <el-icon><RefreshRight/></el-icon>
              </el-button>
            </el-tooltip>
          </el-button-group>

          <span class="divider">|</span>

          <el-button-group>
            <el-tooltip content="Переместить назад" placement="bottom" :show-after="500">
              <el-button size="small" @click="canvasAction('sendBackwards')">▼</el-button>
            </el-tooltip>

            <el-tooltip content="Переместить вперед" placement="bottom" :show-after="500">
              <el-button size="small" @click="canvasAction('bringForward')">▲</el-button>
            </el-tooltip>
          </el-button-group>

          <span class="divider">|</span>

          <el-tooltip content="Удалить выделенное (Delete)" placement="bottom" :show-after="200">
            <el-button
                type="danger"
                plain
                size="small"
                :icon="Delete"
                @click="canvasAction('removeActiveObject')"
            />
          </el-tooltip>

          <span class="divider">|</span>

          <span class="label">Размер:</span>
          <el-tooltip content="Ширина холста" placement="bottom">
            <el-input-number
                v-model="canvasSize.width"
                size="small"
                :min="300" :max="2000" :step="50"
                controls-position="right"
                style="width: 85px"
                @change="updateCanvasSize"
            />
          </el-tooltip>
          <span class="x">x</span>
          <el-tooltip content="Высота холста" placement="bottom">
            <el-input-number
                v-model="canvasSize.height"
                size="small"
                :min="300" :max="2000" :step="50"
                controls-position="right"
                style="width: 85px"
                @change="updateCanvasSize"
            />
          </el-tooltip>
        </div>

        <div class="actions">
          <el-button
              @click="openSaveTemplateDialog"
              :type="isTemplateEditMode ? 'primary' : 'default'"
              :plain="!isTemplateEditMode"
          >
            {{ isTemplateEditMode ? 'Сохранить изменения' : 'Сохранить в шаблоны' }}
          </el-button>

          <el-button
              v-if="!isTemplateEditMode"
              type="success"
              @click="openSaveDialog"
          >
            Сохранить образ
          </el-button>
        </div>
      </div>

      <div
          class="canvas-bg"
          @drop.prevent="onDrop"
          @dragover.prevent
      >
        <OutfitCanvas
            ref="outfitCanvasRef"
            @object-modified="triggerUpdate"
            @selection-created="triggerUpdate"
            @selection-cleared="triggerUpdate"
            @items-changed="triggerUpdate"
            @slot-selected="handleSlotSelected"
            @history-changed="handleHistoryChanged"
        />
      </div>
    </div>

    <el-dialog
        v-model="showSaveDialog"
        :title="isEditMode ? 'Обновить образ' : 'Сохранить новый образ'"
        width="500px"
        align-center
    >
      <el-form :model="saveForm" label-position="top" @submit.prevent>
        <el-form-item label="Название" required>
          <el-input v-model="saveForm.name" placeholder="Например: Офис лето" autofocus/>
        </el-form-item>

        <el-form-item label="Описание">
          <el-input v-model="saveForm.description" type="textarea" placeholder="Заметки..."/>
        </el-form-item>

        <el-form-item label="Категории">
          <div class="select-row">
            <el-select
                v-model="saveForm.categoryIds"
                multiple
                placeholder="Выберите категории образа"
                style="flex-grow: 1"
            >
              <el-option
                  v-for="cat in outfitCategories"
                  :key="cat.id"
                  :label="cat.name"
                  :value="cat.id"
              />
            </el-select>
            <el-button :icon="Setting" circle @click="showOutfitCatManager = true"
                       title="Управление категориями образов"/>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showSaveDialog = false">Отмена</el-button>
          <el-button
              type="primary"
              @click="handleSave"
              :loading="isSaving"
              :disabled="!saveForm.name"
          >
            Сохранить
          </el-button>
        </span>
      </template>

      <CategoryManager
          v-model="showOutfitCatManager"
          mode="outfit"
          @change="refreshOutfitCategories"
      />
    </el-dialog>

    <el-dialog
        v-model="showSaveTemplateDialog"
        :title="isTemplateEditMode ? 'Обновить шаблон' : 'Сохранить как шаблон'"
        width="500px"
        align-center
    >
      <div style="margin-bottom: 15px; color: #606266; font-size: 13px;">
        {{
          isTemplateEditMode ? 'Обновление текущей структуры шаблона.' : 'Текущая расстановка вещей будет сохранена как структура (слоты).'
        }}
      </div>

      <el-form :model="saveTemplateForm" label-position="top" @submit.prevent>
        <el-form-item label="Название шаблона" required>
          <el-input v-model="saveTemplateForm.name" placeholder="Например: Flat Lay (Верх + Низ)" autofocus/>
        </el-form-item>

        <el-form-item label="Описание">
          <el-input v-model="saveTemplateForm.description" type="textarea" placeholder="Описание..."/>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showSaveTemplateDialog = false">Отмена</el-button>
          <el-button
              type="primary"
              @click="handleSaveTemplate"
              :loading="isSavingTemplate"
              :disabled="!saveTemplateForm.name"
          >
            {{ isTemplateEditMode ? 'Сохранить изменения' : 'Создать шаблон' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import {ref, computed, onMounted, reactive, nextTick} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {Search, Back, Delete, Setting, RefreshLeft, RefreshRight} from '@element-plus/icons-vue';
import {ElMessage} from 'element-plus';

import {itemsApi} from '../api/items';
import {commonApi} from '../api/common';
import {outfitsApi} from '../api/outfits';
import {templatesApi} from '../api/templates';
import {IMG_BASE_URL} from '../api';
import { dailyOutfitsApi } from '../api/dailyOutfits';
import type {ItemResponseDto, CategoryDto, TagDto, ItemFilterDto, CreateTemplateDto, TemplateItemDto} from '../types';

import OutfitCanvas from '../components/outfit/OutfitCanvas.vue';
import CategoryManager from '../components/CategoryManager.vue';

const route = useRoute();
const router = useRouter();

const isEditMode = computed(() => !!route.params.id);
const sourceTemplateId = computed(() => route.query.templateId as string | undefined);
const isTemplateEditMode = computed(() => !!sourceTemplateId.value && route.query.mode === 'edit_template');

const pageTitle = computed(() => {
  if (isTemplateEditMode.value) return 'Редактирование шаблона';
  if (isEditMode.value) return 'Редактирование образа';
  if (sourceTemplateId.value) return 'Новый по шаблону';
  return 'Новый образ';
});

const items = ref<ItemResponseDto[]>([]);
const categories = ref<CategoryDto[]>([]);
const outfitCategories = ref<CategoryDto[]>([]);
const tags = ref<TagDto[]>([]);
const loading = ref(false);

const filters = reactive<ItemFilterDto>({searchTerm: '', categoryId: undefined, tagId: undefined});

const outfitCanvasRef = ref<InstanceType<typeof OutfitCanvas> | null>(null);
const canvasSize = reactive({width: 800, height: 600});
const itemsOnCanvasIds = ref<Set<string>>(new Set());

const canUndo = ref(false);
const canRedo = ref(false);

const showSaveDialog = ref(false);
const showOutfitCatManager = ref(false);
const isSaving = ref(false);
const saveForm = reactive({name: '', description: '', categoryIds: [] as string[]});

const showSaveTemplateDialog = ref(false);
const isSavingTemplate = ref(false);
const saveTemplateForm = reactive({name: '', description: ''});

const refreshOutfitCategories = async () => {
  try {
    const res = await commonApi.getCategories();
    outfitCategories.value = res.data.filter(c => c.isOutfitCategory);
  } catch (e) {
    console.error(e);
  }
};

const loadData = async () => {
  loading.value = true;
  try {
    const [itemsRes, catRes, tagRes] = await Promise.all([
      itemsApi.getAll(),
      commonApi.getCategories(),
      commonApi.getTags()
    ]);
    items.value = itemsRes.data;
    const allCats = catRes.data;
    categories.value = allCats.filter(c => c.isItemCategory);
    outfitCategories.value = allCats.filter(c => c.isOutfitCategory);
    tags.value = tagRes.data.filter(t => t.isItemTag);
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

const initContent = async () => {
  await nextTick();
  if (!outfitCanvasRef.value) return;

  outfitCanvasRef.value.initCanvas(canvasSize.width, canvasSize.height);

  if (isEditMode.value) {
    try {
      const res = await outfitsApi.getById(route.params.id as string);
      const outfit = res.data;
      saveForm.name = outfit.name;
      saveForm.description = outfit.description || '';
      saveForm.categoryIds = outfit.categories ? outfit.categories.map(c => c.id) : [];

      if (outfit.canvasWidth && outfit.canvasHeight) {
        canvasSize.width = outfit.canvasWidth;
        canvasSize.height = outfit.canvasHeight;
        outfitCanvasRef.value.setSize(canvasSize.width, canvasSize.height);
      }

      await outfitCanvasRef.value.loadItems(outfit.items);
      updateItemsOnCanvasSet();
    } catch (e) {
      ElMessage.error('Ошибка загрузки образа');
    }
    return;
  }

  if (sourceTemplateId.value) {
    try {
      const res = await templatesApi.getById(sourceTemplateId.value);
      const template = res.data;

      await outfitCanvasRef.value.loadTemplate(template.items);

      if (isTemplateEditMode.value) {
        saveTemplateForm.name = template.name;
        saveTemplateForm.description = template.description || '';
        ElMessage.info(`Режим редактирования шаблона "${template.name}"`);
      } else {
        ElMessage.info(`Шаблон "${template.name}" загружен. Выберите слот для заполнения.`);
      }
    } catch (e) {
      ElMessage.error('Ошибка загрузки шаблона');
    }
  }
};

onMounted(async () => {
  await loadData();
  await initContent();
});

const filteredItems = computed(() => {
  return items.value.filter(item => {
    const matchesSearch = !filters.searchTerm || item.name.toLowerCase().includes(filters.searchTerm.toLowerCase());
    const matchesCategory = !filters.categoryId || item.categories.some(c => c.id === filters.categoryId);
    const matchesTag = !filters.tagId || item.tags.some(t => t.id === filters.tagId);
    return matchesSearch && matchesCategory && matchesTag;
  });
});
const hasActiveFilters = computed(() => !!filters.searchTerm || !!filters.categoryId || !!filters.tagId);
const resetFilters = () => {
  filters.searchTerm = '';
  filters.categoryId = undefined;
  filters.tagId = undefined;
};

const isItemOnCanvas = (id: string) => itemsOnCanvasIds.value.has(id);
const updateItemsOnCanvasSet = () => {
  if (outfitCanvasRef.value) {
    const currentItems = outfitCanvasRef.value.getAllItems();
    itemsOnCanvasIds.value = new Set(currentItems.map(i => i.itemId));
  }
};
const triggerUpdate = () => updateItemsOnCanvasSet();
const handleHistoryChanged = (state: { canUndo: boolean, canRedo: boolean }) => {
  canUndo.value = state.canUndo;
  canRedo.value = state.canRedo;
};

const getImageUrl = (path: string) => {
  if (!path) return '';
  const cleanPath = path.startsWith('/') ? path.substring(1) : path;
  return `${IMG_BASE_URL}/${cleanPath}`;
};

const handleSlotSelected = (categoryIdHint: string | undefined) => {
  filters.searchTerm = '';
  filters.tagId = undefined;

  if (categoryIdHint) {
    filters.categoryId = categoryIdHint;
    ElMessage.info({message: 'Фильтр по категории слота', duration: 1000});
  } else {
    filters.categoryId = undefined;
  }
};

const onItemClick = async (item: ItemResponseDto) => {
  if (!outfitCanvasRef.value) return;
  const activeFilledSlot = outfitCanvasRef.value.getActiveFilledSlot();

  if (activeFilledSlot) {
    const fullUrl = getImageUrl(item.processedImageUrl);
    // @ts-ignore
    const oldItemId = activeFilledSlot.data.itemId;

    if (oldItemId === item.id) return;

    await outfitCanvasRef.value.replaceActiveObjectImage(fullUrl, item.id);

    updateItemsOnCanvasSet();
    ElMessage.success('Вещь заменена');
    return;
  }

  const activeSlot = outfitCanvasRef.value.getActiveSlot();
  if (activeSlot) {
    // @ts-ignore
    const {left, top, scaleX, angle, data} = activeSlot;

    outfitCanvasRef.value.removeObject(activeSlot);

    const fullUrl = getImageUrl(item.processedImageUrl);
    await outfitCanvasRef.value.addImage(fullUrl, item.id, {
      left, top, scale: scaleX, rotation: angle,
      isSlotFilled: true,
      categoryIdHint: data.categoryIdHint
    });

    resetFilters();
    ElMessage.success('Слот заполнен');
    setTimeout(() => updateItemsOnCanvasSet(), 50);
    return;
  }

  if (isItemOnCanvas(item.id)) {
    ElMessage.warning('Эта вещь уже на холсте');
    return;
  }
  const fullUrl = getImageUrl(item.processedImageUrl);
  await outfitCanvasRef.value.addImage(fullUrl, item.id, {
    left: canvasSize.width / 2,
    top: canvasSize.height / 2
  });
  setTimeout(() => updateItemsOnCanvasSet(), 50);
};

const onDragStart = (event: DragEvent, item: ItemResponseDto) => {
  if (event.dataTransfer) {
    const fullUrl = getImageUrl(item.processedImageUrl);
    event.dataTransfer.setData('application/json', JSON.stringify({id: item.id, url: fullUrl}));
    event.dataTransfer.effectAllowed = 'copy';
  }
};
const onDrop = (event: DragEvent) => {
  const data = event.dataTransfer?.getData('application/json');
  if (!data || !outfitCanvasRef.value) return;
  try {
    const {id, url} = JSON.parse(data);
    if (outfitCanvasRef.value.hasItem(id)) return;
    outfitCanvasRef.value.addImage(url, id, {left: event.offsetX, top: event.offsetY});
    setTimeout(() => updateItemsOnCanvasSet(), 50);
  } catch (e) {
    console.error(e);
  }
};

const canvasAction = (action: any) => {
  if (outfitCanvasRef.value) outfitCanvasRef.value[action]();
};
const updateCanvasSize = () => {
  if (outfitCanvasRef.value) outfitCanvasRef.value.setSize(canvasSize.width, canvasSize.height);
};

const openSaveDialog = () => {
  if (!checkCanvasNotEmpty()) return;
  showSaveDialog.value = true;
};

const handleSave = async () => {
  if (!outfitCanvasRef.value) return;
  isSaving.value = true;
  try {
    const dataItems = outfitCanvasRef.value.getAllItems();
    const payload = {
      name: saveForm.name,
      description: saveForm.description,
      items: dataItems,
      categoryIds: saveForm.categoryIds,
      canvasWidth: canvasSize.width,
      canvasHeight: canvasSize.height,
      templateId: sourceTemplateId.value || null
    };

    if (isEditMode.value) {
      await outfitsApi.update(route.params.id as string, payload);
      ElMessage.success('Образ обновлен');
      showSaveDialog.value = false;
      router.push('/outfits');
    } else {
      const res = await outfitsApi.create(payload);

      const targetDate = route.query.date as string;
      if (targetDate) {
        await dailyOutfitsApi.setOutfit({ date: targetDate, outfitId: res.data.id });
        ElMessage.success('Образ сохранен на ' + targetDate);
        showSaveDialog.value = false;
        router.push(`/ootd?date=${targetDate}`);
        return;
      } else {
        ElMessage.success('Образ создан');
        showSaveDialog.value = false;
        router.push('/outfits');
      }
    }
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || 'Ошибка при сохранении');
  } finally {
    isSaving.value = false;
  }
};

const openSaveTemplateDialog = () => {
  if (!checkCanvasNotEmpty()) return;
  showSaveTemplateDialog.value = true;
};

const handleSaveTemplate = async () => {
  if (!outfitCanvasRef.value) return;
  isSavingTemplate.value = true;

  try {
    const allObjects = outfitCanvasRef.value.getCanvas()?.getObjects() || [];

    const templateItems: TemplateItemDto[] = allObjects.map(obj => {
      // @ts-ignore
      const data = obj.data || {};

      let categoryId = undefined;

      if (data.isSlot) {
        categoryId = data.categoryIdHint;
      }
      else if (data.itemId) {
        const fullItem = items.value.find(i => i.id === data.itemId);
        categoryId = (fullItem?.categories && fullItem.categories.length > 0) ? fullItem.categories[0].id : undefined;
      }
      else {
        return null;
      }

      return {
        categoryIdHint: categoryId,
        x: obj.left || 0,
        y: obj.top || 0,
        scale: obj.scaleX || 1,
        rotation: obj.angle || 0,
        zIndex: 0
      };
    }).filter(i => i !== null) as TemplateItemDto[];

    const payload: CreateTemplateDto = {
      name: saveTemplateForm.name,
      description: saveTemplateForm.description,
      items: templateItems
    };

    if (isTemplateEditMode.value && sourceTemplateId.value) {
      await templatesApi.update(sourceTemplateId.value, payload);
      ElMessage.success('Шаблон обновлен!');
      router.push('/outfits');
    } else {
      await templatesApi.create(payload);
      ElMessage.success('Шаблон успешно создан!');
      showSaveTemplateDialog.value = false;
    }

  } catch (e: any) {
    console.error(e);
    ElMessage.error(e.response?.data?.error || 'Ошибка при сохранении шаблона');
  } finally {
    isSavingTemplate.value = false;
  }
};

const checkCanvasNotEmpty = () => {
  if (!outfitCanvasRef.value) return false;
  const count = outfitCanvasRef.value.getCanvas()?.getObjects().length || 0;
  if (count === 0) {
    ElMessage.warning('Холст пуст');
    return false;
  }
  return true;
};
</script>

<style scoped>
.constructor-page {
  display: flex;
  height: calc(100vh - 80px);
  margin: -20px;
}

.sidebar {
  width: 280px;
  background: #fff;
  border-right: 1px solid #dcdfe6;
  display: flex;
  flex-direction: column;
  z-index: 2;
}

.sidebar-header {
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
  background: #fff;
}

.filters-stack {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 10px;
}

.filter-select {
  width: 100%;
}

.reset-btn {
  align-self: flex-end;
}

.items-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  align-content: start;
  background-color: #fafafa;
}

.draggable-item {
  cursor: pointer;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #fff;
  transition: all 0.2s;
  user-select: none;
  position: relative;
}

.draggable-item:hover {
  border-color: #d128a1;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.draggable-item:active {
  cursor: grabbing;
}

.draggable-item.is-used {
  opacity: 0.6;
  background-color: #f2f6fc;
  border-color: #e4e7ed;
}

.draggable-item.is-used:hover {
  transform: none;
  box-shadow: none;
}

.used-badge {
  position: absolute;
  top: -6px;
  right: -6px;
  background: #67c23a;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 11px;
  font-weight: bold;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.draggable-item img {
  width: 100%;
  height: 90px;
  object-fit: contain;
  margin-bottom: 5px;
  pointer-events: none;
}

.item-name {
  font-size: 11px;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
  color: #606266;
}

.workspace {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
  min-width: 0;
}

.toolbar {
  height: 50px;
  background: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 15px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-block h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.toolbar-center {
  display: flex;
  align-items: center;
  gap: 10px;
}

.divider {
  color: #dcdfe6;
}

.label {
  font-size: 12px;
  color: #666;
  margin-left: 5px;
}

.x {
  margin: 0 2px;
  color: #999;
}

.actions {
  display: flex;
  gap: 10px;
}

.canvas-bg {
  flex: 1;
  overflow: hidden;
  display: flex;
  justify-content: center;
  padding: 0;
  background-image: radial-gradient(#ccc 1px, transparent 1px);
  background-size: 20px 20px;
}

.select-row {
  display: flex;
  gap: 10px;
  width: 100%;
}
</style>
