package com.store.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("stock_out_record")
public class StockOutRecordEntity {
  @TableId(type = IdType.AUTO)
  private Long id;
  private String outNo;
  private Long operatorId;
  private BigDecimal totalAmount;
  private BigDecimal totalProfit;
  private LocalDateTime createdAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getOutNo() {
    return outNo;
  }

  public void setOutNo(String outNo) {
    this.outNo = outNo;
  }

  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
