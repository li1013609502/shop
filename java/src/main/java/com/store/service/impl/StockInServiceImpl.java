package com.store.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.store.common.BizException;
import com.store.config.UserSession;
import com.store.dto.StockInCreateDto;
import com.store.dto.StockInCreateProductDto;
import com.store.dto.StockInItemDto;
import com.store.entity.ProductEntity;
import com.store.entity.StockInItemEntity;
import com.store.entity.StockInRecordEntity;
import com.store.mapper.ProductMapper;
import com.store.mapper.StockInItemMapper;
import com.store.mapper.StockInRecordMapper;
import com.store.service.StockInService;
import com.store.vo.StockInResultVo;

@Service
public class StockInServiceImpl implements StockInService {
  private final ProductMapper productMapper;
  private final StockInRecordMapper stockInRecordMapper;
  private final StockInItemMapper stockInItemMapper;

  public StockInServiceImpl(
      ProductMapper productMapper,
      StockInRecordMapper stockInRecordMapper,
      StockInItemMapper stockInItemMapper) {
    this.productMapper = productMapper;
    this.stockInRecordMapper = stockInRecordMapper;
    this.stockInItemMapper = stockInItemMapper;
  }

  @Override
  @Transactional
  public StockInResultVo createStockIn(StockInCreateDto dto) {
    UserSession session = currentSession();
    String inNo = generateInNo();
    BigDecimal totalCost = BigDecimal.ZERO;
    List<StockInItemEntity> items = new ArrayList<>();

    for (StockInItemDto itemDto : dto.getItems()) {
      QueryWrapper<ProductEntity> wrapper = new QueryWrapper<>();
      wrapper.eq("barcode", itemDto.getBarcode());
      ProductEntity product = productMapper.selectOne(wrapper);
      if (product == null) {
        throw new BizException(2001, "商品不存在");
      }
      product.setStock(product.getStock() + itemDto.getQty());
      product.setCostPrice(itemDto.getCostPrice());
      product.setSalePrice(itemDto.getSalePrice());
      productMapper.updateById(product);

      BigDecimal amount = itemDto.getCostPrice().multiply(BigDecimal.valueOf(itemDto.getQty()));

      StockInItemEntity item = new StockInItemEntity();
      item.setProductId(product.getId());
      item.setBarcode(product.getBarcode());
      item.setQty(itemDto.getQty());
      item.setCostPrice(itemDto.getCostPrice());
      item.setSalePrice(itemDto.getSalePrice());
      item.setAmount(amount);
      items.add(item);

      totalCost = totalCost.add(amount);
    }

    StockInRecordEntity record = new StockInRecordEntity();
    record.setInNo(inNo);
    record.setOperatorId(session.getId());
    record.setTotalCost(totalCost);
    record.setCreatedAt(LocalDateTime.now());
    stockInRecordMapper.insert(record);

    for (StockInItemEntity item : items) {
      item.setRecordId(record.getId());
      stockInItemMapper.insert(item);
    }

    return new StockInResultVo(inNo, totalCost);
  }

  @Override
  @Transactional
  public StockInResultVo createProductAndStockIn(StockInCreateProductDto dto) {
    UserSession session = currentSession();
    QueryWrapper<ProductEntity> wrapper = new QueryWrapper<>();
    wrapper.eq("barcode", dto.getBarcode());
    if (productMapper.selectCount(wrapper) > 0) {
      throw new BizException(1001, "商品已存在");
    }
    ProductEntity product = new ProductEntity();
    product.setBarcode(dto.getBarcode());
    product.setName(dto.getName());
    product.setCategory(dto.getCategory());
    product.setCostPrice(dto.getCostPrice());
    product.setSalePrice(dto.getSalePrice());
    product.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
    product.setStock(0);
    productMapper.insert(product);

    product.setStock(product.getStock() + dto.getQty());
    product.setCostPrice(dto.getCostPrice());
    product.setSalePrice(dto.getSalePrice());
    productMapper.updateById(product);

    BigDecimal amount = dto.getCostPrice().multiply(BigDecimal.valueOf(dto.getQty()));

    StockInItemEntity item = new StockInItemEntity();
    item.setProductId(product.getId());
    item.setBarcode(product.getBarcode());
    item.setQty(dto.getQty());
    item.setCostPrice(dto.getCostPrice());
    item.setSalePrice(dto.getSalePrice());
    item.setAmount(amount);

    String inNo = generateInNo();
    StockInRecordEntity record = new StockInRecordEntity();
    record.setInNo(inNo);
    record.setOperatorId(session.getId());
    record.setTotalCost(amount);
    record.setCreatedAt(LocalDateTime.now());
    stockInRecordMapper.insert(record);

    item.setRecordId(record.getId());
    stockInItemMapper.insert(item);

    return new StockInResultVo(inNo, amount);
  }

  private UserSession currentSession() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof UserSession) {
      return (UserSession) principal;
    }
    throw new BizException(401, "未登录");
  }

  private String generateInNo() {
    return "SI" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        + (System.currentTimeMillis() % 1000);
  }
}
