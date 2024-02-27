package es.uniovi.avib.morphing.projections.backend.organization.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class StorageConfig {
    @Value("${storage.host:localhost}")
    String host;

    @Value("${storage.port:8083}")
    String port;     
}
