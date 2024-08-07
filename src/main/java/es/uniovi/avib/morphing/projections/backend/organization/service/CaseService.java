package es.uniovi.avib.morphing.projections.backend.organization.service;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import es.uniovi.avib.morphing.projections.backend.organization.repository.CaseRepository;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Case;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Organization;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Project;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Resource;
import es.uniovi.avib.morphing.projections.backend.organization.dto.CaseDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.OrganizationDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.ProjectDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.ResourceDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserCaseDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserOrganizationDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseService {
	private final CaseRepository caseRepository;
	private final MongoTemplate mongoTemplate;
	private final String ADMIN_ID = "66a90828bfb5b24be6ab8210";
	
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
	
	public UserCaseDto findByUserAggregate(String userId) {
		log.debug("findById: found organizations with user id: {}", userId);
						
		AggregationOperation aggregationOperationUser01 = Aggregation
				.stage("""
						{
							 $match: {
								$expr: {
								  $eq: [ 
								    '$user_id', { $toObjectId: "$userId" } 
								  ] 
								}
							 }
						}											
					   """.replace("$userId", userId));		
		
		
		AggregationOperation aggregationOperationUser02 = Aggregation
				.stage("""
						{
							 $lookup: {
								from: 'organization',
								localField: 'organization_id',
								foreignField: '_id',
								as: 'organizations',
								pipeline: [
									{
										$lookup: {
						          			from: 'project',
											localField: '_id',
											foreignField: 'organization_id',
											as: 'projects',
											pipeline: [
										      {
											      $lookup: {
											      from: "case",
												  localField: "_id",
												  foreignField: "project_id",
												  as: "cases",
												  pipeline: [
												   {
												     $lookup: {
												      from: "resource",
												      localField: "_id",
												      foreignField: "case_id",
												      as: "resources",
												     },
												   }
												 ]
										        }
										      }
									       ]					
										}
									}
								]
							}
						}																	
					   """);	

		AggregationOperation aggregationOperationAdmin01 = Aggregation
				.stage("""
						{
						 $lookup: {
							  from: 'project',
							  localField: '_id',
							  foreignField: 'organization_id',
							  as: 'projects',
							  pipeline: [
							    {
							      $lookup: {
							        from: "case",
							        localField: "_id",
							        foreignField: "project_id",
							        as: "cases",
							        pipeline: [
							         {
							           $lookup: {
							            from: "resource",
							            localField: "_id",
							            foreignField: "case_id",
							            as: "resources",
							           },
							         }
							       ]
							      }
							    }
							  ]
							}								 
						}																	
					   """);
		
		Aggregation aggregation;
		UserCaseDto userCaseDto = new UserCaseDto();
		
		if (userId.equals(ADMIN_ID)) {
			aggregation = Aggregation.newAggregation(aggregationOperationAdmin01);
			
			List<OrganizationDto> organizations = mongoTemplate.aggregate(aggregation, "organization", OrganizationDto.class).getMappedResults();
			
			// create User Cases from organization configuration by user
			for (OrganizationDto organization : organizations) {
				OrganizationDto organizationDto = OrganizationDto.builder()
						.id(organization.getId())
						.name(organization.getName())
						.description(organization.getDescription())
						.build();
												
				for (ProjectDto project : organization.getProjects()) {
					ProjectDto projectDto = ProjectDto.builder()
							.id(project.getId())
							.name(project.getName())
							.description(project.getDescription())
							.build();
									
					for (CaseDto cs : project.getCases()) {
						CaseDto caseDto = CaseDto.builder()
								.id(cs.getId())
								.name(cs.getName())
								.description(cs.getDescription())
								.build();
						
						for (ResourceDto resource : cs.getResources()) {
							caseDto.getResources().add(ResourceDto
									.builder()
										.bucket(resource.getBucket())
										.file(resource.getFile())
										.description(resource.getDescription())
										.type(resource.getType())
									.build());
						}	
						
						projectDto.getCases().add(caseDto);
					}
					
					organizationDto.getProjects().add(projectDto);
				}
				
				userCaseDto.getOrganizations().add(organizationDto);			
					
			}			
		}
		else {
			aggregation = Aggregation.newAggregation(aggregationOperationUser01, aggregationOperationUser02);
			
			List<UserOrganizationDto> userOrganizationDtos = mongoTemplate.aggregate(aggregation, "user_organization", UserOrganizationDto.class).getMappedResults();
			
			// create User Cases from organization configuration by user
			for (UserOrganizationDto userOrganizationDto : userOrganizationDtos) {
				for (Organization organization : userOrganizationDto.getOrganizations()) {
					OrganizationDto organizationDto = OrganizationDto.builder()
							.id(organization.getOrganizationId())
							.name(organization.getName())
							.description(organization.getDescription())
							.build();
													
					for (Project project : organization.getProjects()) {
						ProjectDto projectDto = ProjectDto.builder()
								.id(project.getProjectId())
								.name(project.getName())
								.description(project.getDescription())
								.build();
										
						for (Case cs : project.getCases()) {
							CaseDto caseDto = CaseDto.builder()
									.id(cs.getCaseId())
									.name(cs.getName())
									.description(cs.getDescription())
									.build();
							
							for (Resource resource : cs.getResources()) {
								caseDto.getResources().add(ResourceDto
										.builder()
											.bucket(resource.getBucket())
											.file(resource.getFile())
											.description(resource.getDescription())
											.type(resource.getType())
										.build());
							}	
							
							projectDto.getCases().add(caseDto);
						}
						
						organizationDto.getProjects().add(projectDto);
					}
					
					userCaseDto.getOrganizations().add(organizationDto);			
				}			
			}				
		}	
				
		return userCaseDto;		
	}
}
