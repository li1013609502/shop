package com.store.service;

import java.util.List;

import com.store.dto.RoleDto;
import com.store.dto.RolePermissionUpdateDto;
import com.store.vo.RoleVo;

public interface RoleService {
  List<RoleVo> listRoles();

  RoleVo createRole(RoleDto dto);

  RoleVo updateRole(Long id, RoleDto dto);

  void deleteRole(Long id);

  List<String> listPermissions(Long roleId);

  void updatePermissions(Long roleId, RolePermissionUpdateDto dto);
}
