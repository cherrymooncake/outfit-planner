<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <template #header>
        <h2>Вход в систему</h2>
      </template>
      <el-form :model="form" label-position="top">
        <el-form-item label="Email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="Пароль">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-button type="primary" class="w-100" @click="onSubmit" :loading="loading">
          Войти
        </el-button>
        <div class="auth-link">
          Нет аккаунта? <router-link to="/register">Зарегистрироваться</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { authApi } from '../api/auth';
import { useAuthStore } from '../stores/auth';
import { ElMessage } from 'element-plus';

const router = useRouter();
const authStore = useAuthStore();

const form = ref({ email: '', password: '' });
const loading = ref(false);

const onSubmit = async () => {
  loading.value = true;
  try {
    const response = await authApi.login(form.value);
    authStore.setTokens(response.data.accessToken, response.data.refreshToken);
    ElMessage.success('Добро пожаловать!');
    router.push('/wardrobe');
  } catch (e) {
    ElMessage.error('Ошибка входа. Проверьте данные.');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  padding-top: 50px;
}
.auth-card {
  width: 400px;
}
.w-100 { width: 100%; }
.auth-link { margin-top: 15px; text-align: center; }
</style>
