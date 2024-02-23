package es.uniovi.avib.morphing.projections.backend.organization.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import es.uniovi.avib.morphing.projections.backend.organization.domain.Resource;
import es.uniovi.avib.morphing.projections.backend.organization.dto.ResourceDto;
import es.uniovi.avib.morphing.projections.backend.organization.repository.ResourceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
	private final ResourceRepository resourceRepository;
	private final MongoTemplate mongoTemplate;
	
	public List<Resource> findAll() {		
		log.debug("findAll: found all resources");
		
		return (List<Resource>) resourceRepository.findAll();		
	}
	
	public Resource findById(String resourceId) {
		log.debug("findById: found resource with id: {}", resourceId);
		
		return resourceRepository.findById(resourceId).orElseThrow(() -> new RuntimeException("Resource not found"));	
	}
		
	public Resource save(Resource resource) {
		log.debug("save: save resource");
		
		return resourceRepository.save(resource);
	}
	
	public void deleteById(String resourceId) {
		log.debug("deleteById: delete resource with id: {}", resourceId);
		
		resourceRepository.deleteById(resourceId);
	}
	
	public List<ResourceDto> findByCaseId(String caseId) {
		log.debug("finfByCaseId: find resources by case id: {}", caseId);
		
		AggregationOperation aggregationOperation = Aggregation
				.match(Criteria.where("case_id").is(new ObjectId(caseId)));
			
		Aggregation aggregation = Aggregation.newAggregation(aggregationOperation);
		
		List<Resource> resources = mongoTemplate.aggregate(aggregation, "resource", Resource.class).getMappedResults();
		
		// create resources collection from case id 
		List<ResourceDto> resourcesDto = new ArrayList<ResourceDto>();	
		for (Resource resource : resources) {					
			resourcesDto.add(ResourceDto.builder()
					.file(resource.getFile())
					.description(resource.getDescription())
					.type(resource.getType())
					.build());
		
		}
									
		return resourcesDto;
	}	
}
