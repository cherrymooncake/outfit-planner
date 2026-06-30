<script setup lang="ts">
import { RouterView, useRouter, useRoute } from 'vue-router';
import { useAuthStore } from './stores/auth';
import { computed } from 'vue';
import { Guide, Goods, User, Setting, Star, Calendar } from '@element-plus/icons-vue';

const authStore = useAuthStore();
const router = useRouter();
const route = useRoute();

const activeIndex = computed(() => route.path);

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<template>
  <div class="app-layout">
    <header v-if="authStore.isAuthenticated" class="navbar">
      <div class="logo" @click="router.push('/')">Wardrobe App</div>

      <el-menu
          :default-active="activeIndex"
          mode="horizontal"
          router
          class="nav-menu"
          :ellipsis="false"
      >
        <el-menu-item index="/ootd">
          <el-icon><Star /></el-icon>
          Образ дня
        </el-menu-item>

        <el-menu-item index="/calendar">
          <el-icon><Calendar /></el-icon>
          Календарь
        </el-menu-item>

        <el-menu-item index="/wardrobe">
          <el-icon><Goods /></el-icon>
          Гардероб
        </el-menu-item>

        <el-menu-item index="/outfits">
          <el-icon><Guide /></el-icon>
          Образы
        </el-menu-item>

        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          Профиль
        </el-menu-item>

        <el-menu-item index="/admin" v-if="authStore.isAdmin">
          <el-icon><Setting /></el-icon>
          Панель администратора
        </el-menu-item>

      </el-menu>

      <div class="user-actions">
        <el-button @click="handleLogout" type="danger" plain size="small">Выход</el-button>
      </div>
    </header>

    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>


<style>
:root {
  --el-color-primary: #f08bc7 !important;
  --el-color-primary-light-3: #f5aed8 !important;
  --el-color-primary-light-5: #f8c5e3 !important;
  --el-color-primary-light-7: #fbdceb !important;
  --el-color-primary-light-8: #fcebf0 !important;
  --el-color-primary-light-9: #fef4f9 !important;
  --el-color-primary-dark-2: #d128a1 !important;
}

.el-button--primary {
  background-color: #d128a1 !important;
  border-color: #d128a1 !important;
  color: #ffffff !important;

  --el-button-bg-color: #f08bc7 !important;
  --el-button-border-color: #f08bc7 !important;
  --el-button-hover-bg-color: #f5aed8 !important;
  --el-button-hover-border-color: #f5aed8 !important;
  --el-button-active-bg-color: #d128a1 !important;
  --el-button-active-border-color: #d128a1 !important;
}

.el-button--primary:hover,
.el-button--primary:focus {
  background-color: #f5aed8 !important;
  border-color: #f5aed8 !important;
}

.el-button--primary:active {
  background-color: #d128a1 !important;
  border-color: #d128a1 !important;
}

.el-menu-item.is-active {
  color: #f08bc7 !important;
  border-bottom-color: #f08bc7 !important;
  background-color: #fef4f9 !important;
}
</style>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  display: flex;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid #dcdfe6;
  background-color: #fff;
  height: 60px;
}

.logo {
  font-weight: bold;
  font-size: 1.2rem;
  margin-right: 40px;
  color: #d128a1;
  cursor: pointer;
  transition: opacity 0.2s;
}

.el-menu-item.is-active {
  background-color: #fef4f9 !important;
  border-bottom: 2px solid #f08bc7 !important;
  color: #d128a1 !important;
}

.el-menu-item:hover {
  background-color: #fef4f9 !important;
  color: #d128a1 !important;
}

.nav-menu {
  flex: 1;
  border-bottom: none !important;
}

.main-content {
  flex: 1;
  padding: 20px;
  background-color: #f5f7fa;
  box-sizing: border-box;
}

:global(body) {
  margin: 0;
  padding: 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}
</style>
