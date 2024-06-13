package es.uniovi.avib.morphing.projections.backend.organization.service;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Service;

import es.uniovi.avib.morphing.projections.backend.organization.repository.OrganizationRepository;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Case;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Organization;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Project;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Resource;
import es.uniovi.avib.morphing.projections.backend.organization.dto.CaseDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.OrganizationDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.ProjectDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.ResourceDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserCaseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationService {
	private final OrganizationRepository organizationRepository;
	private final MongoTemplate mongoTemplate;
	
	public List<Organization> findAll() {		
		log.debug("findAll: found all organizations");
		
		return (List<Organization>) organizationRepository.findAll();		
	}
	
	public Organization findById(String annotationId) {
		log.debug("findById: found organization with id: {}", annotationId);
		
		return organizationRepository.findById(annotationId).orElseThrow(() -> new RuntimeException("Annotation not found"));	
	}
		
	public Organization save(Organization organization) {
		log.debug("save: save organization");
		
		return organizationRepository.save(organization);
	}
	
	public void deleteById(String organizationId) {
		log.debug("deleteById: delete organization with id: {}", organizationId);
		
		organizationRepository.deleteById(organizationId);
	}
	
	public UserCaseDto findByUserAggregate(String userId) {
		log.debug("findById: found organizations with user id: {}", userId);
		
		AggregationOperation aggregationOperation = Aggregation
				.stage("""
						{
							$lookup:					
							{
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
							          },
							          {
							            $lookup: {
							              from: "user_case",
							              localField: "_id",
							              foreignField: "case_id",
							              as: "user_cases",
							              let: { user_id: "$user_id"},
							              pipeline: [
							                {
							                  $match: {
							                    $expr: {
							                       $and: [
							                        { 
							                          $eq: ["$user_id", "$userId"] 
							                        }
							                      ]
							                    }
							                  }
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
						""".replace("$userId", userId));
			
		Aggregation aggregation = Aggregation.newAggregation(aggregationOperation);
				
		List<Organization> organizations = mongoTemplate.aggregate(aggregation, "organization", Organization.class).getMappedResults();
		
		// create User Cases from organization configuration by user
		UserCaseDto userCaseDto = new UserCaseDto();		
		for (Organization organization : organizations) {
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
					if (cs.getUserCases().size() > 0) {
						CaseDto caseDto = CaseDto.builder()
								.id(cs.getCaseId())
								.name(cs.getName())
								.description(cs.getDescription())
								.build();
						
						projectDto.getCases().add(caseDto);											
														
						for (Resource resource : cs.getResources()) {
							caseDto.getResources().add(ResourceDto
									.builder()
										.bucket(resource.getBucket())
										.file(resource.getFile())
										.description(resource.getDescription())
										.type(resource.getType())
									.build());
						}										
					}
				}
				
				if (projectDto.getCases().size() > 0)
					organizationDto.getProjects().add(projectDto);
			}
			
			if (organizationDto.getProjects().size() > 0)
				userCaseDto.getOrganizations().add(organizationDto);			
		}
					
		return userCaseDto;
	}	
}
