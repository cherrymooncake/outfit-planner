<template>
  <el-menu mode="horizontal" :router="true" :ellipsis="false">
    <el-menu-item index="/">
      <img style="width: 40px" src="../assets/logo.svg" alt="Logo" />
      <span style="margin-left: 10px; font-weight: bold">Wardrobe App</span>
    </el-menu-item>

    <div class="flex-grow" />

    <template v-if="authStore.isAuthenticated">
      <el-menu-item index="/wardrobe">Шкаф</el-menu-item>
      <el-menu-item index="/outfits">Образы</el-menu-item>
      <el-sub-menu index="user">
        <template #title>Аккаунт</template>
        <el-menu-item index="/profile">Профиль</el-menu-item>
        <el-menu-item @click="handleLogout">Выход</el-menu-item>
      </el-sub-menu>
    </template>

    <template v-else>
      <el-menu-item index="/login">Вход</el-menu-item>
      <el-menu-item index="/register">Регистрация</el-menu-item>
    </template>
  </el-menu>
</template>

<script setup lang="ts">
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.flex-grow {
  flex-grow: 1;
}
</style>
