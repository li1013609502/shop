<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="用户名/姓名" class="search-input" />
        <el-button type="primary" @click="load">查询</el-button>
        <el-button type="success" @click="openCreate">新增用户</el-button>
      </div>

      <el-table :data="users" style="width: 100%">
        <el-table-column prop="username" label="用户名" width="140" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="roleId" label="角色" width="160">
          <template #default="scope">
            {{ roleName(scope.row.roleId) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            {{ scope.row.status === 1 ? "启用" : "禁用" }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260">
          <template #default="scope">
            <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
            <el-button size="small" @click="toggleStatus(scope.row)">
              {{ scope.row.status === 1 ? "禁用" : "启用" }}
            </el-button>
            <el-button size="small" @click="resetPassword(scope.row)">重置密码</el-button>
            <el-button size="small" type="danger" @click="removeUser(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="prev, pager, next"
          @current-change="load"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="420px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="用户名" v-if="isCreate">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleId" placeholder="选择角色" style="width: 100%">
            <el-option
              v-for="role in roles"
              :key="role.id"
              :label="role.roleName"
              :value="role.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";

import {
  createUser,
  deleteUser,
  fetchUsers,
  resetUserPassword,
  updateUser,
  updateUserStatus,
  UserDetail
} from "../api/users";
import { fetchRoles, Role } from "../api/roles";

const keyword = ref("");
const page = ref(1);
const size = ref(10);
const total = ref(0);
const users = ref<UserDetail[]>([]);
const roles = ref<Role[]>([]);

const dialogVisible = ref(false);
const dialogTitle = ref("新增用户");
const isCreate = ref(true);
const currentId = ref<number | null>(null);

const form = reactive({
  username: "",
  name: "",
  phone: "",
  roleId: 1
});

const load = async () => {
  const { data } = await fetchUsers(page.value, size.value, keyword.value);
  users.value = data.data.records;
  total.value = data.data.total;
};

const loadRoles = async () => {
  const { data } = await fetchRoles();
  roles.value = data.data;
};

const openCreate = () => {
  dialogTitle.value = "新增用户";
  isCreate.value = true;
  currentId.value = null;
  form.username = "";
  form.name = "";
  form.phone = "";
  form.roleId = roles.value[0]?.id || 1;
  dialogVisible.value = true;
};

const openEdit = (row: UserDetail) => {
  dialogTitle.value = "编辑用户";
  isCreate.value = false;
  currentId.value = row.id;
  form.username = row.username;
  form.name = row.name;
  form.phone = row.phone || "";
  form.roleId = row.roleId;
  dialogVisible.value = true;
};

const submitForm = async () => {
  if (isCreate.value) {
    await createUser({
      username: form.username,
      name: form.name,
      phone: form.phone,
      roleId: form.roleId
    });
    ElMessage.success("用户已创建，默认密码 123456");
  } else if (currentId.value) {
    await updateUser(currentId.value, {
      name: form.name,
      phone: form.phone,
      roleId: form.roleId
    });
    ElMessage.success("用户已更新");
  }
  dialogVisible.value = false;
  load();
};

const toggleStatus = async (row: UserDetail) => {
  const nextStatus = row.status === 1 ? 0 : 1;
  await updateUserStatus(row.id, nextStatus);
  ElMessage.success("状态已更新");
  load();
};

const resetPassword = async (row: UserDetail) => {
  await resetUserPassword(row.id);
  ElMessage.success("密码已重置为 123456");
};

const removeUser = async (row: UserDetail) => {
  await deleteUser(row.id);
  ElMessage.success("用户已删除");
  load();
};

const roleName = (roleId: number) => {
  const role = roles.value.find((item) => item.id === roleId);
  return role ? role.roleName : String(roleId);
};

onMounted(() => {
  loadRoles();
  load();
});
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.search-input {
  max-width: 240px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
