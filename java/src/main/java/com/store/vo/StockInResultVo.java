package com.store.vo;

import java.math.BigDecimal;

public class StockInResultVo {
  private String inNo;
  private BigDecimal totalCost;

  public StockInResultVo() {}

  public StockInResultVo(String inNo, BigDecimal totalCost) {
    this.inNo = inNo;
    this.totalCost = totalCost;
  }

  public String getInNo() {
    return inNo;
  }

  public void setInNo(String inNo) {
    this.inNo = inNo;
  }

  public BigDecimal getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(BigDecimal totalCost) {
    this.totalCost = totalCost;
  }
}
