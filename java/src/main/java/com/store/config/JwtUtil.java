package com.store.config;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
  private final String secret;
  private final long expireMinutes;

  public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expire-minutes}") long expireMinutes) {
    this.secret = secret;
    this.expireMinutes = expireMinutes;
  }

  public String generateToken(UserSession session) {
    Instant now = Instant.now();
    return Jwts.builder()
        .setSubject(session.getUsername())
        .claim("uid", session.getId())
        .claim("name", session.getName())
        .claim("roleId", session.getRoleId())
        .claim("perms", session.getPermissions())
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(now.plusSeconds(expireMinutes * 60)))
        .signWith(SignatureAlgorithm.HS256, secret.getBytes(StandardCharsets.UTF_8))
        .compact();
  }

  @SuppressWarnings("unchecked")
  public UserSession parseToken(String token) {
    Claims claims = Jwts.parser()
        .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
        .parseClaimsJws(token)
        .getBody();
    Long uid = claims.get("uid", Number.class).longValue();
    String username = claims.getSubject();
    String name = claims.get("name", String.class);
    Long roleId = claims.get("roleId", Number.class).longValue();
    List<String> permissions = (List<String>) claims.get("perms");
    return new UserSession(uid, username, name, roleId, permissions);
  }
}
