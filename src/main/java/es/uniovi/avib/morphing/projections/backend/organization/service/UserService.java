package es.uniovi.avib.morphing.projections.backend.organization.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import es.uniovi.avib.morphing.projections.backend.organization.repository.UserRepository;
import es.uniovi.avib.morphing.projections.backend.organization.configuration.SecurityConfig;
import es.uniovi.avib.morphing.projections.backend.organization.domain.User;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserRequestDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final RestTemplate restTemplate;
	private final UserRepository userRepository;
	private final SecurityConfig securityConfig;
	
	private final String REALM = "avib";
	
	public List<User> findAll() {		
		log.debug("findAll: found all users");
		
		return (List<User>) userRepository.findAll();		
	}
	
	public User findById(String userId) {
		log.debug("findById: found user with id: {}", userId);
		
		return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));	
	}
		
	public User save(User user) {
		log.debug("save: user");
		
		String url = "http://" + securityConfig.getHost() + ":" + securityConfig.getPort() + "/security"
				+ "/realms/" + REALM + "/users";

		UserRequestDto userRequestDto = UserRequestDto.builder()
			.enabled(user.isActive())
			.firstName(user.getFirstname())
			.lastName(user.getLastName())
			.email(user.getEmail())
			.username(user.getUsername())
			.password(user.getPassword())
			.realmRoles(new ArrayList<String>(Arrays.asList(user.getRole())))
		.build();
		
		// save user in IAM service
		ResponseEntity<Object> responseEntityStr = restTemplate.postForEntity(url, userRequestDto, Object.class);
		
		// set external Id
		user.setExternalId(responseEntityStr.getBody().toString());
		
		// save user in system DB
		return userRepository.save(user);
	}
	
	public void deleteById(String userId) {
		log.debug("deleteById: delete user with id: {}", userId);
		
		userRepository.deleteById(userId);
	}
	
	public User findByEmail(String email) {
		log.debug("findById: found user with id: {}", email);
		
		return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));	
	}
}
