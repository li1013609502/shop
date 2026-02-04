import http from "./http";

export interface Product {
  id: number;
  barcode: string;
  name: string;
  category: string;
  costPrice: number;
  salePrice: number;
  stock: number;
  status: number;
}

export interface PageResult<T> {
  total: number;
  records: T[];
}

export const fetchProductByBarcode = (barcode: string) =>
  http.get<{ code: number; message: string; data: Product }>(
    `/products/by-barcode?barcode=${barcode}`
  );

export const fetchProducts = (page = 1, size = 10, keyword = "") =>
  http.get<{ code: number; message: string; data: PageResult<Product> }>(
    `/products?page=${page}&size=${size}&keyword=${encodeURIComponent(keyword)}`
  );

export const updateProductPrice = (id: number, salePrice: number) =>
  http.put<{ code: number; message: string; data: Product }>(
    `/products/${id}/price`,
    { salePrice }
  );

export const adjustProductStock = (id: number, changeStock: number, reason: string) =>
  http.put<{ code: number; message: string; data: Product }>(
    `/products/${id}/stock-adjust`,
    { changeStock, reason }
  );

export const createProduct = (payload: {
  barcode: string;
  name: string;
  category: string;
  costPrice: number;
  salePrice: number;
  status?: number;
}) => http.post<{ code: number; message: string; data: Product }>("/products", payload);
