package com.soso.web;

import com.soso.models.ServiceInfo;
import com.soso.service.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;

import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;

@Configuration
@ComponentScan
@EnableWebMvc
@PropertySource(value = { "classpath:application.properties" })
public class WebConfig
        extends WebMvcConfigurerAdapter {


    /** properties for heroku db
     *   Host= ec2-54-247-99-159.eu-west-1.compute.amazonaws.com
     *   Database= dlu28gghspr52
     *   User= iwrfrdlugrxtfd
     *   Port= 5432
     *   Password=  568e49d657f1e72020222cbc9637dce8e8545a4583afd82a398d415271ab7532
     *   URI=  postgres://iwrfrdlugrxtfd:568e49d657f1e72020222cbc9637dce8e8545a4583afd82a398d415271ab7532@ec2-54-247-99-159.eu-west-1.compute.amazonaws.com:5432/dlu28gghspr52
     *   Heroku CLI= heroku pg:psql postgresql-dimensional-48496 --app soso-partner
     *
     */

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public DataSource dataSource() {
        PartnerService partnerService = new PartnerService(2);
        ServiceInfo myInfo =  partnerService.getDestinationService();

        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(myInfo.getDbConnectionMetaData().getDriverClassName());
        ds.setUrl(myInfo.getDbConnectionMetaData().getUrl());
        ds.setUsername(myInfo.getDbConnectionMetaData().getUsername());
        ds.setPassword(myInfo.getDbConnectionMetaData().getPassword());
        return ds;
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }


    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("128KB");
        factory.setMaxRequestSize("128KB");
        return factory.createMultipartConfig();
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {

        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        return resolver;
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*").allowedOrigins("*");
    }

}
