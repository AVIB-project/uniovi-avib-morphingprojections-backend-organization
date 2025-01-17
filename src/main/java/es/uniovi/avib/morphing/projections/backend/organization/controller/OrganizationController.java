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

import es.uniovi.avib.morphing.projections.backend.organization.domain.Organization;
import es.uniovi.avib.morphing.projections.backend.organization.service.OrganizationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("organizations")
public class OrganizationController {
	private final OrganizationService organizationService;
	
	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json")
	public ResponseEntity<List<Organization>> findAll() {
		List<Organization> organizations = (List<Organization>) organizationService.findAll();
					
		log.debug("findAll: found {} organizations", organizations.size());
		
		return new ResponseEntity<List<Organization>>(organizations, HttpStatus.OK);			
	}

	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json", value = "/{organizationId}")	
	public ResponseEntity<Organization> findById(@PathVariable String organizationId) {
		Organization organization = organizationService.findById(organizationId);
										
		log.debug("findById: found organization with organizationId: {}", organizationId);
			
		return new ResponseEntity<Organization>(organization, HttpStatus.OK);		
	}
	
	@RequestMapping(method = { RequestMethod.POST }, produces = "application/json")	
	public ResponseEntity<Organization> save(@RequestBody Organization organization) {		
		Organization organizationSaved = organizationService.save(organization);

		log.debug("save: create/update organization with annotationId: {} from Manager Service", organizationSaved.getOrganizationId());
			
		return new ResponseEntity<Organization>(organizationSaved, HttpStatus.OK);			
	}

	@RequestMapping(method = { RequestMethod.DELETE },value = "/{organizationId}")	
	public void deleteById(@PathVariable String organizationId) {
		log.debug("deleteById: remove organization with organizationId: {}", organizationId);
			
		organizationService.deleteById(organizationId);					
	}
}
