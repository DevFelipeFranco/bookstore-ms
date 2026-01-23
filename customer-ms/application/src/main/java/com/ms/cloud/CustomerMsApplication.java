package com.ms.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
//@Import({
//        com.ms.cloud.service.CustomerService.class,
//        com.ms.cloud.out.CustomerPersistenceAdapter.class
//})
public class CustomerMsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerMsApplication.class, args);
    }
}