package es.uniovi.avib.morphing.projections.backend.organization.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import es.uniovi.avib.morphing.projections.backend.organization.repository.UserOrganizationRepository;
import es.uniovi.avib.morphing.projections.backend.organization.repository.UserRepository;
import es.uniovi.avib.morphing.projections.backend.organization.configuration.SecurityConfig;
import es.uniovi.avib.morphing.projections.backend.organization.domain.User;
import es.uniovi.avib.morphing.projections.backend.organization.domain.UserOrganization;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserKeycloakDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserRequestDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final RestTemplate restTemplate;
	private final UserRepository userRepository;
	private final UserOrganizationRepository userOrganizationRepository;
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
		
	public User findByEmail(String email) {
		log.debug("findById: found user with id: {}", email);
		
		return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));	
	}
	
	public User save(UserRequestDto userRequestDto) {
		log.debug("save: user");

		// save user in Keycloack
		String url = "http://" + securityConfig.getHost() + ":" + securityConfig.getPort() + "/security/realms/" + REALM + "/users";

		UserKeycloakDto userKeycloakDto = UserKeycloakDto.builder()
				.firstname(userRequestDto.getFirstname())
				.lastname(userRequestDto.getLastname())
				.username(userRequestDto.getUsername())
				.password(userRequestDto.getPassword())
				.email(userRequestDto.getEmail())				
				.realmRoles(new ArrayList<String>(Arrays.asList(userRequestDto.getRole())))
				.enabled(true)
		.build();
		
		ResponseEntity<String> responseEntityStr = restTemplate.postForEntity(url, userKeycloakDto, String.class);
		
		// save user in system DB	
		User user = User.builder()
				.firstName(userRequestDto.getFirstname())
				.lastName(userRequestDto.getLastname())
				.username(userRequestDto.getUsername())
				.email(userRequestDto.getEmail())		
				.role(userRequestDto.getRole())
				.externalId(responseEntityStr.getBody().toString())
				.creationDate(new Date())
				.creationBy("Administrator")
				.active(true)
		.build();
			
		User userSaved = userRepository.save(user);
		
		// bind organization to user for not admin users
		if (!userRequestDto.getRole().equals("admin")) {			
			UserOrganization userOrganization = UserOrganization.builder()
					.organizationId(new ObjectId(userRequestDto.getOrganizationId()))
					.userId(new ObjectId(userSaved.getUserId()))
					.creationDate(new Date())
					.creationBy("Administrator")
			.build();
			
			userOrganizationRepository.save(userOrganization);
		}
		
		// save user in System DB
		return userSaved;
	}
	
	public void deleteById(String userId) throws Exception {
		log.debug("deleteById: delete user with id: {}", userId);

		// recover system user
		Optional<User> user = userRepository.findById(userId);
		
		if (user.isEmpty()) {
			throw new Exception("User not exist");
		}
				
		// delete Keycloack user
		String url = "http://" + securityConfig.getHost() + ":" + securityConfig.getPort() + "/security/realms/" + REALM + "/users/" + user.get().getExternalId();
				
		restTemplate.delete(url);
		
		// delete system user
		userRepository.delete(user.get());
		
		// delete user system organization binds
		List<UserOrganization> userOrganizations = userOrganizationRepository.findByUserId(user.get().getUserId());
		userOrganizationRepository.deleteAll(userOrganizations);
	}
}
