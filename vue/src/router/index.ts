import { createRouter, createWebHistory } from "vue-router";

import Layout from "../layout/Layout.vue";
import HomeView from "../views/HomeView.vue";
import LoginView from "../views/LoginView.vue";
import ForbiddenView from "../views/ForbiddenView.vue";
import InventoryView from "../views/InventoryView.vue";
import StockInView from "../views/StockInView.vue";
import AccountsView from "../views/AccountsView.vue";
import UsersView from "../views/UsersView.vue";
import RolesView from "../views/RolesView.vue";

import { useAuthStore } from "../store/auth";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/login", name: "login", component: LoginView },
    { path: "/403", name: "forbidden", component: ForbiddenView },
    {
      path: "/",
      component: Layout,
      children: [
        { path: "", redirect: "/home" },
        { path: "/home", name: "home", component: HomeView },
        {
          path: "/inventory",
          name: "inventory",
          component: InventoryView,
          meta: { permission: "STOCK_EDIT" }
        },
        {
          path: "/stock-in",
          name: "stock-in",
          component: StockInView,
          meta: { permission: "STOCK_IN" }
        },
        {
          path: "/accounts",
          name: "accounts",
          component: AccountsView,
          meta: { permission: "ACCOUNT_VIEW" }
        },
        {
          path: "/users",
          name: "users",
          component: UsersView,
          meta: { permission: "USER_MGR" }
        },
        {
          path: "/roles",
          name: "roles",
          component: RolesView,
          meta: { permission: "ROLE_MGR" }
        }
      ]
    }
  ]
});

router.beforeEach(async (to, _from, next) => {
  if (to.path === "/login") {
    next();
    return;
  }
  const authStore = useAuthStore();
  if (!authStore.token) {
    next("/login");
    return;
  }
  if (!authStore.loaded) {
    try {
      await authStore.refreshMe();
    } catch (error) {
      authStore.clear();
      next("/login");
      return;
    }
  }
  const permission = to.meta.permission as string | undefined;
  if (permission && !authStore.permissions.includes(permission)) {
    next("/403");
    return;
  }
  next();
});

export default router;
