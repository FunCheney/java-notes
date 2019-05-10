package com.fchen.concurrency;

import com.fchen.concurrency.example.threadlocal.RequestHolder;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Classname HttpFilter
 * @Description http请求过滤器
 * @Date 2019/5/9 19:07
 * @Author by Fchen
 */
@Slf4j
public class HttpFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        log.info("do filter:{},{}",Thread.currentThread().getId(), servletRequest.getRequestURI());
        RequestHolder.add(Thread.currentThread().getId());
        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
