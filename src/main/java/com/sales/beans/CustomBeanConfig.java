package com.sales.beans;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomBeanConfig {


    @Bean
    public Logger getLogger(){
        return LogManager.getLogger(this);
    }
}
