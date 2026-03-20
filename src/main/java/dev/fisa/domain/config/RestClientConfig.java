package dev.fisa.domain.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${gateway.base-url}")
    private String gatewayUrl;

    @Bean
    public RestClient gatewayRestClient() {
        return RestClient.builder()
                         .baseUrl(gatewayUrl)
                         .build();
    }
}