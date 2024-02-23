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

import es.uniovi.avib.morphing.projections.backend.organization.domain.Resource;
import es.uniovi.avib.morphing.projections.backend.organization.dto.ResourceDto;
import es.uniovi.avib.morphing.projections.backend.organization.service.ResourceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("resources")
public class ResourceController {
	private final ResourceService resourceService;
	
	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json")
	public ResponseEntity<List<Resource>> findAll() {
		List<Resource> resources = (List<Resource>) resourceService.findAll();
					
		log.debug("findAll: found {} resources", resources.size());
		
		return new ResponseEntity<List<Resource>>(resources, HttpStatus.OK);			
	}

	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json", value = "/{resourceId}")	
	public ResponseEntity<Resource> findById(@PathVariable String resourceId) {
		Resource resource = resourceService.findById(resourceId);
										
		log.debug("findById: found resource with resourceId: {}", resourceId);
			
		return new ResponseEntity<Resource>(resource, HttpStatus.OK);		
	}
	
	@RequestMapping(method = { RequestMethod.POST }, produces = "application/json")	
	public ResponseEntity<Resource> save(@RequestBody Resource resource) {		
		Resource resourceSaved = resourceService.save(resource);

		log.debug("save: create/update resource with resource id: {} from Manager Service", resourceSaved.getResourceId());
			
		return new ResponseEntity<Resource>(resourceSaved, HttpStatus.OK);			
	}

	@RequestMapping(method = { RequestMethod.DELETE },value = "/{resourceId}")	
	public void deleteById(@PathVariable String resourceId) {
		log.debug("deleteById: remove resource with resourceId: {}", resourceId);
			
		resourceService.deleteById(resourceId);					
	}
	
	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json", value = "/cases/{caseId}")	
	public ResponseEntity<List<ResourceDto>> findByCase(@PathVariable String caseId) {
		List<ResourceDto> resourcesDto = resourceService.findByCaseId(caseId);
										
		log.debug("findById: find resources with caseId: {}", caseId);
			
		return new ResponseEntity<List<ResourceDto>>(resourcesDto, HttpStatus.OK);		
	}	
}
