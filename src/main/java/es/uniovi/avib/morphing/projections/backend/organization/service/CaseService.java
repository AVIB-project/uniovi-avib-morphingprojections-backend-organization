package es.uniovi.avib.morphing.projections.backend.organization.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import es.uniovi.avib.morphing.projections.backend.organization.repository.CaseRepository;
import es.uniovi.avib.morphing.projections.backend.organization.repository.ResourceRepository;
import es.uniovi.avib.morphing.projections.backend.organization.configuration.AnnotationConfig;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Case;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Organization;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Project;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Resource;
import es.uniovi.avib.morphing.projections.backend.organization.dto.CaseAdminDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.CaseAdminTotalDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.CaseDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.CaseProjectDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.OrganizationAdminDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.OrganizationAdminTotalDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.OrganizationDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.ProjectAminDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.ProjectAminTotalDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.ProjectDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.ResourceDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.DashboardTotalDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.JobDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.CaseUserDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.OrganizationUserDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.OrganizationUserTotalDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseService {
	private final CaseRepository caseRepository;
	private final AnnotationConfig annotationConfig;	
	private final ResourceRepository resourceRepository;

	private final UserService userService;
	private final ResourceService resourceService;
	
	private final MongoTemplate mongoTemplate;
	private final RestTemplate restTemplate;
	
	private final String ADMIN_ID = "ADMIN";
	
	public List<Case> findAll() {
		log.debug("findAll: found all cases");
		
		return (List<Case>) caseRepository.findAll();		
	}
	
	public CaseProjectDto findById(String caseId) {
		log.debug("findById: found case with id: {}", caseId);		

		AggregationOperation aggregationOperationImage = Aggregation
				.stage("""
						{
						    $lookup: {
								from: 'image',
								localField: 'image_id',
								foreignField: '_id',
								as: 'image'
						    }
						}
						""");
		
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
						        imageId: "$image._id",
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
				aggregationOperationImage,
				aggregationOperationProject, 
				aggregationOperationUnwindProject,
				aggregationOperationMatchCase,
				aggregationOperationProjectProject
		);
		
		CaseProjectDto _case = mongoTemplate.aggregate(aggregation, "case", CaseProjectDto.class).getUniqueMappedResult();
		
		return _case;
	}
	
	public CaseUserDto findCasesByOrganizationAndUser(String organizationId, String userId) {
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
							  from: 'image',
							  localField: 'organization_id',
							  foreignField: 'organization_id',
							  as: 'image'
							}
						}
					   """);
		
		AggregationOperation aggregationOperationUser03 = Aggregation
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
							  from: 'image',
							  localField: '_id',
							  foreignField: 'organization_id',
							  as: 'image'
							}
						}
					   """);

		AggregationOperation aggregationOperationAdmin03 = Aggregation
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
		CaseUserDto userCaseDto = new CaseUserDto();
		
		if (userDto.getRole().equals(ADMIN_ID)) {
			aggregation = Aggregation.newAggregation(aggregationOperationAdmin01, aggregationOperationAdmin02, aggregationOperationAdmin03);
			
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
								.image(organization.getImage().get(0))
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
			aggregation = Aggregation.newAggregation(aggregationOperationUser01, aggregationOperationUser02, aggregationOperationUser03);
			
			List<OrganizationUserDto> userOrganizationDtos = mongoTemplate.aggregate(aggregation, "user_organization", OrganizationUserDto.class).getMappedResults();
			
			// create User Cases from organization configuration by user
			for (OrganizationUserDto userOrganizationDto : userOrganizationDtos) {
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
									.image(userOrganizationDto.getImage().get(0))
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
	
	public DashboardTotalDto findTotalCasesByOrganizationAndUser(String organizationId, String userId) {
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
							  from: 'image',
							  localField: 'organization_id',
							  foreignField: 'organization_id',
							  as: 'image'
							}
						}
					   """);
		
		AggregationOperation aggregationOperationUser03 = Aggregation
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
												       from: "job",
												       localField: "_id",
												       foreignField: "case_id",
												       as: "jobs",
												     },
												   },												  
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
							  from: 'image',
							  localField: '_id',
							  foreignField: 'organization_id',
							  as: 'image'
							}
						}
					   """);

		AggregationOperation aggregationOperationAdmin03 = Aggregation
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
							             from: "job",
							             localField: "_id",
							             foreignField: "case_id",
							             as: "jobs",
							           },
							         },
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

		AggregationOperation aggregationOperationAdmin04 = Aggregation
				.stage("""
						 {						 						
						 	$lookup: {
							  from: 'user_organization',
							  localField: '_id',
							  foreignField: 'organization_id',
							  as: 'users',
							}								 
						}																	
					   """);
		
		Aggregation aggregation;
		
		int totalCases = 0;
		int totalResources = 0;
		int totalJobs = 0;
		int totalUsers = 0;
		
		if (userDto.getRole().equals(ADMIN_ID)) {
			aggregation = Aggregation.newAggregation(aggregationOperationAdmin01, aggregationOperationAdmin02, aggregationOperationAdmin03, aggregationOperationAdmin04);
			
			List<OrganizationAdminTotalDto> organizations = mongoTemplate.aggregate(aggregation, "organization", OrganizationAdminTotalDto.class).getMappedResults();
			
			// create User Cases from organization configuration by user
			for (OrganizationAdminTotalDto organization : organizations) {
					if (organization.getUsers() != null) {
						totalUsers = organization.getUsers().size();
					}
				
					if (organization.getProjects() != null) {
						for (ProjectAminTotalDto project : organization.getProjects()) {
							if (project.getCases() != null) {
								for (CaseAdminTotalDto cs : project.getCases()) {
									totalCases = totalCases + 1;
									
									if (cs.getJobs() != null) {
										for (@SuppressWarnings("unused") JobDto job : cs.getJobs()) {
											totalJobs = totalJobs + 1;
										}
									}
									
									if (cs.getResources() != null) {
										for (@SuppressWarnings("unused") ResourceDto resource : cs.getResources()) {
											totalResources = totalResources + 1;
										}							
									}
								}
							}
						}
				}
			}			
		}
		else {
			aggregation = Aggregation.newAggregation(aggregationOperationUser01, aggregationOperationUser02, aggregationOperationUser03);
			
			List<OrganizationUserTotalDto> userOrganizationDtos = mongoTemplate.aggregate(aggregation, "user_organization", OrganizationUserTotalDto.class).getMappedResults();
			
			// create User Cases from organization configuration by user
			for (OrganizationUserTotalDto userOrganizationDto : userOrganizationDtos) {
				//totalUsers = userOrganizationDtos.size();
				
				if (userOrganizationDto.getOrganizations() != null) {
					for (Organization organization : userOrganizationDto.getOrganizations()) {
						if (organization.getProjects() != null) {
							for (Project project : organization.getProjects()) {
								if (project.getCases() != null) {
									for (Case cs : project.getCases()) {
										totalCases = totalCases + 1;
										
										if (cs.getResources() != null) {
											for (@SuppressWarnings("unused") Resource resource : cs.getResources()) {
												totalResources = totalResources + 1;
											}
										}
									}
								}
							}
						}
					}
				}
			}				
		}	
				
		DashboardTotalDto resourceTotalDto = DashboardTotalDto.builder()
				.totalCases(totalCases)
				.totalJobs(totalJobs)
				.totalResources(totalResources)
				.totalUsers(totalUsers)
			.build();
				
		return resourceTotalDto;
	}
	
	public Case save(Case _case) {
		log.debug("save: save case");
		
		return caseRepository.save(_case);
	}
	
	public void deleteById(String caseId) {
		log.debug("deleteById: delete case with id: {}", caseId);

		// delete all annotations from case				
		String url = "http://" + annotationConfig.getHost() + ":" + annotationConfig.getPort() + "/annotations/cases/" + caseId;
					  
		restTemplate.delete(url);
		
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
