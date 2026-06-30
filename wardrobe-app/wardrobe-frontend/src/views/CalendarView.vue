<template>
  <el-config-provider :locale="ruLocale">
    <div class="calendar-page">
      <div class="header-row">
        <h1>Календарь образов</h1>
      </div>

      <el-card shadow="never" class="calendar-card" v-loading="loading">
        <el-calendar v-model="selectedDate">
          <template #date-cell="{ data }">
            <div class="custom-cell" @click="goToOotd(data.day)">
              <div class="day-number" :class="{ 'is-today': data.type === 'today' }">
                {{ data.day.split('-')[2] }}
              </div>

              <div v-if="monthData[data.day]" class="outfit-miniature">
                <OutfitPreview
                    :items="monthData[data.day].items"
                    :width="110"
                :canvas-width="monthData[data.day].canvasWidth"
                :canvas-height="monthData[data.day].canvasHeight"
                />
              </div>

              <div v-else class="empty-plus">
                <el-icon><Plus /></el-icon>
              </div>
            </div>
          </template>
        </el-calendar>
      </el-card>
    </div>
  </el-config-provider>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Plus } from '@element-plus/icons-vue';
import { ElConfigProvider } from 'element-plus';
import ruLocale from 'element-plus/es/locale/lang/ru';

import { dailyOutfitsApi } from '../api/dailyOutfits';
import type { OutfitResponseDto } from '../types';
import OutfitPreview from '../components/outfit/OutfitPreview.vue';

const router = useRouter();

const selectedDate = ref(new Date());
const currentYear = ref(selectedDate.value.getFullYear());
const currentMonth = ref(selectedDate.value.getMonth() + 1);

const loading = ref(false);

const monthData = ref<Record<string, OutfitResponseDto>>({});

const fetchMonthData = async () => {
  loading.value = true;
  try {
    const res = await dailyOutfitsApi.getMonth(currentYear.value, currentMonth.value);
    const map: Record<string, OutfitResponseDto> = {};

    res.data.forEach(item => {
      if (item.outfit) {
        map[item.date] = item.outfit;
      }
    });

    monthData.value = map;
  } catch (e) {
    console.error('Ошибка загрузки календаря', e);
  } finally {
    loading.value = false;
  }
};

const goToOotd = (dateStr: string) => {
  router.push(`/ootd?date=${dateStr}`);
};

watch(selectedDate, (newVal) => {
  const year = newVal.getFullYear();
  const month = newVal.getMonth() + 1;

  if (year !== currentYear.value || month !== currentMonth.value) {
    currentYear.value = year;
    currentMonth.value = month;
    fetchMonthData();
  }
});

onMounted(() => {
  fetchMonthData();
});
</script>

<style scoped>
.calendar-page {
  padding-bottom: 40px;
}

.header-row {
  margin-bottom: 20px;
}

.calendar-card {
  border-radius: 12px;
}

:deep(.el-calendar-day) {
  padding: 0 !important;
  height: 140px !important;
}

.custom-cell {
  width: 100%;
  height: 100%;
  padding: 8px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  transition: background-color 0.2s;
}

.custom-cell:hover {
  background-color: #fef4f9;
}

.day-number {
  align-self: flex-start;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  z-index: 2;
}

.day-number.is-today {
  color: #d128a1;
  font-weight: bold;
  background: #fcebf0;
  padding: 2px 6px;
  border-radius: 4px;
}

.outfit-miniature {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  margin-top: 2px;
}

.empty-plus {
  opacity: 0;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #dcdfe6;
  font-size: 24px;
  transition: opacity 0.2s;
}

.custom-cell:hover .empty-plus {
  opacity: 1;
  color: #d128a1;
}
</style>
