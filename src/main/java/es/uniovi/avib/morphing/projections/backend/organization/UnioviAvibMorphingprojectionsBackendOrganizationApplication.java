package es.uniovi.avib.morphing.projections.backend.organization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class UnioviAvibMorphingprojectionsBackendOrganizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnioviAvibMorphingprojectionsBackendOrganizationApplication.class, args);
	}

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
