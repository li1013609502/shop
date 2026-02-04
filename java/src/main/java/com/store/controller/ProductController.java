package com.store.controller;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.store.common.PageResult;
import com.store.common.Result;
import com.store.dto.ProductCreateDto;
import com.store.dto.ProductPriceUpdateDto;
import com.store.dto.StockAdjustDto;
import com.store.service.ProductService;
import com.store.vo.ProductVo;

@RestController
@RequestMapping("/api/products")
public class ProductController {
  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  @PreAuthorize("hasAuthority('STOCK_IN')")
  public Result<ProductVo> create(@Valid @RequestBody ProductCreateDto dto) {
    return Result.ok(productService.createProduct(dto));
  }

  @GetMapping("/by-barcode")
  @PreAuthorize("hasAnyAuthority('STOCK_OUT','STOCK_IN')")
  public Result<ProductVo> byBarcode(@RequestParam String barcode) {
    return Result.ok(productService.getByBarcode(barcode));
  }

  @GetMapping
  @PreAuthorize("hasAuthority('STOCK_EDIT')")
  public Result<PageResult<ProductVo>> page(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String keyword) {
    return Result.ok(productService.pageProducts(page, size, keyword));
  }

  @PutMapping("/{id}/price")
  @PreAuthorize("hasAuthority('STOCK_EDIT')")
  public Result<ProductVo> updatePrice(
      @PathVariable Long id,
      @Valid @RequestBody ProductPriceUpdateDto dto) {
    return Result.ok(productService.updatePrice(id, dto));
  }

  @PutMapping("/{id}/stock-adjust")
  @PreAuthorize("hasAuthority('STOCK_EDIT')")
  public Result<ProductVo> adjustStock(
      @PathVariable Long id,
      @Valid @RequestBody StockAdjustDto dto) {
    return Result.ok(productService.adjustStock(id, dto));
  }
}
