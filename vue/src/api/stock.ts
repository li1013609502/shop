import http from "./http";

export interface StockOutItemPayload {
  barcode: string;
  qty: number;
}

export interface StockOutResult {
  outNo: string;
  totalAmount: number;
  totalProfit: number;
}

export const submitStockOut = (items: StockOutItemPayload[]) =>
  http.post<{ code: number; message: string; data: StockOutResult }>(
    "/stock-out",
    { items }
  );

export const submitStockIn = (items: {
  barcode: string;
  qty: number;
  costPrice: number;
  salePrice: number;
}[]) =>
  http.post<{ code: number; message: string; data: { inNo: string; totalCost: number } }>(
    "/stock-in",
    { items }
  );

export const createProductAndStockIn = (payload: {
  barcode: string;
  name: string;
  category: string;
  qty: number;
  costPrice: number;
  salePrice: number;
  status?: number;
}) =>
  http.post<{ code: number; message: string; data: { inNo: string; totalCost: number } }>(
    "/stock-in/create-product",
    payload
  );

export const fetchStockOutItems = (page = 1, size = 10, beginDate = "", endDate = "") =>
  http.get<{
    code: number;
    message: string;
    data: { total: number; records: any[] };
  }>(
    `/stock-out/items?page=${page}&size=${size}&beginDate=${encodeURIComponent(
      beginDate
    )}&endDate=${encodeURIComponent(endDate)}`
  );
