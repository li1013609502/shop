package com.store.service;

import com.store.dto.LoginDto;
import com.store.vo.AuthMeVo;
import com.store.vo.LoginVo;

public interface AuthService {
  LoginVo login(LoginDto dto);

  AuthMeVo currentUser();
}
