<template>
  <div class="layout">
    <el-container>
      <el-aside width="200px" class="sidebar">
        <div class="logo">便利店库存</div>
        <el-menu :default-active="activePath" router>
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item v-if="hasPermission('STOCK_IN')" index="/stock-in">入库</el-menu-item>
          <el-menu-item v-if="hasPermission('STOCK_EDIT')" index="/inventory">库存管理</el-menu-item>
          <el-menu-item v-if="hasPermission('ACCOUNT_VIEW')" index="/accounts">账目管理</el-menu-item>
          <el-menu-item v-if="hasPermission('USER_MGR')" index="/users">用户管理</el-menu-item>
          <el-menu-item v-if="hasPermission('ROLE_MGR')" index="/roles">角色管理</el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header class="header">
          <div class="header-left">门店收银台</div>
          <div class="header-right">
            <span class="user-name">{{ authStore.user?.name || "" }}</span>
            <el-button size="small" @click="logout">退出</el-button>
          </div>
        </el-header>
        <el-main class="content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";

import { useAuthStore } from "../store/auth";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const activePath = computed(() => route.path);

const hasPermission = (permission: string) =>
  authStore.permissions.includes(permission);

const logout = () => {
  authStore.clear();
  router.push("/login");
};
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.sidebar {
  background: #f5f7fa;
}

.logo {
  padding: 16px;
  font-weight: 600;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.header-left {
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.content {
  background: #f0f2f5;
  min-height: calc(100vh - 60px);
}

.user-name {
  font-size: 14px;
}
</style>
