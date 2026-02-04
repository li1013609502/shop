package com.store.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;

public class RolePermissionUpdateDto {
  @NotEmpty
  private List<String> permissions;

  public List<String> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<String> permissions) {
    this.permissions = permissions;
  }
}
