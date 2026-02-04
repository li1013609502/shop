package com.store.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.store.common.BizException;
import com.store.common.PageResult;
import com.store.dto.ProductCreateDto;
import com.store.dto.ProductPriceUpdateDto;
import com.store.dto.StockAdjustDto;
import com.store.config.UserSession;
import com.store.entity.ProductEntity;
import com.store.entity.StockAdjustRecordEntity;
import com.store.mapper.ProductMapper;
import com.store.mapper.StockAdjustRecordMapper;
import com.store.service.ProductService;
import com.store.vo.ProductVo;

@Service
public class ProductServiceImpl implements ProductService {
  private final ProductMapper productMapper;
  private final StockAdjustRecordMapper stockAdjustRecordMapper;

  public ProductServiceImpl(ProductMapper productMapper, StockAdjustRecordMapper stockAdjustRecordMapper) {
    this.productMapper = productMapper;
    this.stockAdjustRecordMapper = stockAdjustRecordMapper;
  }

  @Override
  public ProductVo createProduct(ProductCreateDto dto) {
    ProductEntity entity = new ProductEntity();
    entity.setBarcode(dto.getBarcode());
    entity.setName(dto.getName());
    entity.setCategory(dto.getCategory());
    entity.setCostPrice(dto.getCostPrice());
    entity.setSalePrice(dto.getSalePrice());
    entity.setStock(0);
    entity.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
    productMapper.insert(entity);
    return toVo(entity);
  }

  @Override
  public ProductVo getByBarcode(String barcode) {
    QueryWrapper<ProductEntity> wrapper = new QueryWrapper<>();
    wrapper.eq("barcode", barcode);
    ProductEntity entity = productMapper.selectOne(wrapper);
    if (entity == null) {
      throw new BizException(2001, "商品不存在");
    }
    return toVo(entity);
  }

  @Override
  public PageResult<ProductVo> pageProducts(int page, int size, String keyword) {
    QueryWrapper<ProductEntity> wrapper = new QueryWrapper<>();
    if (keyword != null && !keyword.trim().isEmpty()) {
      wrapper.and(q -> q.like("barcode", keyword).or().like("name", keyword));
    }
    Page<ProductEntity> mpPage = productMapper.selectPage(new Page<>(page, size), wrapper);
    List<ProductVo> records = mpPage.getRecords().stream().map(this::toVo).collect(Collectors.toList());
    return new PageResult<>(mpPage.getTotal(), records);
  }

  @Override
  public ProductVo updatePrice(Long id, ProductPriceUpdateDto dto) {
    ProductEntity entity = productMapper.selectById(id);
    if (entity == null) {
      throw new BizException(2001, "商品不存在");
    }
    entity.setSalePrice(dto.getSalePrice());
    productMapper.updateById(entity);
    return toVo(entity);
  }

  @Override
  @Transactional
  public ProductVo adjustStock(Long id, StockAdjustDto dto) {
    if (dto.getChangeStock() == 0) {
      throw new BizException(1001, "库存调整值不能为0");
    }
    ProductEntity entity = productMapper.selectById(id);
    if (entity == null) {
      throw new BizException(2001, "商品不存在");
    }
    int beforeStock = entity.getStock();
    int newStock = beforeStock + dto.getChangeStock();
    if (newStock < 0) {
      throw new BizException(2002, "库存不足");
    }
    entity.setStock(newStock);
    productMapper.updateById(entity);
    StockAdjustRecordEntity record = new StockAdjustRecordEntity();
    record.setAdjNo(generateAdjNo());
    record.setOperatorId(currentSession().getId());
    record.setProductId(entity.getId());
    record.setBarcode(entity.getBarcode());
    record.setBeforeStock(beforeStock);
    record.setChangeStock(dto.getChangeStock());
    record.setAfterStock(newStock);
    record.setReason(dto.getReason());
    record.setCreatedAt(LocalDateTime.now());
    stockAdjustRecordMapper.insert(record);
    return toVo(entity);
  }

  private UserSession currentSession() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof UserSession) {
      return (UserSession) principal;
    }
    throw new BizException(401, "未登录");
  }

  private String generateAdjNo() {
    return "SA" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        + (System.currentTimeMillis() % 1000);
  }

  private ProductVo toVo(ProductEntity entity) {
    ProductVo vo = new ProductVo();
    vo.setId(entity.getId());
    vo.setBarcode(entity.getBarcode());
    vo.setName(entity.getName());
    vo.setCategory(entity.getCategory());
    vo.setCostPrice(entity.getCostPrice());
    vo.setSalePrice(entity.getSalePrice());
    vo.setStock(entity.getStock());
    vo.setStatus(entity.getStatus());
    return vo;
  }
}
