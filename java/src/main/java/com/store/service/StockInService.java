package com.store.service;

import com.store.dto.StockInCreateDto;
import com.store.dto.StockInCreateProductDto;
import com.store.vo.StockInResultVo;

public interface StockInService {
  StockInResultVo createStockIn(StockInCreateDto dto);

  StockInResultVo createProductAndStockIn(StockInCreateProductDto dto);
}
