package com.epam.training.config;

import com.epam.training.filter.TransactionIdFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static org.junit.jupiter.api.Assertions.*;

class FilterConfigTest {

    @Test
    void transactionIdFilterRegistration_returnsConfiguredBean() {
        FilterConfig config = new FilterConfig();

        FilterRegistrationBean<TransactionIdFilter> bean = config.transactionIdFilterRegistration();

        assertNotNull(bean);
        assertNotNull(bean.getFilter());
        assertEquals(TransactionIdFilter.class, bean.getFilter().getClass());
        assertTrue(bean.getUrlPatterns().contains("/*"));
        assertEquals(1, bean.getOrder());
    }
}