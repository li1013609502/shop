package com.store.vo;

import java.math.BigDecimal;

public class RevenueVo {
  private BigDecimal todayRevenue;
  private BigDecimal monthRevenue;

  public RevenueVo() {}

  public RevenueVo(BigDecimal todayRevenue, BigDecimal monthRevenue) {
    this.todayRevenue = todayRevenue;
    this.monthRevenue = monthRevenue;
  }

  public BigDecimal getTodayRevenue() {
    return todayRevenue;
  }

  public void setTodayRevenue(BigDecimal todayRevenue) {
    this.todayRevenue = todayRevenue;
  }

  public BigDecimal getMonthRevenue() {
    return monthRevenue;
  }

  public void setMonthRevenue(BigDecimal monthRevenue) {
    this.monthRevenue = monthRevenue;
  }
}
