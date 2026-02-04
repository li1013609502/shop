import http from "./http";

export const fetchRevenue = () =>
  http.get<{ code: number; message: string; data: { todayRevenue: number; monthRevenue: number } }>(
    "/dashboard/revenue"
  );

export const fetchAccountsSummary = (beginDate: string, endDate: string) =>
  http.get<{ code: number; message: string; data: { salesAmount: number; profitAmount: number } }>(
    `/accounts/summary?beginDate=${encodeURIComponent(beginDate)}&endDate=${encodeURIComponent(
      endDate
    )}`
  );
