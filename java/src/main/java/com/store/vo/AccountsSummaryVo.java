package com.store.vo;

import java.math.BigDecimal;

public class AccountsSummaryVo {
  private BigDecimal salesAmount;
  private BigDecimal profitAmount;

  public AccountsSummaryVo() {}

  public AccountsSummaryVo(BigDecimal salesAmount, BigDecimal profitAmount) {
    this.salesAmount = salesAmount;
    this.profitAmount = profitAmount;
  }

  public BigDecimal getSalesAmount() {
    return salesAmount;
  }

  public void setSalesAmount(BigDecimal salesAmount) {
    this.salesAmount = salesAmount;
  }

  public BigDecimal getProfitAmount() {
    return profitAmount;
  }

  public void setProfitAmount(BigDecimal profitAmount) {
    this.profitAmount = profitAmount;
  }
}
