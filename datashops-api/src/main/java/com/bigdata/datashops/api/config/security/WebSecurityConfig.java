package com.bigdata.datashops.api.config.security;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bigdata.datashops.api.config.security.jwt.JwtAuthenticationEntryPoint;
import com.bigdata.datashops.api.config.security.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Resource
    private JdbcTemplate jdbcTemplate;

    private final String[] permitMethods = {
            "/v1/login"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        LoginUserDetailsService userDetailsService = new LoginUserDetailsService();
        userDetailsService.setJdbcTemplate(jdbcTemplate);
        userDetailsService.setUsersByUsernameQuery("select username,password,enabled,id,root from auth_user where username = ?");
        userDetailsService.setAuthoritiesByUsernameQuery("select username,role from auth_user_role where username = ?");
        auth.userDetailsService(userDetailsService).passwordEncoder(new StandardPasswordEncoder());
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http    // 关闭cors
                .cors().disable()
                // 关闭csrf
                .csrf().disable()
                // 无状态Session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 异常处理
                .exceptionHandling().authenticationEntryPoint(this.jwtAuthenticationEntryPoint).and()
                // 对所有的请求都做权限校验
                .authorizeRequests()
                // 允许登录和注册
                .antMatchers(permitMethods)
                .permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated().and();

        http    // 基于定制JWT安全过滤器
                .addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // 禁用页面缓存
        http.headers().cacheControl();
    }

    static class LoginUserDetailsService extends JdbcDaoImpl {

        @Override
        protected List<UserDetails> loadUsersByUsername(String username) {
            return this.getJdbcTemplate().query(super.getUsersByUsernameQuery(), new String[]{username}, (rs, rowNum) -> {
                String username1 = rs.getString(1);
                String password = rs.getString(2);
                boolean enabled = rs.getBoolean(3);
                String id = rs.getString(4);
                boolean root = rs.getBoolean(5);
                return new LoginUser(id, root, username1, password, enabled, true, true, true, AuthorityUtils.NO_AUTHORITIES);
            });
        }

        @Override
        protected List<GrantedAuthority> loadUserAuthorities(String username) {
            List<GrantedAuthority> authorities = super.loadUserAuthorities(username);
            if (authorities.isEmpty()) {
                return Collections.singletonList(new SimpleGrantedAuthority("no_auth"));
            }
            return authorities;
        }

        @Override
        protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery, List<GrantedAuthority> combinedAuthorities) {
            String returnUsername = userFromUserQuery.getUsername();
            if (!isUsernameBasedPrimaryKey()) {
                returnUsername = username;
            }
            String id = ((LoginUser) userFromUserQuery).getId();
            boolean root = ((LoginUser) userFromUserQuery).isRoot();
            return new LoginUser(id, root, returnUsername, userFromUserQuery.getPassword(), userFromUserQuery.isEnabled(), true, true, true, combinedAuthorities);
        }
    }
}
