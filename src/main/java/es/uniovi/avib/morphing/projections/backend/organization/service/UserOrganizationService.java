package es.uniovi.avib.morphing.projections.backend.organization.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import es.uniovi.avib.morphing.projections.backend.organization.repository.UserOrganizationRepository;
import es.uniovi.avib.morphing.projections.backend.organization.domain.UserOrganization;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserOrganizationService {
	private final UserOrganizationRepository userOrganizationRepository;
	
	public List<UserOrganization> findAll() {
		log.debug("findAll: found all users");
		
		return (List<UserOrganization>) userOrganizationRepository.findAll();		
	}
	
	public UserOrganization findById(String userOrganizationId) {
		log.debug("findById: found user with id: {}", userOrganizationId);
		
		return userOrganizationRepository.findById(userOrganizationId).orElseThrow(() -> new RuntimeException("User Organization not found"));	
	}
		
	public UserOrganization save(UserOrganization userOrganization) {
		log.debug("save: save user");
		
		return userOrganizationRepository.save(userOrganization);
	}
	
	public void deleteById(String userOrganizationId) {
		log.debug("deleteById: delete user organization with id: {}", userOrganizationId);
		
		userOrganizationRepository.deleteById(userOrganizationId);
	}		
}
