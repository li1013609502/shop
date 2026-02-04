package com.store.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.store.mapper.StockOutRecordMapper;
import com.store.service.AccountsService;
import com.store.vo.AccountsSummaryVo;

@Service
public class AccountsServiceImpl implements AccountsService {
  private final StockOutRecordMapper stockOutRecordMapper;

  public AccountsServiceImpl(StockOutRecordMapper stockOutRecordMapper) {
    this.stockOutRecordMapper = stockOutRecordMapper;
  }

  @Override
  public AccountsSummaryVo summary(String beginDate, String endDate) {
    BigDecimal sales = stockOutRecordMapper.selectSalesAmount(beginDate, endDate);
    BigDecimal profit = stockOutRecordMapper.selectProfitAmount(beginDate, endDate);
    return new AccountsSummaryVo(sales, profit);
  }
}
