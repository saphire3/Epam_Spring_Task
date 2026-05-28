package com.epam.workload.logging;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionLoggingFilterTest {

    @InjectMocks
    private TransactionLoggingFilter filter;

    @Mock
    private FilterChain filterChain;

    @Test
    void doFilter_withTransactionIdHeader_usesProvidedId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Transaction-Id", "tx-abc-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getHeader("X-Transaction-Id")).isEqualTo("tx-abc-123");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_withoutTransactionIdHeader_generatesNewId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getHeader("X-Transaction-Id")).isNotNull().isNotBlank();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_withBlankTransactionIdHeader_generatesNewId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Transaction-Id", "  ");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getHeader("X-Transaction-Id")).isNotBlank();
        verify(filterChain).doFilter(request, response);
    }
}