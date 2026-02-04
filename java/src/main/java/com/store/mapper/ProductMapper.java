package com.store.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.store.entity.ProductEntity;

@Mapper
public interface ProductMapper extends BaseMapper<ProductEntity> {}
