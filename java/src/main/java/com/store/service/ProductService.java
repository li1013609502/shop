package com.store.service;

import com.store.common.PageResult;
import com.store.dto.ProductCreateDto;
import com.store.dto.ProductPriceUpdateDto;
import com.store.dto.StockAdjustDto;
import com.store.vo.ProductVo;

public interface ProductService {
  ProductVo createProduct(ProductCreateDto dto);

  ProductVo getByBarcode(String barcode);

  PageResult<ProductVo> pageProducts(int page, int size, String keyword);

  ProductVo updatePrice(Long id, ProductPriceUpdateDto dto);

  ProductVo adjustStock(Long id, StockAdjustDto dto);
}
