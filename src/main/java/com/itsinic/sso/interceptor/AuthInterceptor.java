/**
 * Copyright (c) 86cis.com 2015 All Rights Reserved.
 */
package com.itsinic.sso.interceptor;


import com.itsinic.sso.WebConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * 权限拦截器
 *
 * @author yintao
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {

    private final static Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
			super.preHandle(request,response,handler);
			logger.info("=============AuthInterceptor================");
			return true;
        } catch (Exception e) {
            logger.error("auth interceptor exception:", e);
            return false;
        }
    }

}
