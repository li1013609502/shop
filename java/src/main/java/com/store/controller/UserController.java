package com.store.controller;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.store.common.PageResult;
import com.store.common.Result;
import com.store.dto.UserCreateDto;
import com.store.dto.UserStatusDto;
import com.store.dto.UserUpdateDto;
import com.store.service.UserService;
import com.store.vo.UserDetailVo;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  @PreAuthorize("hasAuthority('USER_MGR')")
  public Result<PageResult<UserDetailVo>> page(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String keyword) {
    return Result.ok(userService.pageUsers(page, size, keyword));
  }

  @PostMapping
  @PreAuthorize("hasAuthority('USER_MGR')")
  public Result<UserDetailVo> create(@Valid @RequestBody UserCreateDto dto) {
    return Result.ok(userService.createUser(dto));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('USER_MGR')")
  public Result<UserDetailVo> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDto dto) {
    return Result.ok(userService.updateUser(id, dto));
  }

  @PutMapping("/{id}/status")
  @PreAuthorize("hasAuthority('USER_MGR')")
  public Result<UserDetailVo> status(@PathVariable Long id, @Valid @RequestBody UserStatusDto dto) {
    return Result.ok(userService.updateStatus(id, dto));
  }

  @PutMapping("/{id}/reset-password")
  @PreAuthorize("hasAuthority('USER_MGR')")
  public Result<Void> resetPassword(@PathVariable Long id) {
    userService.resetPassword(id);
    return Result.ok(null);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('USER_MGR')")
  public Result<Void> delete(@PathVariable Long id) {
    userService.deleteUser(id);
    return Result.ok(null);
  }
}
