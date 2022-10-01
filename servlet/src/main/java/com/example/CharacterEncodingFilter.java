package com.example;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;
import java.nio.charset.Charset;

@WebFilter("/*")
public class CharacterEncodingFilter implements Filter {

    // doFilter은 Container가 Servlet에게 Request, Response를 넘겨주는 시점에 한번만 발생하는 걸까?
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.getServletContext().log("doFilter() 호출");
        response.setCharacterEncoding("utf-8");
        // dofilter 메서드에서 Service를 호출해버린다.
        chain.doFilter(request, response);
        request.getServletContext().log("doFilter() 종료");
    }
}
