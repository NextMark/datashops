package com.bigdata.datashops.api.config.security.jwt;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.api.config.RSAUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * Json web token 工具
 * 验证、生成token
 *
 */
@Component
public class JwtUtil {
    private final static Logger LOG = LoggerFactory.getLogger(JwtUtil.class);
    @Autowired
    private RSAUtil rsaUtil;
    @Resource
    private JwtSetting jwtSetting;

    private Claims getClaims(final String token) {
        final Jws<Claims> jws = parseToken(token);
        return jws == null ? null : jws.getBody();
    }

    /**
     * 根据token得到用户名
     */
    public String getUsername(final String token) {
        final Claims claims = getClaims(token);
        return claims == null ? null : claims.getSubject();
    }


    /**
     * 从请求头或请求参数中获取token
     */
    public String getTokenFromRequest(final HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader(jwtSetting.getHeader());
        if (StringUtils.isEmpty(token)) {
            token = httpRequest.getParameter(jwtSetting.getHeader());
        }
        return token;
    }

    /**
     * 返回用户认证
     *
     * @param username 用户名
     * @param token    token
     * @return UsernamePasswordAuthenticationToken
     */
    public UsernamePasswordAuthenticationToken getAuthentication(final String username, final String token) {
        // 解析 token 的 payload
        final Claims claims = getClaims(token);

        // 获取用户角色字符串
        // 将元素转换为 GrantedAuthority 接口集合
        //noinspection ConstantConditions
        final Collection<? extends GrantedAuthority> authorities =
                // 因为 JwtAuthenticationFilter 拦截器已经检查过 token 有效，所以可以忽略 get 空指针提示
                Arrays.stream(claims.get(jwtSetting.getAuthoritiesKey()).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(
                new User(username, "", authorities),
                null,
                authorities);
    }

    /**
     * 签发token
     *
     * @param username           用户名
     * @return token
     */
    public String sign(final String username) {
        final Date date = new Date(System.currentTimeMillis() + jwtSetting.getExpirationTime() * 1000);
        // 加载私钥
        final PrivateKey privateKey = rsaUtil.loadPemPrivateKey(jwtSetting.getPrivateKey());
        // 创建 token
        return jwtSetting.getTokenPrefix() + " " +
                Jwts.builder()
                        // 设置用户名
                        .setSubject(username)
                        // 添加权限属性
                        .claim(jwtSetting.getAuthoritiesKey(), "ROLE_USER")
                        // 设置失效时间
                        .setExpiration(date)
                        // 512位的私钥加密生成签名
                        .signWith(SignatureAlgorithm.RS256, privateKey)
                        // 哈夫曼压缩
                        .compressWith(CompressionCodecs.DEFLATE)
                        .compact();
    }

    /**
     * 解析token
     */
    private Jws<Claims> parseToken(final String token) {
        try {
            // 加载公钥
            final PublicKey publicKey = rsaUtil.loadPemPublicKey(jwtSetting.getPublicKey());
            return Jwts
                    .parser()
                    // 公钥解密
                    .setSigningKey(publicKey)
                    .parseClaimsJws(token.replace(jwtSetting.getTokenPrefix(), ""));
        } catch (final SignatureException e) {
            // 签名异常
            LOG.error("Invalid JWT signature");
        } catch (final MalformedJwtException e) {
            // JWT 格式错误
            LOG.error("Invalid JWT token");
        } catch (final ExpiredJwtException e) {
            // JWT 过期
            LOG.error("Expired JWT token");
        } catch (final UnsupportedJwtException e) {
            // 不支持该 JWT
            LOG.error("Unsupported JWT token");
        } catch (final IllegalArgumentException e) {
            // 参数错误异常
            LOG.error("JWT token compact of handler are invalid");
        }
        return null;
    }
}
