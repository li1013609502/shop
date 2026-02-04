package com.store.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

public class StockOutCreateDto {
  @Valid
  @NotEmpty
  private List<StockOutItemDto> items;

  public List<StockOutItemDto> getItems() {
    return items;
  }

  public void setItems(List<StockOutItemDto> items) {
    this.items = items;
  }
}
