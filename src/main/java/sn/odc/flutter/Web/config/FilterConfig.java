package sn.odc.flutter.Web.config;

import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sn.odc.flutter.Web.filters.ResponseFormattingFilter;

@Configuration
public class FilterConfig {

    @Bean
    public Filter responseFormattingFilter() {
        return new ResponseFormattingFilter();
    }
}
