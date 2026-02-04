import http from "./http";

export interface Role {
  id: number;
  roleName: string;
  roleCode: string;
}

export const fetchRoles = () =>
  http.get<{ code: number; message: string; data: Role[] }>("/roles");

export const createRole = (payload: { roleName: string; roleCode: string }) =>
  http.post<{ code: number; message: string; data: Role }>("/roles", payload);

export const updateRole = (id: number, payload: { roleName: string; roleCode: string }) =>
  http.put<{ code: number; message: string; data: Role }>(`/roles/${id}`, payload);

export const deleteRole = (id: number) =>
  http.delete<{ code: number; message: string; data: null }>(`/roles/${id}`);

export const fetchRolePermissions = (id: number) =>
  http.get<{ code: number; message: string; data: string[] }>(`/roles/${id}/permissions`);

export const updateRolePermissions = (id: number, permissions: string[]) =>
  http.put<{ code: number; message: string; data: null }>(`/roles/${id}/permissions`, {
    permissions
  });
