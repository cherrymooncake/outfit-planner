<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <template #header>
        <h2>Регистрация</h2>
      </template>

      <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
      >
        <el-form-item label="Email" prop="email">
          <el-input v-model="form.email" placeholder="example@mail.com" />
        </el-form-item>

        <el-form-item label="Пароль" prop="password">
          <el-input
              v-model="form.password"
              type="password"
              show-password
              placeholder="Минимум 6 символов"
          />
        </el-form-item>

        <el-form-item label="Подтвердите пароль" prop="confirmPassword">
          <el-input
              v-model="form.confirmPassword"
              type="password"
              show-password
          />
        </el-form-item>

        <el-button type="primary" class="w-100" @click="onSubmit" :loading="loading">
          Зарегистрироваться
        </el-button>

        <div class="auth-link">
          Уже есть аккаунт? <router-link to="/login">Войти</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { authApi } from '../api/auth';
import { ElMessage, type FormInstance } from 'element-plus';

const router = useRouter();
const formRef = ref<FormInstance>();
const loading = ref(false);

const form = reactive({
  email: '',
  password: '',
  confirmPassword: ''
});

const validatePass2 = (rule: any, value: any, callback: any) => {
  if (value !== form.password) {
    callback(new Error('Пароли не совпадают'));
  } else {
    callback();
  }
};

const rules = {
  email: [
    { required: true, message: 'Введите email', trigger: 'blur' },
    { type: 'email', message: 'Некорректный email', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Введите пароль', trigger: 'blur' },
    { min: 6, message: 'Минимум 6 символов', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: 'Повторите пароль', trigger: 'blur' },
    { validator: validatePass2, trigger: 'blur' }
  ]
};

const onSubmit = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        await authApi.register({
          email: form.email,
          password: form.password
        });

        ElMessage.success('Регистрация успешна! Теперь войдите в систему.');
        router.push('/login');
      } catch (e: any) {
        const msg = e.response?.data?.error || 'Ошибка регистрации';
        ElMessage.error(msg);
      } finally {
        loading.value = false;
      }
    }
  });
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
