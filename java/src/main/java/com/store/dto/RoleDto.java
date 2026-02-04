package com.store.dto;

import javax.validation.constraints.NotBlank;

public class RoleDto {
  @NotBlank
  private String roleName;

  @NotBlank
  private String roleCode;

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  public String getRoleCode() {
    return roleCode;
  }

  public void setRoleCode(String roleCode) {
    this.roleCode = roleCode;
  }
}
