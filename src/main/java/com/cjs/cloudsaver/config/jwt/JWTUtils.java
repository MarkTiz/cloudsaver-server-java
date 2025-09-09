package com.cjs.cloudsaver.config.jwt;

import com.cjs.cloudsaver.common.exception.BizException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * JWT 工具类
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
@Slf4j
public class JWTUtils {

    /**
     * 密钥
     */
    private String secret;

    /**
     * 过期时间
     */
    private Long expire;

    /**
     * 签发者
     */
    private String issuer;

    /**
     * 头部
     */
    private String header;

    /**
     * 获取 JWT 密钥
     *
     * @return 结果
     */
    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret); // 解码 Base64 字符串
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成密钥
     *
     * @return 结果
     */
    public String generateKey() {
        SecretKey key = Jwts.SIG.HS256.key().build();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * 生成token
     *
     * @param userId 主题
     * @return 结果
     */
    public String generateToken(String userId) {
        return this.generateToken(userId, expire);
    }

    /**
     * 生成token
     *
     * @param userId 用户id
     * @param expire 过期时间
     * @return 结果
     */
    public String generateToken(String userId, Long expire) {
        long expirationDate = System.currentTimeMillis() + expire;
        Date expireDate = new Date(expirationDate);
        SecretKey key = getSecretKey();
        return Jwts.builder()
                .claim("userId", userId)
                .expiration(expireDate)
                .signWith(key)
                .compact();
    }

    /**
     * 解析 token
     *
     * @param token token
     * @return 结果
     */
    public String parseToken(String token) throws BizException {
        SecretKey key = getSecretKey();
        try {
            Claims payload = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return payload.get("userId", String.class);
        } catch (ExpiredJwtException e) {
            // 令牌过期，工具类内部自己判断了
            log.error("token expired", e);
            throw new BizException("auth_token_expired");
        } catch (JwtException e) {
            // 令牌无效
            log.error("token parse error", e);
            throw new BizException("auth_token_not_valid");
        }
    }


}
