package es.uniovi.avib.morphing.projections.backend.organization.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import es.uniovi.avib.morphing.projections.backend.organization.repository.CaseRepository;
import es.uniovi.avib.morphing.projections.backend.organization.repository.OrganizationRepository;
import es.uniovi.avib.morphing.projections.backend.organization.repository.ProjectRepository;
import es.uniovi.avib.morphing.projections.backend.organization.repository.ResourceRepository;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Case;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Organization;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Project;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Resource;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationService {
	private final OrganizationRepository organizationRepository;
	private final ProjectRepository projectRepository;
	private final CaseRepository caseRepository;
	private final ResourceRepository resourceRepository;
	
	private final ResourceService resourceService;
	
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
		
		// get all projects from organization
		List<Project> projects = projectRepository.findByOrganizationId(new ObjectId(organizationId));		
		
		for (Project project : projects) {
			// get all cases from poject
			List<Case> cases = caseRepository.findByProjectId(new ObjectId(project.getProjectId()));
		
			for (Case _case : cases) {
				// get all resources from case
				List<Resource> resources = resourceRepository.findByCaseId(new ObjectId(_case.getCaseId()));
				
				for (Resource resource : resources) {
					// delete resource from case in Minio
					resourceService.deleteById(resource.getResourceId());
				}
				
				// delete all resources from case
				resourceRepository.deleteAll(resources);
			}
			
			// delete all cases from project
			caseRepository.deleteAll(cases);
		}

		// delete all projects from organization
		projectRepository.deleteAll(projects);
				
		// delete the organization
		organizationRepository.deleteById(organizationId);
	}
}
