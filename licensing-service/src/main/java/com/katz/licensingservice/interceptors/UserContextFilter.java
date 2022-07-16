package com.katz.licensingservice.interceptors;

import com.katz.licensingservice.model.UserContext;
import com.katz.licensingservice.utils.Definitions;
import com.katz.licensingservice.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * (c) Katz Solution
 * @Author: KatlegoM
 * Date: 20220712
 *
 * <p>
 *      A simple request filter which takes in info from the request header and sets user
 *      context.
 * </p>
 * */
public class UserContextFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(UserContextFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        UserContextHolder.set(
                new UserContext(
                        httpServletRequest.getHeader(Definitions.CORRELATION_ID)
                )
        );

        log.info("User Correlation Id: {}", UserContextHolder.get().getCorrelationId());

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
