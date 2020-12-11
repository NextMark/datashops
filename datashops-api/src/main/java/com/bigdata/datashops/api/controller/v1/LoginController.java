package com.bigdata.datashops.api.controller.v1;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bigdata.datashops.api.config.security.jwt.JwtUtil;
import com.bigdata.datashops.api.controller.BasicController;

@RestController
@RequestMapping("/v1/login")
public class LoginController extends BasicController {
    @Resource
    private JwtUtil jwtUtil;

    @RequestMapping(method = RequestMethod.GET)
    public Object token(@NotNull String partner) {
        String token = jwtUtil.sign(partner);
        return ok(token);
    }
}
