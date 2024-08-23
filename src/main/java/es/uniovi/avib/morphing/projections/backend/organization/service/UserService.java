package es.uniovi.avib.morphing.projections.backend.organization.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import es.uniovi.avib.morphing.projections.backend.organization.repository.UserOrganizationRepository;
import es.uniovi.avib.morphing.projections.backend.organization.repository.UserRepository;
import es.uniovi.avib.morphing.projections.backend.organization.configuration.SecurityConfig;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Case;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Organization;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Project;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Resource;
import es.uniovi.avib.morphing.projections.backend.organization.domain.User;
import es.uniovi.avib.morphing.projections.backend.organization.domain.UserOrganization;
import es.uniovi.avib.morphing.projections.backend.organization.dto.CaseAdminDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.CaseDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.OrganizationAdminDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.OrganizationDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.ProjectAminDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.ProjectDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.ResourceDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserCaseDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserKeycloakDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserOrganizationDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserRequestDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final SecurityConfig securityConfig;
	
	private final RestTemplate restTemplate;
	private final MongoTemplate mongoTemplate;
	
	private final UserRepository userRepository;
	private final UserOrganizationRepository userOrganizationRepository;	

	private final String ADMIN_ID = "ADMIN";
	private final String DEF_REALM = "avib";
	private final String DEF_PASSWORD = "password";
	
	public List<User> findAll() {
		log.debug("findAll: find all users");
		
		return (List<User>) userRepository.findAll();		
	}
	
	public UserDto findById(String userId) {
		log.debug("findById: found user with id: {}", userId);
				
		AggregationOperation aggregationOperationOrganization = Aggregation
				.stage("""
						{
						    $lookup: {
						        from: "user_organization",
						        localField: '_id',
						        foreignField: "user_id",
						        as: 'organization',
						        pipeline: [{
						            "$project": {
						                "_id": 0,
										"organization_id": 1
						            }
						        }]
						    }
						}
						""");
		
		AggregationOperation aggregationOperationUnwindOrganizations = Aggregation
				.stage("""
						{
							$unwind: {
								"path": "$organization",
								"preserveNullAndEmptyArrays": true
							}
						}																	
					   """);
		
		
		AggregationOperation aggregationOperationProjectOrganization = Aggregation
				.stage("""
						{
							$project: {
						        _id: 0,
						        userId: "$_id",
						        externalId: 1,
						        firstName: 1,
						        lastName: 1,
						        username: 1,
						        email: 1,
						        language: 1,
						        address: 1,
						        city: 1,
						        country: 1,
						        phone: 1,
						        notes: 1,
						        role: 1,
						        active: 1,
						        creationDate: 1,
						        creationBy: 1,
						        updatedDate: 1,
						        updatedBy: 1,
						        organizationId: "$organization.organization_id"
						    }
						}																	
					   """);
		
		AggregationOperation aggregationOperationMatchOrganization = Aggregation
				.match(Criteria.where("userId").is(new ObjectId(userId)));
		
		Aggregation aggregation = Aggregation.newAggregation(
				aggregationOperationOrganization, 
				aggregationOperationUnwindOrganizations,
				aggregationOperationProjectOrganization,
				aggregationOperationMatchOrganization
				);
		
		List<UserDto> users = mongoTemplate.aggregate(aggregation, "user", UserDto.class).getMappedResults();
		
		if (users.size() > 0)
			return users.get(0);
		
		return null;
	}
	
	public UserDto findByExternalId(String externalId) {
		log.debug("findByExternalId: found user with id: {}", externalId);
				
		AggregationOperation aggregationOperationOrganization = Aggregation
				.stage("""
						{
						    $lookup: {
						        from: "user_organization",
						        localField: '_id',
						        foreignField: "user_id",
						        as: 'organization',
						        pipeline: [{
						            "$project": {
						                "_id": 0,
										"organization_id": 1
						            }
						        }]
						    }
						}
						""");
		
		AggregationOperation aggregationOperationUnwindOrganizations = Aggregation
				.stage("""
						{
							$unwind: {
								"path": "$organization",
								"preserveNullAndEmptyArrays": true
							}
						}																	
					   """);
		
		
		AggregationOperation aggregationOperationProjectOrganization = Aggregation
				.stage("""
						{
							$project: {
						        _id: 0,
						        userId: "$_id",
						        externalId: 1,
						        firstName: 1,
						        lastName: 1,
						        username: 1,
						        email: 1,
						        language: 1,
						        address: 1,
						        city: 1,
						        country: 1,
						        phone: 1,
						        notes: 1,
						        role: 1,
						        active: 1,
						        creationDate: 1,
						        creationBy: 1,
						        updatedDate: 1,
						        updatedBy: 1,
						        organizationId: "$organization.organization_id"
						    }
						}																	
					   """);
		
		AggregationOperation aggregationOperationMatchOrganization = Aggregation
				.match(Criteria.where("externalId").is(externalId));
		
		Aggregation aggregation = Aggregation.newAggregation(
				aggregationOperationOrganization, 
				aggregationOperationUnwindOrganizations,
				aggregationOperationProjectOrganization,
				aggregationOperationMatchOrganization
				);
		
		List<UserDto> users = mongoTemplate.aggregate(aggregation, "user", UserDto.class).getMappedResults();
		
		if (users.size() > 0)
			return users.get(0);
		
		return null;
	}
	
	public List<UserDto> findAllByOrganizationId(String organizationId) {
		log.debug("findAllByOrganizationId: find all users by organization with id {}", organizationId);
						
		AggregationOperation aggregationOperationOrganization = Aggregation
				.stage("""
						{
						    $lookup: {
						        from: "user_organization",
						        localField: '_id',
						        foreignField: "user_id",
						        as: 'organization',
						        pipeline: [{
						            "$project": {
						                "_id": 0,
										"organization_id": 1
						            }
						        }]
						    }
						}
						""");
		
		AggregationOperation aggregationOperationUnwindOrganizations = Aggregation
				.stage("""
						{
							$unwind: {
								"path": "$organization",
								"preserveNullAndEmptyArrays": true
							}
						}																	
					   """);
		
		
		AggregationOperation aggregationOperationProjectOrganization = Aggregation
				.stage("""
						{
							$project: {
						        _id: 0,
						        userId: "$_id",
						        externalId: 1,
						        firstName: 1,
						        lastName: 1,
						        username: 1,
						        email: 1,
						        language: 1,
						        address: 1,
						        city: 1,
						        country: 1,
						        phone: 1,
						        notes: 1,
						        role: 1,
						        active: 1,
						        creationDate: 1,
						        creationBy: 1,
						        updatedDate: 1,
						        updatedBy: 1,
						        organizationId: "$organization.organization_id"
						    }
						}																	
					   """);
		
		AggregationOperation aggregationOperationMatchOrganization = Aggregation
				.match(Criteria.where("organizationId").is(new ObjectId(organizationId)));
		
		Aggregation aggregation = Aggregation.newAggregation(
				aggregationOperationOrganization, 
				aggregationOperationUnwindOrganizations,
				aggregationOperationProjectOrganization,
				aggregationOperationMatchOrganization
				);
		
		List<UserDto> users = mongoTemplate.aggregate(aggregation, "user", UserDto.class).getMappedResults();
		
		return users;		
	}
	
	public User findByEmail(String email) {
		log.debug("findById: found user with id: {}", email);
		
		return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));	
	}
	
	public UserCaseDto findCasesByUser(String userId) {
		log.debug("findCasesByUser: find cases from user id: {}", userId);
						
		// recover user from id
		UserDto userDto = findById(userId);
		
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
		
		if (userDto!= null && userDto.getRole().equals(ADMIN_ID)) {
			aggregation = Aggregation.newAggregation(aggregationOperationAdmin01);
			
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
	
	public User save(UserRequestDto userRequestDto) {
		log.debug("save: user");

		// save user in Keycloack
		String url = "http://" + securityConfig.getHost() + ":" + securityConfig.getPort() + "/security/realms/" + DEF_REALM + "/users";

		UserKeycloakDto userKeycloakDto = null;
		if (userRequestDto.getExternalId().isEmpty()) {			
			userKeycloakDto = UserKeycloakDto.builder()
				.username(userRequestDto.getUsername())
				.password(DEF_PASSWORD)
				.firstName(userRequestDto.getFirstName())
				.lastName(userRequestDto.getLastName())
				.email(userRequestDto.getEmail())				
				.realmRoles(new ArrayList<String>(Arrays.asList(userRequestDto.getRole())))
				.enabled(true)
			.build();
		} else {
			userKeycloakDto = UserKeycloakDto.builder()
					.username(userRequestDto.getUsername())
					.firstName(userRequestDto.getFirstName())
					.lastName(userRequestDto.getLastName())
					.email(userRequestDto.getEmail())				
					.realmRoles(new ArrayList<String>(Arrays.asList(userRequestDto.getRole())))
					.enabled(true)
			.build();
		}
		
		ResponseEntity<String> responseEntityStr = null;
		if (userRequestDto.getExternalId().isEmpty())
			responseEntityStr = restTemplate.postForEntity(url, userKeycloakDto, String.class);
		else {
			restTemplate.put(url + "/" + userRequestDto.getExternalId(), userKeycloakDto);
		}			
		
		// save user in system	
		User user = User.builder()
				.firstName(userRequestDto.getFirstName())
				.lastName(userRequestDto.getLastName())
				.username(userRequestDto.getUsername())
				.email(userRequestDto.getEmail())
				.language(userRequestDto.getLanguage())
				.address(userRequestDto.getAddress())
				.city(userRequestDto.getCity())
				.country(userRequestDto.getCountry())
				.phone(userRequestDto.getPhone())
				.notes(userRequestDto.getNotes())
				.role(userRequestDto.getRole())
				.creationDate(new Date())
				.creationBy("Administrator")
				.active(true)
		.build();
			
		if (responseEntityStr != null)
			user.setExternalId(responseEntityStr.getBody().toString());
		else
			user.setExternalId(userRequestDto.getExternalId());
		
		if (userRequestDto.getUserId() != null)
			user.setUserId(userRequestDto.getUserId());
		
		User userSaved = userRepository.save(user);
		
		// bind organization to user for not admin users role
		if (userRequestDto.getUserId() == null && !userRequestDto.getRole().equals("admin")) {			
			UserOrganization userOrganization = UserOrganization.builder()
					.organizationId(userRequestDto.getOrganizationId())
					.userId(userSaved.getUserId())
					.creationDate(new Date())
					.creationBy("Administrator")
			.build();
			
			userOrganizationRepository.save(userOrganization);
		}
		
		// save user in System DB
		return userSaved;
	}
	
	public void deleteById(String userId) throws Exception {
		log.debug("deleteById: delete user with id: {}", userId);

		// recover system user
		Optional<User> user = userRepository.findById(userId);
		
		if (user.isEmpty()) {
			throw new Exception("User not exist");
		}
				
		// delete Keycloack user
		String url = "http://" + securityConfig.getHost() + ":" + securityConfig.getPort() + "/security/realms/" + DEF_REALM + "/users/" + user.get().getExternalId();
				
		restTemplate.delete(url);
		
		// delete system user
		userRepository.delete(user.get());
		
		// delete user system organization binds
		List<UserOrganization> userOrganizations = userOrganizationRepository.findByUserId(new ObjectId(user.get().getUserId()));
		userOrganizationRepository.deleteAll(userOrganizations);
	}

	public void resetPassword(String userId, String password) throws Exception {
		log.debug("resetPassword");

		// recover system user
		Optional<User> user = userRepository.findById(userId);
		
		if (user.isEmpty()) {
			throw new Exception("User not exist");
		}
						
		// reset password Keycloack user
		String url = "http://" + securityConfig.getHost() + ":" + securityConfig.getPort() + "/security/realms/" + DEF_REALM + "/users/" + user.get().getExternalId() + "/resetPassword";
				
		restTemplate.postForEntity(url, password, Void.class);
		
	}
}
