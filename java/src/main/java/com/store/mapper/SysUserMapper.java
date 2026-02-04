package com.store.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.store.entity.SysUserEntity;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUserEntity> {}
