<template>
  <div class="home">
    <h1>Добро пожаловать в Wardrobe App</h1>
    <p>Создавайте идеальные образы из своих вещей.</p>
    <el-button type="primary" size="large" @click="$router.push('/login')">Начать</el-button>
  </div>
</template>

<style scoped>
.home { text-align: center; padding-top: 100px; }
</style>
