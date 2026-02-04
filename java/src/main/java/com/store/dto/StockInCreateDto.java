package com.store.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

public class StockInCreateDto {
  @Valid
  @NotEmpty
  private List<StockInItemDto> items;

  public List<StockInItemDto> getItems() {
    return items;
  }

  public void setItems(List<StockInItemDto> items) {
    this.items = items;
  }
}
