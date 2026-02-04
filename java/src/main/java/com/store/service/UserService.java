package com.store.service;

import com.store.common.PageResult;
import com.store.dto.UserCreateDto;
import com.store.dto.UserStatusDto;
import com.store.dto.UserUpdateDto;
import com.store.vo.UserDetailVo;

public interface UserService {
  PageResult<UserDetailVo> pageUsers(int page, int size, String keyword);

  UserDetailVo createUser(UserCreateDto dto);

  UserDetailVo updateUser(Long id, UserUpdateDto dto);

  UserDetailVo updateStatus(Long id, UserStatusDto dto);

  void resetPassword(Long id);

  void deleteUser(Long id);
}
