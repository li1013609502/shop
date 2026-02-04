package com.store.service;

import com.store.common.PageResult;
import com.store.dto.StockOutCreateDto;
import com.store.vo.StockOutItemVo;
import com.store.vo.StockOutResultVo;

public interface StockOutService {
  StockOutResultVo createStockOut(StockOutCreateDto dto);

  PageResult<StockOutItemVo> pageItems(int page, int size, String beginDate, String endDate);
}
