package com.sales.config;

import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public GlobalOperationCustomizer addGlobalHeaders() {
        return (operation, handlerMethod) -> {
            List<Parameter> parameters = operation.getParameters();
            if (parameters == null) {
                parameters = new java.util.ArrayList<>();
            }

            parameters.add(new Parameter()
                    .in("header")
                    .name("Authorization")
                    .description("My custom header (REQUIRED)")
                    .required(true)
            );
/*

            parameters.add(new Parameter()
                    .in(Parameter.In.HEADER)
                    .name("X-Optional-Header")
                    .description("An optional header")
                    .required(false)
                    .schema(new io.swagger.v3.oas.models.media.Schema().type("string"))); // Add schema type
*/


            operation.setParameters(parameters); // Crucial: Update operation's parameters
            return operation;
        };
    }
}