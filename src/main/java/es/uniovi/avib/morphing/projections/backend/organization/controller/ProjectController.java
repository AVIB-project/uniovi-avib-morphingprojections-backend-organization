package es.uniovi.avib.morphing.projections.backend.organization.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.uniovi.avib.morphing.projections.backend.organization.domain.Project;
import es.uniovi.avib.morphing.projections.backend.organization.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("projects")
public class ProjectController {
	private final ProjectService projectService;
	
	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json")
	public ResponseEntity<List<Project>> findAll() {
		List<Project> projects = (List<Project>) projectService.findAll();
					
		log.debug("findAll: found {} organizations", projects.size());
		
		return new ResponseEntity<List<Project>>(projects, HttpStatus.OK);			
	}

	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json", value = "/organizations/{organizationId}")
	public ResponseEntity<List<Project>> findByOrganizationId(@PathVariable String organizationId) {
		List<Project> projects = (List<Project>) projectService.findByOrganizationId(organizationId);
					
		log.debug("findAll: found {} organizations", projects.size());
		
		return new ResponseEntity<List<Project>>(projects, HttpStatus.OK);			
	}
	
	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json", value = "/{projectId}")	
	public ResponseEntity<Project> findById(@PathVariable String projectId) {
		Project project = projectService.findById(projectId);
										
		log.debug("findById: found project with projectId: {}", projectId);
			
		return new ResponseEntity<Project>(project, HttpStatus.OK);		
	}
	
	@RequestMapping(method = { RequestMethod.POST }, produces = "application/json")	
	public ResponseEntity<Project> save(@RequestBody Project project) {		
		Project projectSaved = projectService.save(project);

		log.debug("save: create/update project with projectId: {} from Manager Service", projectSaved.getProjectId());
			
		return new ResponseEntity<Project>(projectSaved, HttpStatus.OK);			
	}

	@RequestMapping(method = { RequestMethod.DELETE },value = "/{projectId}")	
	public void deleteById(@PathVariable String projectId) {
		log.debug("deleteById: remove project with projectId: {}", projectId);
			
		projectService.deleteById(projectId);					
	}
}
