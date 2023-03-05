package vn.com.report.config;


import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.report.filter.CorsFilter;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean greetingFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setName("CorsFilter");
        CorsFilter greetingFilter = new CorsFilter();
        registrationBean.setFilter(greetingFilter);
        registrationBean.setOrder(1);
        return registrationBean;
    }

}

