package com.epam.training.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class TransactionLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TransactionLoggingFilter.class);
    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String transactionId = request.getHeader(TRANSACTION_ID_HEADER);
        if (transactionId == null || transactionId.isBlank()) {
            transactionId = UUID.randomUUID().toString();
        }
        MDC.put("transactionId", transactionId);
        response.setHeader(TRANSACTION_ID_HEADER, transactionId);

        log.info("[TRANSACTION] endpoint={} method={} transactionId={}",
                request.getRequestURI(), request.getMethod(), transactionId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            log.info("[TRANSACTION] endpoint={} status={} transactionId={}",
                    request.getRequestURI(), response.getStatus(), transactionId);
            MDC.clear();
        }
    }
}