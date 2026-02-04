package com.store.service.impl;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.store.common.BizException;
import com.store.config.JwtUtil;
import com.store.config.UserSession;
import com.store.dto.LoginDto;
import com.store.entity.SysUserEntity;
import com.store.mapper.SysRolePermissionMapper;
import com.store.mapper.SysUserMapper;
import com.store.service.AuthService;
import com.store.vo.AuthMeVo;
import com.store.vo.LoginVo;
import com.store.vo.UserVo;

@Service
public class AuthServiceImpl implements AuthService {
  private final SysUserMapper sysUserMapper;
  private final SysRolePermissionMapper rolePermissionMapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public AuthServiceImpl(
      SysUserMapper sysUserMapper,
      SysRolePermissionMapper rolePermissionMapper,
      PasswordEncoder passwordEncoder,
      JwtUtil jwtUtil) {
    this.sysUserMapper = sysUserMapper;
    this.rolePermissionMapper = rolePermissionMapper;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  @Override
  public LoginVo login(LoginDto dto) {
    QueryWrapper<SysUserEntity> wrapper = new QueryWrapper<>();
    wrapper.eq("username", dto.getUsername());
    SysUserEntity user = sysUserMapper.selectOne(wrapper);
    if (user == null || user.getStatus() == null || user.getStatus() == 0) {
      throw new BizException(401, "用户名或密码错误");
    }
    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
      throw new BizException(401, "用户名或密码错误");
    }
    List<String> permissions = rolePermissionMapper.selectPermissionsByRoleId(user.getRoleId());
    if (permissions == null) {
      permissions = java.util.Collections.emptyList();
    }
    UserSession session = new UserSession(user.getId(), user.getUsername(), user.getName(), user.getRoleId(), permissions);
    String token = jwtUtil.generateToken(session);
    return new LoginVo(token, toUserVo(user), permissions);
  }

  @Override
  public AuthMeVo currentUser() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof UserSession) {
      UserSession session = (UserSession) principal;
      SysUserEntity user = sysUserMapper.selectById(session.getId());
      if (user == null || user.getStatus() == null || user.getStatus() == 0) {
        throw new BizException(401, "未登录");
      }
      UserVo vo = new UserVo();
      vo.setId(user.getId());
      vo.setUsername(user.getUsername());
      vo.setName(user.getName());
      vo.setRoleId(user.getRoleId());
      List<String> permissions = rolePermissionMapper.selectPermissionsByRoleId(user.getRoleId());
      if (permissions == null) {
        permissions = java.util.Collections.emptyList();
      }
      return new AuthMeVo(vo, permissions);
    }
    throw new BizException(401, "未登录");
  }

  private UserVo toUserVo(SysUserEntity user) {
    UserVo vo = new UserVo();
    vo.setId(user.getId());
    vo.setUsername(user.getUsername());
    vo.setName(user.getName());
    vo.setRoleId(user.getRoleId());
    return vo;
  }
}
