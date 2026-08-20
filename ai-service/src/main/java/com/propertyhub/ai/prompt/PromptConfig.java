package com.propertyhub.ai.prompt;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class PromptConfig {

    @Bean
    public PromptTemplate propertyAssistantPromptTemplate(
            @Value("classpath:prompts/property-assistant.st") Resource templateResource) {
        return new PromptTemplate(templateResource);
    }

}
