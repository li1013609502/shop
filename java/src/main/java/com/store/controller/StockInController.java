package com.store.controller;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.common.Result;
import com.store.dto.StockInCreateDto;
import com.store.dto.StockInCreateProductDto;
import com.store.service.StockInService;
import com.store.vo.StockInResultVo;

@RestController
@RequestMapping("/api/stock-in")
public class StockInController {
  private final StockInService stockInService;

  public StockInController(StockInService stockInService) {
    this.stockInService = stockInService;
  }

  @PostMapping
  @PreAuthorize("hasAuthority('STOCK_IN')")
  public Result<StockInResultVo> create(@Valid @RequestBody StockInCreateDto dto) {
    return Result.ok(stockInService.createStockIn(dto));
  }

  @PostMapping("/create-product")
  @PreAuthorize("hasAuthority('STOCK_IN')")
  public Result<StockInResultVo> createProduct(@Valid @RequestBody StockInCreateProductDto dto) {
    return Result.ok(stockInService.createProductAndStockIn(dto));
  }
}
