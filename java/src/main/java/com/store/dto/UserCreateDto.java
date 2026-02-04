package com.store.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UserCreateDto {
  @NotBlank
  private String username;

  @NotBlank
  private String name;

  private String phone;

  @NotNull
  private Long roleId;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }
}
