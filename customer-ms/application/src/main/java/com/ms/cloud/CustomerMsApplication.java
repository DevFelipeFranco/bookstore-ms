package com.ms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
// @Import({
// com.ms.cloud.service.CustomerService.class,
// com.ms.cloud.out.CustomerPersistenceAdapter.class
// })
public class CustomerMsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerMsApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.boot.web.servlet.ServletRegistrationBean<jakarta.servlet.Servlet> h2Console() {
        org.springframework.boot.web.servlet.ServletRegistrationBean<jakarta.servlet.Servlet> bean = new org.springframework.boot.web.servlet.ServletRegistrationBean<>(
                new org.h2.server.web.JakartaWebServlet());
        bean.addUrlMappings("/h2-console/*");
        return bean;
    }

}