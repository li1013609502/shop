import { defineStore } from "pinia";

import { fetchMe } from "../api/auth";

export interface UserInfo {
  id: number;
  username: string;
  name: string;
  roleId: number;
}

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: localStorage.getItem("token") || "",
    permissions: JSON.parse(localStorage.getItem("permissions") || "[]") as string[],
    user: (JSON.parse(localStorage.getItem("user") || "null") as UserInfo | null),
    loaded: false
  }),
  actions: {
    setAuth(token: string, user: UserInfo, permissions: string[]) {
      this.token = token;
      this.user = user;
      this.permissions = permissions;
      localStorage.setItem("token", token);
      localStorage.setItem("user", JSON.stringify(user));
      localStorage.setItem("permissions", JSON.stringify(permissions));
    },
    clear() {
      this.token = "";
      this.user = null;
      this.permissions = [];
      this.loaded = false;
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      localStorage.removeItem("permissions");
    },
    async refreshMe() {
      if (!this.token) {
        return;
      }
      const { data } = await fetchMe();
      this.user = data.data.user;
      this.permissions = data.data.permissions;
      this.loaded = true;
      localStorage.setItem("user", JSON.stringify(this.user));
      localStorage.setItem("permissions", JSON.stringify(this.permissions));
    }
  }
});
