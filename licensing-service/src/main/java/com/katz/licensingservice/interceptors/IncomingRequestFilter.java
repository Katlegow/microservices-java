package com.katz.licensingservice.interceptors;

import com.katz.licensingservice.model.UserContext;
import com.katz.licensingservice.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.katz.licensingservice.utils.Definitions.CORRELATION_ID;

@Component
public class IncomingRequestFilter implements Filter {
    public static Logger log = LoggerFactory.getLogger(IncomingRequestFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        UserContextHolder.set(
                new UserContext(httpServletRequest.getHeader(CORRELATION_ID))
        );

        log.info(
                "IncomingRequestFilter Correlation Id: {}",
                UserContextHolder.get().getCorrelationId()
        );

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
