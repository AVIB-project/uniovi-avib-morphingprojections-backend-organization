package es.uniovi.avib.morphing.projections.backend.organization.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import es.uniovi.avib.morphing.projections.backend.organization.repository.CaseRepository;
import es.uniovi.avib.morphing.projections.backend.organization.repository.ResourceRepository;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Case;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Organization;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Project;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Resource;
import es.uniovi.avib.morphing.projections.backend.organization.dto.CaseAdminDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.CaseDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.CaseProjectDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.OrganizationAdminDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.OrganizationDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.ProjectAminDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.ProjectDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.ResourceDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserCaseDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserOrganizationDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseService {
	private final CaseRepository caseRepository;
	private final ResourceRepository resourceRepository;

	private final UserService userService;
	private final ResourceService resourceService;
	
	private final MongoTemplate mongoTemplate;
	
	private final String ADMIN_ID = "ADMIN";
	
	public List<Case> findAll() {
		log.debug("findAll: found all cases");
		
		return (List<Case>) caseRepository.findAll();		
	}
	
	public CaseProjectDto findById(String caseId) {
		log.debug("findById: found case with id: {}", caseId);		
		
		AggregationOperation aggregationOperationProject = Aggregation
				.stage("""
						{
						    $lookup: {
								from: 'project',
								localField: 'project_id',
								foreignField: '_id',
								as: 'project'
						    }
						}
						""");
		
		AggregationOperation aggregationOperationUnwindProject = Aggregation
				.stage("""
						{
							$unwind: {
								"path": "$project",
								"preserveNullAndEmptyArrays": true
							}
						}																	
					   """);
		
		AggregationOperation aggregationOperationProjectProject = Aggregation
				.stage("""
						{
							$project: {
						        _id: 0,
						        caseId: "$_id",
						        projectId: "$project._id",
						        organizationId: "$project.organization_id",						        
						        name: 1,
						        description: 1,
						        type: 1,
						        creationDate: 1,
						        creationBy: 1,
						        updatedDate: 1,
						        updatedBy: 1
						    }
						}																	
					   """);
		
		AggregationOperation aggregationOperationMatchCase = Aggregation
				.match(Criteria.where("_id").is(new ObjectId(caseId)));
		
		Aggregation aggregation = Aggregation.newAggregation(
				aggregationOperationProject, 
				aggregationOperationUnwindProject,
				aggregationOperationMatchCase,
				aggregationOperationProjectProject
		);
		
		CaseProjectDto _case = mongoTemplate.aggregate(aggregation, "case", CaseProjectDto.class).getUniqueMappedResult();
		
		return _case;
	}
	
	public UserCaseDto findCasesByOrganizationAndUser(String organizationId, String userId) {
		log.debug("findCasesByUser: find cases from user id: {}", userId);
		
		// recover user from id
		UserDto userDto = userService.findById(userId);
		
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
							 $match: {
								$expr: {
								  $eq: [ 
								    '$_id', { $toObjectId: "$organizationId" } 
								  ] 
								}
							 }
						}											
					   """.replace("$organizationId", organizationId));	
		
		AggregationOperation aggregationOperationAdmin02 = Aggregation
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
		
		if (userDto.getRole().equals(ADMIN_ID)) {
			aggregation = Aggregation.newAggregation(aggregationOperationAdmin01, aggregationOperationAdmin02);
			
			List<OrganizationAdminDto> organizations = mongoTemplate.aggregate(aggregation, "organization", OrganizationAdminDto.class).getMappedResults();
			
			// create User Cases from organization configuration by user
			for (OrganizationAdminDto organization : organizations) {
				OrganizationDto organizationDto = OrganizationDto.builder()
						.organizationId(organization.getId())
						.name(organization.getName())
						.description(organization.getDescription())
						.build();
												
				for (ProjectAminDto project : organization.getProjects()) {
					ProjectDto projectDto = ProjectDto.builder()
							.projectId(project.getId())
							.name(project.getName())
							.description(project.getDescription())
							.build();
									
					for (CaseAdminDto cs : project.getCases()) {
						CaseDto caseDto = CaseDto.builder()
								.caseId(cs.getId())
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
							.organizationId(organization.getOrganizationId())
							.name(organization.getName())
							.description(organization.getDescription())
							.build();
													
					for (Project project : organization.getProjects()) {
						ProjectDto projectDto = ProjectDto.builder()
								.projectId(project.getProjectId())
								.name(project.getName())
								.description(project.getDescription())
								.build();
										
						for (Case cs : project.getCases()) {
							CaseDto caseDto = CaseDto.builder()
									.caseId(cs.getCaseId())
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
	
	public Case save(Case _case) {
		log.debug("save: save case");
		
		return caseRepository.save(_case);
	}
	
	public void deleteById(String caseId) {
		log.debug("deleteById: delete case with id: {}", caseId);
			
		// get all resources from case
		List<Resource> resources = resourceRepository.findByCaseId(new ObjectId(caseId));
		
		for (Resource resource : resources) {
			// delete resource from case in Minio
			resourceService.deleteById(resource.getResourceId());
		}
		
		// delete all resources from case
		resourceRepository.deleteAll(resources);
				
		caseRepository.deleteById(caseId);
	}
}
