package com.ai.langchainllm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ai.langchainllm", "com.ai.langchainllm.config"})
public class LangchainApplication {

    public static void main(String[] args) {
        SpringApplication.run(LangchainApplication.class, args);
    }

}
