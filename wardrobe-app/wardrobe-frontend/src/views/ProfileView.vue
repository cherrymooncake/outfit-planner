<template>
  <div class="profile-container">
    <h1>Настройки профиля</h1>

    <el-row :gutter="20">
      <el-col :span="12" :xs="24">
        <el-card class="mb-20">
          <template #header>
            <div class="card-header">
              <span>Смена пароля</span>
            </div>
          </template>

          <el-form
              ref="passFormRef"
              :model="passForm"
              :rules="passRules"
              label-position="top"
          >
            <el-form-item label="Текущий пароль" prop="oldPassword">
              <el-input v-model="passForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="Новый пароль" prop="newPassword">
              <el-input v-model="passForm.newPassword" type="password" show-password />
            </el-form-item>

            <el-button type="primary" @click="onChangePassword" :loading="loading">
              Обновить пароль
            </el-button>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="12" :xs="24">
        <el-card class="danger-zone">
          <template #header>
            <div class="card-header danger-text">
              <span>Удаление аккаунта</span>
            </div>
          </template>

          <p>
            Это действие необратимо. Все ваши вещи, образы и данные будут удалены без возможности восстановления.
          </p>

          <el-button type="danger" plain @click="onDeleteAccount">
            Удалить аккаунт навсегда
          </el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { authApi } from '../api/auth';
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';

const authStore = useAuthStore();
const router = useRouter();
const passFormRef = ref<FormInstance>();
const loading = ref(false);

const passForm = reactive({
  oldPassword: '',
  newPassword: ''
});

const passRules = {
  oldPassword: [{ required: true, message: 'Введите старый пароль', trigger: 'blur' }],
  newPassword: [
    { required: true, message: 'Введите новый пароль', trigger: 'blur' },
    { min: 6, message: 'Минимум 6 символов', trigger: 'blur' }
  ]
};

const onChangePassword = async () => {
  if (!passFormRef.value) return;

  await passFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        await authApi.changePassword(passForm);
        ElMessage.success('Пароль успешно изменен. Пожалуйста, войдите снова.');
        authStore.logout();
        router.push('/login');
      } catch (e: any) {
        const msg = e.response?.data?.error || 'Ошибка при смене пароля';
        ElMessage.error(msg);
      } finally {
        loading.value = false;
      }
    }
  });
};

const onDeleteAccount = () => {
  ElMessageBox.confirm(
      'Вы уверены, что хотите удалить аккаунт? Все данные будут потеряны.',
      'Предупреждение',
      {
        confirmButtonText: 'Да, удалить',
        cancelButtonText: 'Отмена',
        type: 'warning',
      }
  )
      .then(async () => {
        try {
          await authApi.deleteAccount();
          ElMessage.success('Аккаунт удален.');
          authStore.logout();
          router.push('/');
        } catch (e) {
          ElMessage.error('Ошибка при удалении аккаунта');
        }
      })
      .catch(() => {
      });
};
</script>

<style scoped>
.profile-container {
  padding-top: 20px;
}
.mb-20 {
  margin-bottom: 20px;
}
.danger-zone {
  border-color: #fde2e2;
}
.danger-text {
  color: #f56c6c;
  font-weight: bold;
}
</style>
