package com.epam.training.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class TransactionIdFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TransactionIdFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String transactionId = UUID.randomUUID().toString();
        MDC.put("transactionId", transactionId);
        response.setHeader("X-Transaction-Id", transactionId);

        try {
            logger.info("Request started: method={}, uri={}, transactionId={}",
                    request.getMethod(), request.getRequestURI(), transactionId);

            filterChain.doFilter(request, response);

            logger.info("Request finished: method={}, uri={}, status={}, transactionId={}",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), transactionId);
        } finally {
            MDC.remove("transactionId");
        }
    }
}