package es.uniovi.avib.morphing.projections.backend.organization.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import es.uniovi.avib.morphing.projections.backend.organization.repository.CaseRepository;
import es.uniovi.avib.morphing.projections.backend.organization.repository.ProjectRepository;
import es.uniovi.avib.morphing.projections.backend.organization.repository.ResourceRepository;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Case;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Project;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
	private final ProjectRepository projectRepository;
	private final CaseRepository caseRepository;
	private final ResourceRepository resourceRepository;
	
	private final ResourceService resourceService;
	
	public List<Project> findAll() {		
		log.debug("findAll: found all cases");
		
		return (List<Project>) projectRepository.findAll();		
	}
	
	public Project findById(String projectId) {
		log.debug("findById: found project with id: {}", projectId);
		 
		return projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));	
	}
	
	public List<Project> findByOrganizationId(String organizationId) {
		log.debug("findByOrganizationId: found project with organizationId: {}", organizationId);
		
		return projectRepository.findByOrganizationId(new ObjectId(organizationId));	
	}
	
	public Project save(Project project) {
		log.debug("save: save project");
		
		return projectRepository.save(project);
	}
	
	public void deleteById(String projectId) {
		log.debug("deleteById: delete Project with id: {}", projectId);
		
		// get all cases from project
		List<Case> cases = caseRepository.findByProjectId(new ObjectId(projectId));
	
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
		
		// delete the organization
		projectRepository.deleteById(projectId);
	}		
}
