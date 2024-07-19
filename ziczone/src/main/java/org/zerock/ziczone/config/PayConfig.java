package org.zerock.ziczone.config;

import lombok.Data;
import lombok.Getter;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "payments.toss") 
public class PayConfig {
//    private String testClientApiKey;
    private String testSecretApiKey;
    private String successUrl;
    private String failUrl;
}
