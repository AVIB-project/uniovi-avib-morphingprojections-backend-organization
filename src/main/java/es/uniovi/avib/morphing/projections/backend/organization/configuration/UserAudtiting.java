package es.uniovi.avib.morphing.projections.backend.organization.configuration;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
public class UserAudtiting implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		// TODO Auto-generated method stub
		return Optional.of("Administrator");
		
        /*String uname = SecurityContextHolder.getContext().getAuthentication().getName();
        return Optional.of(uname);*/		
	}
}
