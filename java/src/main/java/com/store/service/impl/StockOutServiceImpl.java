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
import com.store.common.PageResult;
import com.store.config.UserSession;
import com.store.dto.StockOutCreateDto;
import com.store.dto.StockOutItemDto;
import com.store.entity.ProductEntity;
import com.store.entity.StockOutItemEntity;
import com.store.entity.StockOutRecordEntity;
import com.store.mapper.ProductMapper;
import com.store.mapper.StockOutItemMapper;
import com.store.mapper.StockOutRecordMapper;
import com.store.service.StockOutService;
import com.store.vo.StockOutItemVo;
import com.store.vo.StockOutResultVo;

@Service
public class StockOutServiceImpl implements StockOutService {
  private final ProductMapper productMapper;
  private final StockOutRecordMapper stockOutRecordMapper;
  private final StockOutItemMapper stockOutItemMapper;

  public StockOutServiceImpl(
      ProductMapper productMapper,
      StockOutRecordMapper stockOutRecordMapper,
      StockOutItemMapper stockOutItemMapper) {
    this.productMapper = productMapper;
    this.stockOutRecordMapper = stockOutRecordMapper;
    this.stockOutItemMapper = stockOutItemMapper;
  }

  @Override
  @Transactional
  public StockOutResultVo createStockOut(StockOutCreateDto dto) {
    UserSession session = currentSession();
    String outNo = generateOutNo();
    BigDecimal totalAmount = BigDecimal.ZERO;
    BigDecimal totalProfit = BigDecimal.ZERO;
    List<StockOutItemEntity> items = new ArrayList<>();

    for (StockOutItemDto itemDto : dto.getItems()) {
      QueryWrapper<ProductEntity> wrapper = new QueryWrapper<>();
      wrapper.eq("barcode", itemDto.getBarcode());
      ProductEntity product = productMapper.selectOne(wrapper);
      if (product == null) {
        throw new BizException(2001, "商品不存在");
      }
      if (product.getStock() < itemDto.getQty()) {
        throw new BizException(2002, "库存不足");
      }
      BigDecimal qty = BigDecimal.valueOf(itemDto.getQty());
      BigDecimal amount = product.getSalePrice().multiply(qty);
      BigDecimal profit = product.getSalePrice().subtract(product.getCostPrice()).multiply(qty);

      product.setStock(product.getStock() - itemDto.getQty());
      productMapper.updateById(product);

      StockOutItemEntity item = new StockOutItemEntity();
      item.setProductId(product.getId());
      item.setBarcode(product.getBarcode());
      item.setProductName(product.getName());
      item.setQty(itemDto.getQty());
      item.setCostPrice(product.getCostPrice());
      item.setSalePrice(product.getSalePrice());
      item.setAmount(amount);
      item.setProfit(profit);
      items.add(item);

      totalAmount = totalAmount.add(amount);
      totalProfit = totalProfit.add(profit);
    }

    StockOutRecordEntity record = new StockOutRecordEntity();
    record.setOutNo(outNo);
    record.setOperatorId(session.getId());
    record.setTotalAmount(totalAmount);
    record.setTotalProfit(totalProfit);
    record.setCreatedAt(LocalDateTime.now());
    stockOutRecordMapper.insert(record);

    for (StockOutItemEntity item : items) {
      item.setRecordId(record.getId());
      stockOutItemMapper.insert(item);
    }

    return new StockOutResultVo(outNo, totalAmount, totalProfit);
  }

  @Override
  public PageResult<StockOutItemVo> pageItems(int page, int size, String beginDate, String endDate) {
    long offset = (long) (page - 1) * size;
    List<StockOutItemVo> records = stockOutItemMapper.selectPagedItems(beginDate, endDate, size, offset);
    long total = stockOutItemMapper.countItems(beginDate, endDate);
    return new PageResult<>(total, records);
  }

  private UserSession currentSession() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof UserSession) {
      return (UserSession) principal;
    }
    throw new BizException(401, "未登录");
  }

  private String generateOutNo() {
    return "SO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        + (System.currentTimeMillis() % 1000);
  }
}
