package com.bigdata.datashops.api.config.security.jwt;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

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
import com.bigdata.datashops.common.Constants;

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
 */
@Component
public class JwtUtil {
    private final static Logger LOG = LoggerFactory.getLogger(JwtUtil.class);
    @Autowired
    private RSAUtil rsaUtil;

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
        String token = httpRequest.getHeader(Constants.JWT_HEADER);
        if (StringUtils.isEmpty(token)) {
            token = httpRequest.getParameter(Constants.JWT_HEADER);
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
        final Claims claims = getClaims(token);
        final Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(Constants.JWT_AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(new User(username, "", authorities), null, authorities);
    }

    /**
     * 签发token
     *
     * @param username 用户名
     * @return token
     */
    public String sign(final String username) {
        final Date date = new Date(System.currentTimeMillis() + Constants.JWT_EXPIRATION_TIME * 1000);
        // 加载私钥
        final PrivateKey privateKey = rsaUtil.loadPemPrivateKey(Constants.JWT_PRIVATE_KEY);
        // 创建 token
        return Constants.JWT_TOKEN_PREFIX + " " + Jwts.builder().setSubject(username)
                                                          .claim(Constants.JWT_AUTHORITIES_KEY, "ROLE_USER")
                                                          .setExpiration(date)
                                                          .signWith(SignatureAlgorithm.RS256, privateKey)
                                                          .compressWith(CompressionCodecs.DEFLATE).compact();
    }

    /**
     * 解析token
     */
    private Jws<Claims> parseToken(final String token) {
        try {
            final PublicKey publicKey = rsaUtil.loadPemPublicKey(Constants.JWT_PUBLIC_LEY);
            return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token.replace(Constants.JWT_TOKEN_PREFIX, ""));
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
