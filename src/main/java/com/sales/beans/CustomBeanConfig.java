package com.sales.beans;


import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomBeanConfig {


    @Bean("safeLogger")
    public Logger getLogger(){
        return LoggerFactory.getLogger("test");
    }

    @Bean
    public Hibernate6Module hibernateModule() {
        Hibernate6Module module = new Hibernate6Module();
        module.disable(Hibernate6Module.Feature.FORCE_LAZY_LOADING);
        return module;
    }
}
