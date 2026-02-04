import axios from "axios";
import { ElMessage } from "element-plus";

const http = axios.create({
  baseURL: "/api",
  timeout: 10000
});

http.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status;
    if (status === 401) {
      localStorage.removeItem("token");
      localStorage.removeItem("permissions");
      localStorage.removeItem("user");
      window.location.href = "/login";
    }
    if (status === 403) {
      ElMessage.error("无权限");
      window.location.href = "/403";
    }
    return Promise.reject(error);
  }
);

export default http;
