package com.store.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.store.common.BizException;
import com.store.common.PageResult;
import com.store.dto.UserCreateDto;
import com.store.dto.UserStatusDto;
import com.store.dto.UserUpdateDto;
import com.store.entity.SysUserEntity;
import com.store.mapper.SysUserMapper;
import com.store.service.UserService;
import com.store.vo.UserDetailVo;

@Service
public class UserServiceImpl implements UserService {
  private static final String DEFAULT_PASSWORD = "123456";

  private final SysUserMapper sysUserMapper;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder) {
    this.sysUserMapper = sysUserMapper;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public PageResult<UserDetailVo> pageUsers(int page, int size, String keyword) {
    QueryWrapper<SysUserEntity> wrapper = new QueryWrapper<>();
    if (keyword != null && !keyword.trim().isEmpty()) {
      wrapper.and(q -> q.like("username", keyword).or().like("name", keyword));
    }
    Page<SysUserEntity> mpPage = sysUserMapper.selectPage(new Page<>(page, size), wrapper);
    List<UserDetailVo> records = mpPage.getRecords().stream().map(this::toVo).collect(Collectors.toList());
    return new PageResult<>(mpPage.getTotal(), records);
  }

  @Override
  public UserDetailVo createUser(UserCreateDto dto) {
    QueryWrapper<SysUserEntity> wrapper = new QueryWrapper<>();
    wrapper.eq("username", dto.getUsername());
    if (sysUserMapper.selectCount(wrapper) > 0) {
      throw new BizException(1001, "用户名已存在");
    }
    SysUserEntity entity = new SysUserEntity();
    entity.setUsername(dto.getUsername());
    entity.setName(dto.getName());
    entity.setPhone(dto.getPhone());
    entity.setRoleId(dto.getRoleId());
    entity.setStatus(1);
    entity.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
    sysUserMapper.insert(entity);
    return toVo(entity);
  }

  @Override
  public UserDetailVo updateUser(Long id, UserUpdateDto dto) {
    SysUserEntity entity = sysUserMapper.selectById(id);
    if (entity == null) {
      throw new BizException(2001, "用户不存在");
    }
    entity.setName(dto.getName());
    entity.setPhone(dto.getPhone());
    entity.setRoleId(dto.getRoleId());
    sysUserMapper.updateById(entity);
    return toVo(entity);
  }

  @Override
  public UserDetailVo updateStatus(Long id, UserStatusDto dto) {
    SysUserEntity entity = sysUserMapper.selectById(id);
    if (entity == null) {
      throw new BizException(2001, "用户不存在");
    }
    entity.setStatus(dto.getStatus());
    sysUserMapper.updateById(entity);
    return toVo(entity);
  }

  @Override
  public void resetPassword(Long id) {
    SysUserEntity entity = sysUserMapper.selectById(id);
    if (entity == null) {
      throw new BizException(2001, "用户不存在");
    }
    entity.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
    sysUserMapper.updateById(entity);
  }

  @Override
  public void deleteUser(Long id) {
    sysUserMapper.deleteById(id);
  }

  private UserDetailVo toVo(SysUserEntity entity) {
    UserDetailVo vo = new UserDetailVo();
    vo.setId(entity.getId());
    vo.setUsername(entity.getUsername());
    vo.setName(entity.getName());
    vo.setPhone(entity.getPhone());
    vo.setRoleId(entity.getRoleId());
    vo.setStatus(entity.getStatus());
    return vo;
  }
}
