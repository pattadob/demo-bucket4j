package com.example.demo.config;

import com.example.demo.filter.RateLimitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilter2(RateLimitFilter rateLimitFilter) {
        FilterRegistrationBean<RateLimitFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(rateLimitFilter);

        // กำหนด URL patterns ที่ต้องการให้ filter ทำงาน
        registrationBean.addUrlPatterns("/api/*");
//        registrationBean.addUrlPatterns("/v1/*");
//        registrationBean.addUrlPatterns("/public/*");

        // กำหนด order ให้ทำงานก่อน filter อื่นๆ
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        // กำหนดชื่อ filter
        registrationBean.setName("rateLimitFilter2");

        return registrationBean;
    }
}
