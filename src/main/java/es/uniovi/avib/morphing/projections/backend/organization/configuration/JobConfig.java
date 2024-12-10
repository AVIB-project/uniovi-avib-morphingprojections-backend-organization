package es.uniovi.avib.morphing.projections.backend.organization.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class JobConfig {
    @Value("${job.host:localhost}")
    String host;

    @Value("${job.port:8084}")
    String port;     
}
