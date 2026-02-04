package com.store.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class StockAdjustDto {
  @NotNull
  private Integer changeStock;

  @NotBlank
  private String reason;

  public Integer getChangeStock() {
    return changeStock;
  }

  public void setChangeStock(Integer changeStock) {
    this.changeStock = changeStock;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }
}
