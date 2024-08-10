package es.uniovi.avib.morphing.projections.backend.organization.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import es.uniovi.avib.morphing.projections.backend.organization.repository.CaseRepository;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Case;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseService {
	private final CaseRepository caseRepository;
	
	public List<Case> findAll() {		
		log.debug("findAll: found all cases");
		
		return (List<Case>) caseRepository.findAll();		
	}
	
	public Case findById(String caseId) {
		log.debug("findById: found case with id: {}", caseId);
		
		return caseRepository.findById(caseId).orElseThrow(() -> new RuntimeException("Case not found"));	
	}
		
	public Case save(Case _case) {
		log.debug("save: save case");
		
		return caseRepository.save(_case);
	}
	
	public void deleteById(String caseId) {
		log.debug("deleteById: delete case with id: {}", caseId);
		
		caseRepository.deleteById(caseId);
	}
}
