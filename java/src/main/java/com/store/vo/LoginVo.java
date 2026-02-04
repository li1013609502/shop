package com.store.vo;

import java.util.List;

public class LoginVo {
  private String token;
  private UserVo user;
  private List<String> permissions;

  public LoginVo() {}

  public LoginVo(String token, UserVo user, List<String> permissions) {
    this.token = token;
    this.user = user;
    this.permissions = permissions;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public UserVo getUser() {
    return user;
  }

  public void setUser(UserVo user) {
    this.user = user;
  }

  public List<String> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<String> permissions) {
    this.permissions = permissions;
  }
}
