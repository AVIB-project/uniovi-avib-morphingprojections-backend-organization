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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import es.uniovi.avib.morphing.projections.backend.organization.domain.Case;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserCaseDto;
import es.uniovi.avib.morphing.projections.backend.organization.service.CaseService;

@Slf4j
@CrossOrigin(maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("cases")
public class CaseController {
	private final CaseService caseService;
	
	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json")
	public ResponseEntity<List<Case>> findAll() {
		List<Case> cases = (List<Case>) caseService.findAll();
					
		log.debug("findAll: found {} cases", cases.size());
		
		return new ResponseEntity<List<Case>>(cases, HttpStatus.OK);			
	}

	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json", value = "/{caseId}")	
	public ResponseEntity<Case> findById(@PathVariable String caseId) {
		Case _case = caseService.findById(caseId);
										
		log.debug("findById: found case with caseId: {}", caseId);
			
		return new ResponseEntity<Case>(_case, HttpStatus.OK);		
	}
	
	@RequestMapping(method = { RequestMethod.POST }, produces = "application/json")	
	public ResponseEntity<Case> save(@RequestBody Case _case) {		
		Case caseSaved = caseService.save(_case);

		log.debug("save: create/update case with caseId: {} from Manager Service", caseSaved.getCaseId());
			
		return new ResponseEntity<Case>(caseSaved, HttpStatus.OK);			
	}

	@RequestMapping(method = { RequestMethod.DELETE },value = "/{caseId}")	
	public void deleteById(@PathVariable String caseId) {
		log.debug("deleteById: remove case with caseId: {}", caseId);
			
		caseService.deleteById(caseId);					
	}
	
	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json", value = "/users/{userId}/aggregate")	
	public ResponseEntity<UserCaseDto> findByUserAggregate(@PathVariable String userId) {
		UserCaseDto userCaseDto = caseService.findByUserAggregate(userId);
										
		log.debug("findByUserAggregate: found user cases with userId: {}", userId);
			
		return new ResponseEntity<UserCaseDto>(userCaseDto, HttpStatus.OK);		
	}	
}
