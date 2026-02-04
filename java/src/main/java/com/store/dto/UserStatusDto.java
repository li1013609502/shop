package com.store.dto;

import javax.validation.constraints.NotNull;

public class UserStatusDto {
  @NotNull
  private Integer status;

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }
}
