package es.uniovi.avib.morphing.projections.backend.organization.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class AnnotationConfig {
    @Value("${annotation.host:localhost}")
    String host;

    @Value("${annotation.port:8081}")
    String port;
}
