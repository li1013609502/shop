package com.store.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.common.Result;
import com.store.dto.RoleDto;
import com.store.dto.RolePermissionUpdateDto;
import com.store.service.RoleService;
import com.store.vo.RoleVo;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
  private final RoleService roleService;

  public RoleController(RoleService roleService) {
    this.roleService = roleService;
  }

  @GetMapping
  @PreAuthorize("hasAuthority('ROLE_MGR')")
  public Result<List<RoleVo>> list() {
    return Result.ok(roleService.listRoles());
  }

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_MGR')")
  public Result<RoleVo> create(@Valid @RequestBody RoleDto dto) {
    return Result.ok(roleService.createRole(dto));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_MGR')")
  public Result<RoleVo> update(@PathVariable Long id, @Valid @RequestBody RoleDto dto) {
    return Result.ok(roleService.updateRole(id, dto));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_MGR')")
  public Result<Void> delete(@PathVariable Long id) {
    roleService.deleteRole(id);
    return Result.ok(null);
  }

  @GetMapping("/{id}/permissions")
  @PreAuthorize("hasAuthority('ROLE_MGR')")
  public Result<List<String>> permissions(@PathVariable Long id) {
    return Result.ok(roleService.listPermissions(id));
  }

  @PutMapping("/{id}/permissions")
  @PreAuthorize("hasAuthority('ROLE_MGR')")
  public Result<Void> updatePermissions(
      @PathVariable Long id,
      @Valid @RequestBody RolePermissionUpdateDto dto) {
    roleService.updatePermissions(id, dto);
    return Result.ok(null);
  }
}
