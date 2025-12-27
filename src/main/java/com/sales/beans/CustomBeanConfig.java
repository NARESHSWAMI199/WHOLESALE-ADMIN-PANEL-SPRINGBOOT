package com.sales.beans;


import com.sales.helpers.Logger;
import com.sales.helpers.SafeLogHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomBeanConfig {


    @Bean("safeLogger")
    public Logger getLogger(){
        return SafeLogHelper.getInstance();
    }
}
