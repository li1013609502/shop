package com.store.controller;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.store.common.PageResult;
import com.store.common.Result;
import com.store.dto.StockOutCreateDto;
import com.store.service.StockOutService;
import com.store.vo.StockOutItemVo;
import com.store.vo.StockOutResultVo;

@RestController
@RequestMapping("/api/stock-out")
public class StockOutController {
  private final StockOutService stockOutService;

  public StockOutController(StockOutService stockOutService) {
    this.stockOutService = stockOutService;
  }

  @PostMapping
  @PreAuthorize("hasAuthority('STOCK_OUT')")
  public Result<StockOutResultVo> create(@Valid @RequestBody StockOutCreateDto dto) {
    return Result.ok(stockOutService.createStockOut(dto));
  }

  @GetMapping("/items")
  @PreAuthorize("hasAuthority('ACCOUNT_VIEW')")
  public Result<PageResult<StockOutItemVo>> items(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String beginDate,
      @RequestParam(required = false) String endDate) {
    String safeBegin = (beginDate == null || beginDate.trim().isEmpty()) ? null : beginDate;
    String safeEnd = (endDate == null || endDate.trim().isEmpty()) ? null : endDate;
    return Result.ok(stockOutService.pageItems(page, size, safeBegin, safeEnd));
  }
}
