package com.store.config;

import java.util.List;

public class UserSession {
  private Long id;
  private String username;
  private String name;
  private Long roleId;
  private List<String> permissions;

  public UserSession() {}

  public UserSession(Long id, String username, String name, Long roleId, List<String> permissions) {
    this.id = id;
    this.username = username;
    this.name = name;
    this.roleId = roleId;
    this.permissions = permissions;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }

  public List<String> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<String> permissions) {
    this.permissions = permissions;
  }
}
