<template>
  <div class="login">
    <el-card class="login-card">
      <h2>系统登录</h2>
      <el-form :model="form" class="login-form">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";

import { login } from "../api/auth";
import { useAuthStore } from "../store/auth";

const router = useRouter();
const authStore = useAuthStore();

const form = reactive({
  username: "",
  password: ""
});

const submit = async () => {
  try {
    const { data } = await login({ username: form.username, password: form.password });
    authStore.setAuth(data.data.token, data.data.user, data.data.permissions);
    router.push("/home");
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message || "登录失败");
  }
};
</script>

<style scoped>
.login {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
}

.login-card {
  width: 360px;
}

.login-form {
  margin-top: 16px;
}
</style>
