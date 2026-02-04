package com.store.vo;

import java.math.BigDecimal;

public class StockOutResultVo {
  private String outNo;
  private BigDecimal totalAmount;
  private BigDecimal totalProfit;

  public StockOutResultVo() {}

  public StockOutResultVo(String outNo, BigDecimal totalAmount, BigDecimal totalProfit) {
    this.outNo = outNo;
    this.totalAmount = totalAmount;
    this.totalProfit = totalProfit;
  }

  public String getOutNo() {
    return outNo;
  }

  public void setOutNo(String outNo) {
    this.outNo = outNo;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public BigDecimal getTotalProfit() {
    return totalProfit;
  }

  public void setTotalProfit(BigDecimal totalProfit) {
    this.totalProfit = totalProfit;
  }
}
