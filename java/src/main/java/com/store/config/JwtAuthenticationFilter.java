package com.store.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.store.entity.SysUserEntity;
import com.store.mapper.SysRolePermissionMapper;
import com.store.mapper.SysUserMapper;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtUtil jwtUtil;
  private final SysUserMapper sysUserMapper;
  private final SysRolePermissionMapper rolePermissionMapper;

  public JwtAuthenticationFilter(
      JwtUtil jwtUtil,
      SysUserMapper sysUserMapper,
      SysRolePermissionMapper rolePermissionMapper) {
    this.jwtUtil = jwtUtil;
    this.sysUserMapper = sysUserMapper;
    this.rolePermissionMapper = rolePermissionMapper;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      try {
        UserSession session = jwtUtil.parseToken(token);
        SysUserEntity user = sysUserMapper.selectById(session.getId());
        if (user == null || user.getStatus() == null || user.getStatus() == 0) {
          SecurityContextHolder.clearContext();
          filterChain.doFilter(request, response);
          return;
        }
        List<String> permissions = rolePermissionMapper.selectPermissionsByRoleId(user.getRoleId());
        if (permissions == null) {
          permissions = new ArrayList<>();
        }
        UserSession refreshedSession = new UserSession(
            user.getId(),
            user.getUsername(),
            user.getName(),
            user.getRoleId(),
            permissions);
        List<SimpleGrantedAuthority> authorities = permissions.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(refreshedSession, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception ex) {
        SecurityContextHolder.clearContext();
      }
    }
    filterChain.doFilter(request, response);
  }
}
