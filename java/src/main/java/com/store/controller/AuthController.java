package com.store.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.common.Result;
import com.store.dto.LoginDto;
import com.store.service.AuthService;
import com.store.vo.AuthMeVo;
import com.store.vo.LoginVo;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public Result<LoginVo> login(@Valid @RequestBody LoginDto dto) {
    return Result.ok(authService.login(dto));
  }

  @GetMapping("/me")
  public Result<AuthMeVo> me() {
    return Result.ok(authService.currentUser());
  }
}
