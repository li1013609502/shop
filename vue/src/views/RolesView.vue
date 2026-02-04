<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <el-button type="success" @click="openCreate">新增角色</el-button>
      </div>
      <el-table :data="roles" style="width: 100%">
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="roleCode" label="角色编码" width="160" />
        <el-table-column label="操作" width="260">
          <template #default="scope">
            <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
            <el-button size="small" @click="openPermissions(scope.row)">权限配置</el-button>
            <el-button size="small" type="danger" @click="removeRole(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="420px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称">
          <el-input v-model="form.roleName" />
        </el-form-item>
        <el-form-item label="编码">
          <el-input v-model="form.roleCode" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRole">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="permissionVisible" title="权限配置" width="480px">
      <el-checkbox-group v-model="permissionForm.permissions">
        <el-checkbox v-for="perm in permissionOptions" :key="perm" :label="perm">
          {{ perm }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="permissionVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPermissions">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";

import {
  createRole,
  deleteRole,
  fetchRolePermissions,
  fetchRoles,
  Role,
  updateRole,
  updateRolePermissions
} from "../api/roles";
import { useAuthStore } from "../store/auth";

const roles = ref<Role[]>([]);
const authStore = useAuthStore();
const dialogVisible = ref(false);
const dialogTitle = ref("新增角色");
const currentId = ref<number | null>(null);

const form = reactive({
  roleName: "",
  roleCode: ""
});

const permissionVisible = ref(false);
const permissionForm = reactive({
  roleId: 0,
  permissions: [] as string[]
});

const permissionOptions = [
  "STOCK_IN",
  "STOCK_OUT",
  "STOCK_EDIT",
  "ACCOUNT_VIEW",
  "USER_MGR",
  "ROLE_MGR"
];

const load = async () => {
  const { data } = await fetchRoles();
  roles.value = data.data;
};

const openCreate = () => {
  dialogTitle.value = "新增角色";
  currentId.value = null;
  form.roleName = "";
  form.roleCode = "";
  dialogVisible.value = true;
};

const openEdit = (role: Role) => {
  dialogTitle.value = "编辑角色";
  currentId.value = role.id;
  form.roleName = role.roleName;
  form.roleCode = role.roleCode;
  dialogVisible.value = true;
};

const submitRole = async () => {
  if (currentId.value) {
    await updateRole(currentId.value, {
      roleName: form.roleName,
      roleCode: form.roleCode
    });
    ElMessage.success("角色已更新");
  } else {
    await createRole({ roleName: form.roleName, roleCode: form.roleCode });
    ElMessage.success("角色已创建");
  }
  dialogVisible.value = false;
  load();
};

const openPermissions = async (role: Role) => {
  const { data } = await fetchRolePermissions(role.id);
  permissionForm.roleId = role.id;
  permissionForm.permissions = data.data || [];
  permissionVisible.value = true;
};

const submitPermissions = async () => {
  await updateRolePermissions(permissionForm.roleId, permissionForm.permissions);
  ElMessage.success("权限已更新");
  permissionVisible.value = false;
  await authStore.refreshMe();
};

const removeRole = async (role: Role) => {
  await deleteRole(role.id);
  ElMessage.success("角色已删除");
  load();
};

onMounted(load);
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
</style>
