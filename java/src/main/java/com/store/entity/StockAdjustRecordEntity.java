package com.store.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("stock_adjust_record")
public class StockAdjustRecordEntity {
  @TableId(type = IdType.AUTO)
  private Long id;
  private String adjNo;
  private Long operatorId;
  private Long productId;
  private String barcode;
  private Integer beforeStock;
  private Integer changeStock;
  private Integer afterStock;
  private String reason;
  private LocalDateTime createdAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAdjNo() {
    return adjNo;
  }

  public void setAdjNo(String adjNo) {
    this.adjNo = adjNo;
  }

  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  public Integer getBeforeStock() {
    return beforeStock;
  }

  public void setBeforeStock(Integer beforeStock) {
    this.beforeStock = beforeStock;
  }

  public Integer getChangeStock() {
    return changeStock;
  }

  public void setChangeStock(Integer changeStock) {
    this.changeStock = changeStock;
  }

  public Integer getAfterStock() {
    return afterStock;
  }

  public void setAfterStock(Integer afterStock) {
    this.afterStock = afterStock;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
