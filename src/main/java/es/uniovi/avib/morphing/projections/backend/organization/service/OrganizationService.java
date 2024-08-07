package es.uniovi.avib.morphing.projections.backend.organization.service;

import java.util.List;

import org.springframework.stereotype.Service;

import es.uniovi.avib.morphing.projections.backend.organization.repository.OrganizationRepository;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Organization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationService {
	private final OrganizationRepository organizationRepository;
	
	public List<Organization> findAll() {		
		log.debug("findAll: found all organizations");
		
		return (List<Organization>) organizationRepository.findAll();		
	}
	
	public Organization findById(String annotationId) {
		log.debug("findById: found organization with id: {}", annotationId);
		
		return organizationRepository.findById(annotationId).orElseThrow(() -> new RuntimeException("Annotation not found"));	
	}
		
	public Organization save(Organization organization) {
		log.debug("save: save organization");
		
		return organizationRepository.save(organization);
	}
	
	public void deleteById(String organizationId) {
		log.debug("deleteById: delete organization with id: {}", organizationId);
		
		organizationRepository.deleteById(organizationId);
	}
}
