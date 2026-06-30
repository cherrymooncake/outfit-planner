<template>
  <div class="admin-page">
    <h1>Панель администратора</h1>

    <el-tabs v-model="activeTab" @tab-click="handleTabClick">

      <el-tab-pane label="Дашборд" name="dashboard">
        <div v-loading="loadingStats" class="tab-content">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-card shadow="hover">
                <el-statistic title="Всего пользователей" :value="globalStats?.totalUsers || 0" />
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <el-statistic title="Всего вещей" :value="globalStats?.totalItems || 0" />
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <el-statistic title="Всего образов" :value="globalStats?.totalOutfits || 0" />
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <el-statistic title="Размер папки Images" :value="globalStats?.imagesFolderSizeMb || 0" suffix="MB" />
              </el-card>
            </el-col>
          </el-row>

          <h3 class="mt-4">Статус сервисов</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-card shadow="never">
                <div>
                  База данных (PostgreSQL):
                  <el-tag :type="healthStatus?.dbStatus === 'Ok' ? 'success' : 'danger'" class="ml-2">
                    {{ healthStatus?.dbStatus || 'Unknown' }}
                  </el-tag>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="never">
                <div>
                  Сервис Обработки изображений:
                  <el-tag :type="healthStatus?.bgRemovalStatus === 'Ok' ? 'success' : 'danger'" class="ml-2">
                    {{ healthStatus?.bgRemovalStatus || 'Unknown' }}
                  </el-tag>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="never">
                <div>
                  Сервис ИИ-Стилист:
                  <el-tag :type="healthStatus?.aiStylistStatus === 'Ok' ? 'success' : 'danger'" class="ml-2">
                    {{ healthStatus?.aiStylistStatus || 'Unknown' }}
                  </el-tag>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>

      <el-tab-pane label="Пользователи" name="users">
        <div v-loading="loadingUsers" class="tab-content">
          <el-table :data="users" border stripe style="width: 100%">
            <el-table-column prop="email" label="Email" min-width="200" />

            <el-table-column label="Роль" width="120">
              <template #default="scope">
                <el-tag :type="scope.row.role === 'Admin' ? 'warning' : 'info'">
                  {{ scope.row.role }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column label="Регистрация" width="160">
              <template #default="scope">
                {{ formatDate(scope.row.registeredAt) }}
              </template>
            </el-table-column>

            <el-table-column label="Последняя активность" width="160">
              <template #default="scope">
                {{ formatDate(scope.row.lastActiveAt) }}
              </template>
            </el-table-column>

            <el-table-column prop="itemsCount" label="Вещей" width="90" align="center" />
            <el-table-column prop="outfitsCount" label="Образов" width="90" align="center" />

            <el-table-column label="Действия" width="180" align="center">
              <template #default="scope">
                <el-popconfirm
                    v-if="scope.row.email !== authStore.userEmail"
                    :title="`Изменить роль на ${scope.row.role === 'Admin' ? 'User' : 'Admin'}?`"
                    @confirm="toggleRole(scope.row)"
                >
                  <template #reference>
                    <el-button size="small" :type="scope.row.role === 'Admin' ? 'danger' : 'primary'" plain>
                      {{ scope.row.role === 'Admin' ? 'Забрать админ. доступ' : 'Дать админ. доступ' }}
                    </el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>

          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="Резервное копирование" name="backups">
        <div class="tab-content backups-content">
          <el-card shadow="never" class="backup-card">
            <h3>Резервное копирование базы данных</h3>
            <p class="text-muted">Выгрузка таблиц, пользователей и связей в формате .sql.</p>
            <el-button type="primary" :icon="Download" :loading="downloading === 'db'" @click="handleDownload('db')">
              Скачать дамп БД
            </el-button>
          </el-card>

          <el-card shadow="never" class="backup-card">
            <h3>Резервное копирование файлов (Изображения)</h3>
            <p class="text-muted">ZIP-архив папки wwwroot/images с оригинальными и обработанными фото.</p>
            <el-button type="warning" :icon="Download" :loading="downloading === 'files'" @click="handleDownload('files')">
              Скачать архив файлов
            </el-button>
          </el-card>

          <el-card shadow="never" class="backup-card border-danger">
            <h3>Полный бэкап системы</h3>
            <p class="text-muted">ZIP-архив, содержащий и дамп БД, и все изображения.</p>
            <el-button type="danger" :icon="Download" :loading="downloading === 'full'" @click="handleDownload('full')">
              Создать полный бэкап
            </el-button>
          </el-card>
        </div>
      </el-tab-pane>

    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Download } from '@element-plus/icons-vue';
import { adminApi } from '../api/admin';
import { useAuthStore } from '../stores/auth';
import type { HealthStatusDto, GlobalStatsDto, UserStatDto } from '../types';

const authStore = useAuthStore();
const activeTab = ref('dashboard');

const loadingStats = ref(false);
const loadingUsers = ref(false);
const downloading = ref<'db' | 'files' | 'full' | null>(null);

const healthStatus = ref<HealthStatusDto | null>(null);
const globalStats = ref<GlobalStatsDto | null>(null);
const users = ref<UserStatDto[]>([]);

const loadDashboard = async () => {
  loadingStats.value = true;
  try {
    const [healthRes, statsRes] = await Promise.all([
      adminApi.getHealth(),
      adminApi.getGlobalStats()
    ]);
    healthStatus.value = healthRes.data;
    globalStats.value = statsRes.data;
  } catch (e) {
    ElMessage.error('Ошибка загрузки статистики');
  } finally {
    loadingStats.value = false;
  }
};

const loadUsers = async () => {
  loadingUsers.value = true;
  try {
    const res = await adminApi.getUsers();
    users.value = res.data;
  } catch (e) {
    ElMessage.error('Ошибка загрузки пользователей');
  } finally {
    loadingUsers.value = false;
  }
};

const handleTabClick = (tab: any) => {
  if (tab.paneName === 'users' && users.value.length === 0) {
    loadUsers();
  }
};

const toggleRole = async (user: UserStatDto) => {
  const newRole = user.role === 'Admin' ? 'User' : 'Admin';
  try {
    await adminApi.changeRole(user.id, newRole);
    ElMessage.success(`Роль изменена на ${newRole}`);
    await loadUsers();
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || 'Ошибка изменения роли');
  }
};

const handleDownload = async (type: 'db' | 'files' | 'full') => {
  downloading.value = type;
  try {
    await adminApi.downloadBackup(type);
    ElMessage.success('Скачивание началось');
  } catch (e) {
    ElMessage.error('Ошибка при создании бэкапа (возможно нет pg_dump в докере)');
  } finally {
    downloading.value = null;
  }
};

const formatDate = (dateString: string) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('ru-RU', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  });
};

onMounted(() => {
  loadDashboard();
});
</script>

<style scoped>
.admin-page {
  padding-bottom: 40px;
}
.tab-content {
  padding-top: 20px;
}
.mt-4 {
  margin-top: 2rem;
  margin-bottom: 1rem;
}
.ml-2 {
  margin-left: 10px;
}
.text-muted {
  color: #909399;
  font-size: 0.9rem;
  margin-bottom: 15px;
}
.backups-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 800px;
}
.backup-card h3 {
  margin-top: 0;
  margin-bottom: 5px;
}
.border-danger {
  border: 1px solid #fde2e2;
  background-color: #fef0f0;
}
</style>
