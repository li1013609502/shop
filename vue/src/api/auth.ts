import http from "./http";

export interface LoginPayload {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: {
    id: number;
    username: string;
    name: string;
    roleId: number;
  };
  permissions: string[];
}

export const login = (payload: LoginPayload) =>
  http.post<{ code: number; message: string; data: LoginResponse }>(
    "/auth/login",
    payload
  );

export const fetchMe = () =>
  http.get<{ code: number; message: string; data: { user: LoginResponse["user"]; permissions: string[] } }>(
    "/auth/me"
  );
