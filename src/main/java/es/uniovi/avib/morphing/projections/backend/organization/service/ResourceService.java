package es.uniovi.avib.morphing.projections.backend.organization.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import es.uniovi.avib.morphing.projections.backend.organization.configuration.StorageConfig;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Resource;
import es.uniovi.avib.morphing.projections.backend.organization.dto.ResourceDto;
import es.uniovi.avib.morphing.projections.backend.organization.repository.ResourceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
	private final RestTemplate restTemplate;
	private final StorageConfig storageConfig;
	
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
					.bucket(resource.getBucket())
					.file(resource.getFile())
					.description(resource.getDescription())
					.type(resource.getType())
					.createdBy(resource.getCreationBy())
					.createdDate(resource.getCreationDate())
					.build());		
		}
									
		return resourcesDto;
	}
	    
    public Object uploadFiles(String organizationId, String projectId, String caseId, String type, String description, MultipartFile[] files) {
		log.info("update files from service");
				
		// put object into minio
		String url = "http://" + storageConfig.getHost() + ":" + storageConfig.getPort() + "/storage"
			+ "/organizations/" + organizationId
			+ "/projects/" + projectId
			+ "/cases/" + caseId;
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		Arrays.asList(files).forEach(file -> {
			// save resource		
			Resource resource = Resource.builder()
					.bucket(organizationId + "/" + projectId + "/" + caseId)
					.file(file.getOriginalFilename())
					.caseId(new ObjectId(caseId))
					.type(type)
					.description(description)
					.creationBy("Administrator")
					.creationDate(new Date())
					.build();
			
			save(resource);
			
			// put resource object in object storage
			body.add("file[]", file.getResource());			    					
		});
		
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);		
		
		ResponseEntity<Object> responseEntityStr = restTemplate.postForEntity(url, requestEntity, Object.class);
		
		return responseEntityStr.getBody();			
	}
}
