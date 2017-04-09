package com.soso.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.util.regex.Pattern;

/**
 * Created by Garik Kalashyan on 2/26/2017.
 */


@Configuration
@Import(DataConfig.class)
@ComponentScan(basePackages = {"com.soso"}, excludeFilters = {@ComponentScan.Filter(type = FilterType.CUSTOM, value = RootConfig.WebPackage.class)})
public class RootConfig {
    public static class WebPackage
            extends RegexPatternTypeFilter {
        public WebPackage() {
            super(Pattern.compile("com.soso\\.web"));
        }
    }
}
