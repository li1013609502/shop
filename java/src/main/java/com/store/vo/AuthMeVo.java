package com.store.vo;

import java.util.List;

public class AuthMeVo {
  private UserVo user;
  private List<String> permissions;

  public AuthMeVo() {}

  public AuthMeVo(UserVo user, List<String> permissions) {
    this.user = user;
    this.permissions = permissions;
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
