package com.aichatservice.interceptors;


import com.aichatservice.pojo.Result;
import com.aichatservice.utils.JwtUtil;
import com.alibaba.fastjson.JSONObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String url = request.getRequestURI().toString();

        if (url.contains("login") || url.contains("register")
                || url.contains("download")) {
            return true;
        }

        //令牌验证
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            response.setContentType("text/html;charset=utf-8");
            Result result = Result.error("未登录");
            String notlogin = JSONObject.toJSONString(result);
            response.getWriter().write(notlogin);
            return false;
        }
        //验证token
        try {
            JwtUtil.parseToken(token);
            return true;
        } catch (Exception e) {
            response.setContentType("text/html;charset=utf-8");
            Result result = Result.error("未登录");
            String notlogin = JSONObject.toJSONString(result);
            response.getWriter().write(notlogin);
            //不放行
            return false;
        }
    }
}
