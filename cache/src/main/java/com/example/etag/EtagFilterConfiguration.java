package com.example.etag;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import com.example.version.CacheBustingWebConfig;

@Configuration
public class EtagFilterConfiguration {

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter = new FilterRegistrationBean<>(
                new ShallowEtagHeaderFilter());

        shallowEtagHeaderFilter.addUrlPatterns(
                "/etag",
                "/etag/*",
                "/resources/*"
        );
        return shallowEtagHeaderFilter;
    }
}
