package com.store.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.store.mapper.StockOutRecordMapper;
import com.store.service.DashboardService;
import com.store.vo.RevenueVo;

@Service
public class DashboardServiceImpl implements DashboardService {
  private final StockOutRecordMapper stockOutRecordMapper;

  public DashboardServiceImpl(StockOutRecordMapper stockOutRecordMapper) {
    this.stockOutRecordMapper = stockOutRecordMapper;
  }

  @Override
  public RevenueVo getRevenue() {
    BigDecimal today = stockOutRecordMapper.selectTodayRevenue();
    BigDecimal month = stockOutRecordMapper.selectMonthRevenue();
    return new RevenueVo(today, month);
  }
}
