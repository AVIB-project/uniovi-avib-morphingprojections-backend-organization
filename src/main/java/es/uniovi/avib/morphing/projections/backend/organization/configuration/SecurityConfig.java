package es.uniovi.avib.morphing.projections.backend.organization.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class SecurityConfig {
    @Value("${security.host:localhost}")
    String host;

    @Value("${security.port:8084}")
    String port;   
}
