<template>
  <div class="ootd-page">
    <div class="header-block">
      <h1>{{ displayDate }}</h1>
      <p class="subtitle" v-if="isToday">Что наденем сегодня?</p>
    </div>

    <div v-if="isToday" class="weather-widget-container">
      <el-card shadow="never" class="weather-card" v-loading="loadingWeather">
        <div v-if="weather" class="weather-content">
          <div class="weather-city-row">
            <el-icon><Location /></el-icon>
            <span style="font-weight: bold; margin-left: 5px;">{{ weather.city }}</span>
            <el-button type="primary" link size="small" style="margin-left: auto;" @click="openCityDialog">Изменить</el-button>
          </div>
          <div class="weather-main">
            <el-icon :size="32" :color="getWeatherColor(weather.condition)">
              <component :is="getWeatherIcon(weather.condition)" />
            </el-icon>
            <span class="temp">{{ Math.round(weather.temperature) }}°C</span>
            <span class="condition">{{ weather.condition }}</span>
          </div>
          <div class="weather-recommendation">
            <span>💡 {{ weather.recommendation }}</span>
          </div>
        </div>
        <div v-else-if="weatherError" class="weather-error">
          <span class="text-muted">{{ weatherError }}</span>
          <el-button size="small" type="primary" plain @click="openCityDialog" style="margin-left: 10px;">
            Указать город
          </el-button>
        </div>
        <div v-else class="weather-error">
          <el-button type="primary" plain @click="openCityDialog" :icon="Location">
            Указать город для прогноза
          </el-button>
        </div>
      </el-card>
    </div>

    <div v-loading="loading" class="content-block">
      <div v-if="dailyOutfit" class="outfit-showcase">
        <el-card class="showcase-card" shadow="hover" :body-style="{ padding: '0px' }">
          <div class="preview-wrapper">
            <OutfitPreview
                :items="dailyOutfit.items"
                :width="400"
                :canvas-width="dailyOutfit.canvasWidth"
                :canvas-height="dailyOutfit.canvasHeight"
            />
          </div>
          <div class="card-footer">
            <h2 class="outfit-name">{{ dailyOutfit.name }}</h2>
            <div class="actions-row">
              <el-button type="primary" plain @click="showSelectModal = true">Изменить</el-button>
              <el-popconfirm title="Очистить образ на этот день?" @confirm="deleteOutfit">
                <template #reference>
                  <el-button type="danger" plain>Сбросить</el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </el-card>
      </div>

      <div v-else class="empty-state">
        <el-empty description="Образ на этот день не запланирован">
          <div class="empty-actions">

            <el-button class="action-btn btn-ai" @click="showAiDialog = true">
              <el-icon><MagicStick /></el-icon>
              <span style="margin-left: 8px;">Не знаете что надеть?</span>
            </el-button>

            <el-button class="action-btn btn-wardrobe" @click="showSelectModal = true">
              Выбрать из шкафа
            </el-button>

            <el-button class="action-btn btn-random" @click="setRandomOutfit">
              Случайный образ
            </el-button>

            <el-button class="action-btn btn-create" @click="createNewOutfit">
              Создать новый
            </el-button>

          </div>
        </el-empty>
      </div>
    </div>

    <el-dialog v-model="showSelectModal" title="Выберите образ" width="800px" align-center>
      <div v-loading="loadingOutfits" class="select-grid">
        <el-card
            v-for="outfit in allOutfits"
            :key="outfit.id"
            class="select-card"
            shadow="hover"
            @click="selectOutfit(outfit.id)"
        >
          <OutfitPreview
              :items="outfit.items"
              :width="150"
              :canvas-width="outfit.canvasWidth"
              :canvas-height="outfit.canvasHeight"
          />
          <p class="select-name">{{ outfit.name }}</p>
        </el-card>
      </div>
      <el-empty v-if="!loadingOutfits && allOutfits.length === 0" description="Нет сохраненных образов" />
    </el-dialog>

    <el-dialog v-model="showCityDialog" title="Укажите город" width="400px" align-center>
      <el-autocomplete
          v-model="tempCityName"
          :fetch-suggestions="querySearchCity"
          placeholder="Начните вводить название..."
          value-key="name"
          @select="handleCitySelect"
          style="width: 100%"
          :trigger-on-focus="false"
          clearable
      >
        <template #default="{ item }">
          <div style="line-height: 1.4; padding: 4px 0;">
            <div style="font-weight: bold; color: #303133;">{{ item.name }}</div>
            <div style="font-size: 12px; color: #909399;">
              <span v-if="item.admin1">{{ item.admin1 }}, </span>{{ item.country }}
            </div>
          </div>
        </template>
      </el-autocomplete>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCityDialog = false">Отмена</el-button>
          <el-button type="primary" @click="saveCity" :disabled="!tempCityName.trim()">
            Сохранить
          </el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog
        v-model="showAiDialog"
        title="ИИ-Стилист"
        width="500px"
        align-center
        @closed="handleAiDialogClose"
    >
      <div v-loading="isAiLoading" class="ai-dialog-body">
        <template v-if="!aiResult">
          <p class="ai-hint">
            Опишите, куда вы собираетесь или какое у вас настроение. Я проанализирую ваш шкаф и погоду!
          </p>
          <el-input
              v-model="aiPrompt"
              type="textarea"
              :rows="3"
              placeholder="Например: Хочу что-то удобное для долгой прогулки в парке"
              @keyup.enter.exact.prevent="askAi"
          />
          <el-button
              type="primary"
              class="ai-submit-btn"
              :loading="isAiLoading"
              :disabled="!aiPrompt.trim()"
              @click="askAi"
          >
            Подобрать образ
          </el-button>
        </template>

        <template v-else>
          <div class="ai-result-view">
            <p class="ai-explanation">✨ {{ aiResult.explanation }}</p>

            <div class="ai-preview-box">
              <OutfitPreview
                  :items="aiResult.outfit.items"
                  :width="250"
                  :canvas-width="aiResult.outfit.canvasWidth"
                  :canvas-height="aiResult.outfit.canvasHeight"
              />
            </div>

            <div class="ai-actions">
              <el-button type="primary" @click="applyAiOutfit">Надеть это</el-button>
              <el-button @click="aiResult = null">Другой запрос</el-button>
            </div>
          </div>
        </template>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { dailyOutfitsApi } from '../api/dailyOutfits';
import { outfitsApi } from '../api/outfits';
import { weatherApi } from '../api/weather';
import type { OutfitResponseDto, WeatherResponseDto, AiRecommendationResponseDto } from '../types';
import OutfitPreview from '../components/outfit/OutfitPreview.vue';
import { Location, Sunny, PartlyCloudy, Pouring, Lightning, MagicStick } from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const dailyOutfit = ref<OutfitResponseDto | null>(null);

const weather = ref<WeatherResponseDto | null>(null);
const loadingWeather = ref(false);
const weatherError = ref('');

const showSelectModal = ref(false);
const loadingOutfits = ref(false);
const allOutfits = ref<OutfitResponseDto[]>([]);

const showAiDialog = ref(false);
const aiPrompt = ref('');
const isAiLoading = ref(false);
const aiResult = ref<AiRecommendationResponseDto | null>(null);

const getTodayStr = () => {
  const d = new Date();
  d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
  return d.toISOString().split('T')[0];
};

const currentDate = computed(() => {
  return (route.query.date as string) || getTodayStr();
});

const isToday = computed(() => currentDate.value === getTodayStr());

const displayDate = computed(() => {
  const d = new Date(currentDate.value);
  const options: Intl.DateTimeFormatOptions = { weekday: 'long', month: 'long', day: 'numeric' };
  let str = d.toLocaleDateString('ru-RU', options);
  return str.charAt(0).toUpperCase() + str.slice(1);
});

const loadWeather = async () => {
  if (!isToday.value) {
    weather.value = null;
    return;
  }

  const savedCity = localStorage.getItem('userCity');
  if (!savedCity) {
    weatherError.value = '';
    return;
  }

  loadingWeather.value = true;
  weatherError.value = '';

  try {
    const res = await weatherApi.getWeather(savedCity);
    weather.value = res.data;
    localStorage.setItem('userCity', res.data.city);
  } catch (e: any) {
    weather.value = null;
    weatherError.value = e.response?.data?.error || 'Сервис погоды временно недоступен';
  } finally {
    loadingWeather.value = false;
  }
};

const showCityDialog = ref(false);
const tempCityName = ref('');

const openCityDialog = () => {
  tempCityName.value = localStorage.getItem('userCity') || '';
  showCityDialog.value = true;
};

const querySearchCity = async (queryString: string, cb: (data: any[]) => void) => {
  if (queryString.length < 2) {
    cb([]);
    return;
  }

  try {
    const response = await fetch(
        `https://geocoding-api.open-meteo.com/v1/search?name=${encodeURIComponent(queryString)}&count=5&language=ru&format=json`
    );
    const data = await response.json();
    cb(data.results || []);
  } catch (error) {
    console.error("Ошибка геокодирования:", error);
    cb([]);
  }
};

const handleCitySelect = (item: any) => {
  tempCityName.value = item.name;
};

const saveCity = () => {
  if (tempCityName.value.trim()) {
    localStorage.setItem('userCity', tempCityName.value.trim());
    showCityDialog.value = false;
    loadWeather();
  }
};

const getWeatherIcon = (condition: string) => {
  if (condition === 'Ясно') return Sunny;
  if (condition === 'Облачно' || condition === 'Туман') return PartlyCloudy;
  if (condition === 'Дождь') return Pouring;
  if (condition === 'Гроза') return Lightning;
  return PartlyCloudy;
};

const getWeatherColor = (condition: string) => {
  if (condition === 'Ясно') return '#E6A23C';
  if (condition === 'Дождь' || condition === 'Гроза') return '#409EFF';
  if (condition === 'Снег') return '#A0CFFF';
  return '#909399';
};

const askAi = async () => {
  if (!aiPrompt.value.trim()) return;

  isAiLoading.value = true;
  try {
    let weatherStr = undefined;
    if (weather.value) {
      weatherStr = `${Math.round(weather.value.temperature)}°C, ${weather.value.condition}`;
    }

    const res = await dailyOutfitsApi.getAiRecommendation({
      prompt: aiPrompt.value,
      weatherContext: weatherStr
    });

    aiResult.value = res.data;
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || 'Ошибка при обращении к ИИ-стилисту');
  } finally {
    isAiLoading.value = false;
  }
};

const applyAiOutfit = async () => {
  if (aiResult.value) {
    await selectOutfit(aiResult.value.recommendedOutfitId);
    showAiDialog.value = false;
  }
};

const handleAiDialogClose = () => {
  aiResult.value = null;
  aiPrompt.value = '';
};

const loadDailyOutfit = async () => {
  loading.value = true;
  try {
    const res = await dailyOutfitsApi.getByDate(currentDate.value);
    if (res.data && res.data.outfit) {
      dailyOutfit.value = res.data.outfit;
    } else {
      dailyOutfit.value = null;
    }
  } catch (e) {
    dailyOutfit.value = null;
  } finally {
    loading.value = false;
  }
};

const loadAllOutfits = async () => {
  loadingOutfits.value = true;
  try {
    const res = await outfitsApi.getAll();
    allOutfits.value = res.data;
  } catch (e) {
    ElMessage.error('Не удалось загрузить образы');
  } finally {
    loadingOutfits.value = false;
  }
};

const selectOutfit = async (outfitId: string) => {
  try {
    await dailyOutfitsApi.setOutfit({ date: currentDate.value, outfitId });
    ElMessage.success('Образ установлен!');
    showSelectModal.value = false;
    await loadDailyOutfit();
  } catch (e) {
    ElMessage.error('Ошибка сохранения');
  }
};

const setRandomOutfit = async () => {
  try {
    const res = await dailyOutfitsApi.getRandomOutfit();
    if (res.data) {
      await dailyOutfitsApi.setOutfit({ date: currentDate.value, outfitId: res.data.id });
      ElMessage.success('Случайный образ подобран!');
      await loadDailyOutfit();
    }
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || 'У вас еще нет сохраненных образов');
  }
};

const deleteOutfit = async () => {
  try {
    await dailyOutfitsApi.deleteOutfit(currentDate.value);
    dailyOutfit.value = null;
    ElMessage.info('Образ убран с этого дня');
  } catch (e) {
    ElMessage.error('Ошибка удаления');
  }
};

const createNewOutfit = () => {
  router.push(`/outfits/create?date=${currentDate.value}`);
};

watch(showSelectModal, (val) => {
  if (val && allOutfits.value.length === 0) loadAllOutfits();
});

watch(() => route.query.date, () => {
  loadDailyOutfit();
  loadWeather();
});

onMounted(() => {
  loadDailyOutfit();
  loadWeather();
});
</script>

<style scoped>
.ootd-page {
  max-width: 800px;
  margin: 0 auto;
  padding-top: 20px;
  text-align: center;
}

.header-block h1 {
  margin-bottom: 5px;
  color: #303133;
}
.subtitle {
  color: #909399;
  font-size: 18px;
  margin-top: 0;
}

.weather-widget-container {
  display: flex;
  justify-content: center;
  margin-top: 15px;
}

.weather-card {
  border-radius: 12px;
  background-color: #fef4f9;
  border: 1px solid #fbdceb;
  min-width: 300px;
  max-width: 500px;
  padding: 0;
}

.weather-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.weather-main {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
}

.weather-main .temp {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.weather-main .condition {
  font-size: 18px;
  color: #606266;
}

.weather-recommendation {
  font-size: 14px;
  color: #d128a1;
  font-weight: 500;
  line-height: 1.4;
}

.weather-error {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 40px;
}

.text-muted {
  color: #909399;
  font-size: 14px;
}

.content-block {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.outfit-showcase {
  width: 100%;
  max-width: 500px;
}

.showcase-card {
  border-radius: 16px;
  overflow: hidden;
}

.preview-wrapper {
  background-color: #f5f7fa;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.card-footer {
  padding: 20px;
  background: white;
}

.outfit-name {
  margin: 0 0 20px 0;
  font-size: 22px;
}

.actions-row {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.empty-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
  margin-top: 20px;
}

.action-btn {
  width: 280px;
  height: 46px;
  margin-left: 0 !important;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  border-width: 1px;
  border-style: solid;
  transition: all 0.25s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.btn-ai {
  background-color: #d8cceb !important;
  border-color: #7b52ab !important;
}
.btn-ai:hover {
  background-color: #f0ebf8 !important;
  border-color: #7b52ab !important;
}

.btn-wardrobe {
  background-color: #fbdceb !important;
  border-color: #d128a1 !important;
}
.btn-wardrobe:hover {
  background-color: #fcebf0 !important;
  border-color: #d128a1 !important;
}

.btn-random {
  background-color: #f9e2c5 !important;
  border-color: #e6a23c !important;
}
.btn-random:hover {
  background-color: #fdf0df !important;
  border-color: #e6a23c !important;
}

.btn-create {
  background-color: #e1f3d8  !important;
  border-color: #67c23a  !important;
}
.btn-create:hover {
  background-color: #ecf0f5 !important;
  border-color: #909399 !important;
  color: #303133 !important;
}

.select-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 15px;
  max-height: 60vh;
  overflow-y: auto;
  padding: 5px;
}

.select-card {
  cursor: pointer;
  text-align: center;
  transition: transform 0.2s;
}
.select-card:hover {
  transform: scale(1.05);
  border-color: #d128a1;
}

.select-name {
  margin: 10px 0 0 0;
  font-size: 14px;
  font-weight: bold;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ai-dialog-body {
  min-height: 150px;
}
.ai-hint {
  margin-top: 0;
  color: #606266;
  font-size: 14px;
  margin-bottom: 15px;
}
.ai-submit-btn {
  margin-top: 15px;
  width: 100%;
}
.ai-result-view {
  text-align: center;
}
.ai-explanation {
  font-size: 15px;
  font-weight: bold;
  color: #d128a1;
  margin-bottom: 16px;
  line-height: 1.4;
}
.ai-preview-box {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
  border: 1px solid #ebeef5;
}
.ai-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
}
.weather-city-row {
  display: flex;
}
</style>
