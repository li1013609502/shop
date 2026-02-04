package com.store.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.store.common.BizException;
import com.store.dto.RoleDto;
import com.store.dto.RolePermissionUpdateDto;
import com.store.entity.SysRoleEntity;
import com.store.entity.SysRolePermissionEntity;
import com.store.mapper.SysRoleMapper;
import com.store.mapper.SysRolePermissionMapper;
import com.store.service.RoleService;
import com.store.vo.RoleVo;

@Service
public class RoleServiceImpl implements RoleService {
  private final SysRoleMapper sysRoleMapper;
  private final SysRolePermissionMapper rolePermissionMapper;

  public RoleServiceImpl(SysRoleMapper sysRoleMapper, SysRolePermissionMapper rolePermissionMapper) {
    this.sysRoleMapper = sysRoleMapper;
    this.rolePermissionMapper = rolePermissionMapper;
  }

  @Override
  public List<RoleVo> listRoles() {
    return sysRoleMapper.selectList(null).stream().map(this::toVo).collect(Collectors.toList());
  }

  @Override
  public RoleVo createRole(RoleDto dto) {
    SysRoleEntity entity = new SysRoleEntity();
    entity.setRoleName(dto.getRoleName());
    entity.setRoleCode(dto.getRoleCode());
    sysRoleMapper.insert(entity);
    return toVo(entity);
  }

  @Override
  public RoleVo updateRole(Long id, RoleDto dto) {
    SysRoleEntity entity = sysRoleMapper.selectById(id);
    if (entity == null) {
      throw new BizException(2001, "角色不存在");
    }
    entity.setRoleName(dto.getRoleName());
    entity.setRoleCode(dto.getRoleCode());
    sysRoleMapper.updateById(entity);
    return toVo(entity);
  }

  @Override
  public void deleteRole(Long id) {
    sysRoleMapper.deleteById(id);
    QueryWrapper<SysRolePermissionEntity> wrapper = new QueryWrapper<>();
    wrapper.eq("role_id", id);
    rolePermissionMapper.delete(wrapper);
  }

  @Override
  public List<String> listPermissions(Long roleId) {
    return rolePermissionMapper.selectPermissionsByRoleId(roleId);
  }

  @Override
  @Transactional
  public void updatePermissions(Long roleId, RolePermissionUpdateDto dto) {
    QueryWrapper<SysRolePermissionEntity> wrapper = new QueryWrapper<>();
    wrapper.eq("role_id", roleId);
    rolePermissionMapper.delete(wrapper);
    for (String permission : dto.getPermissions()) {
      SysRolePermissionEntity entity = new SysRolePermissionEntity();
      entity.setRoleId(roleId);
      entity.setPermissionCode(permission);
      rolePermissionMapper.insert(entity);
    }
  }

  private RoleVo toVo(SysRoleEntity entity) {
    RoleVo vo = new RoleVo();
    vo.setId(entity.getId());
    vo.setRoleName(entity.getRoleName());
    vo.setRoleCode(entity.getRoleCode());
    return vo;
  }
}
