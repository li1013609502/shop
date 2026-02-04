import http from "./http";

export interface UserDetail {
  id: number;
  username: string;
  name: string;
  phone?: string;
  roleId: number;
  status: number;
}

export interface PageResult<T> {
  total: number;
  records: T[];
}

export const fetchUsers = (page = 1, size = 10, keyword = "") =>
  http.get<{ code: number; message: string; data: PageResult<UserDetail> }>(
    `/users?page=${page}&size=${size}&keyword=${encodeURIComponent(keyword)}`
  );

export const createUser = (payload: {
  username: string;
  name: string;
  phone?: string;
  roleId: number;
}) => http.post<{ code: number; message: string; data: UserDetail }>("/users", payload);

export const updateUser = (id: number, payload: { name: string; phone?: string; roleId: number }) =>
  http.put<{ code: number; message: string; data: UserDetail }>(`/users/${id}`, payload);

export const updateUserStatus = (id: number, status: number) =>
  http.put<{ code: number; message: string; data: UserDetail }>(`/users/${id}/status`, { status });

export const resetUserPassword = (id: number) =>
  http.put<{ code: number; message: string; data: null }>(`/users/${id}/reset-password`);

export const deleteUser = (id: number) =>
  http.delete<{ code: number; message: string; data: null }>(`/users/${id}`);
