package br.com.ialmeida.codingchallengeitau.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("properties")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertiesConfig {

    private String apiKey;
    private String tokenSecret;
    private Long tokenExpirationTime;

}
