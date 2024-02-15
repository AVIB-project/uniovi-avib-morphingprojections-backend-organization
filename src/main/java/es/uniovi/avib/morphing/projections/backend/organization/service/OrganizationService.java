package es.uniovi.avib.morphing.projections.backend.organization.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import es.uniovi.avib.morphing.projections.backend.organization.repository.OrganizationRepository;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Case;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Organization;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Project;
import es.uniovi.avib.morphing.projections.backend.organization.domain.UserCaseResponse;

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
	
	public List<Organization> findAllAggregate() {		
		log.debug("findAll: found all organizations");
		
		AggregationOperation casesOperation = Aggregation
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
						               from: "index",
						               localField: "_id",
						               foreignField: "case_id",
						               as: "indices",
						            },
						          },
						          {
						            $lookup: {
						              from: "user_case",
						              localField: "_id",
						              foreignField: "case_id",
						              as: "user_cases"							                
						            }
						          }            
						        ]       						        
						      }
						    }
						  ]						  
						}
					}								
					""");
		
		Aggregation aggregation = Aggregation.newAggregation(casesOperation);
			
		return mongoTemplate.aggregate(aggregation, "organization", Organization.class).getMappedResults();
	}
	
	public Organization findByIdAggregate(String organizationId) {
		log.debug("findById: found organization with id: {}", organizationId);
		
		AggregationOperation organizationnOperation = Aggregation
				.match(Criteria.where("_id").is(organizationId));
		
		AggregationOperation casesOperation = Aggregation
				.stage("""
						{
							$lookup:					
							{
							  from: 'project',
							  localField: '_id',
							  foreignField: 'organization_id',
							  as: 'projects'
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
							                from: "index",
							                localField: "_id",
							                foreignField: "case_id",
							                as: "indices",
							              },
							            },
							            {
							              $lookup: {
							                from: "user_case",
							                localField: "_id",
							                foreignField: "case_id",
							                as: "user_cases",
							              }
							            }            
							         ]							       
							      }
							    }
							  ]							  
							}
						}								
						""");
		
		Aggregation aggregation = Aggregation.newAggregation(organizationnOperation, casesOperation);
						
		List<Organization> organizations = mongoTemplate.aggregate(aggregation, "organization", Organization.class).getMappedResults();
			
		if (organizations.size() > 0)
			return organizations.get(0);
					
		return null;
	}
	
	public List<UserCaseResponse> findByUserAggregate(String userId) {
		log.debug("findById: found organizations with user id: {}", userId);
		
		AggregationOperation casesOperation = Aggregation
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
							              from: "index",
							              localField: "_id",
							              foreignField: "case_id",
							              as: "indices",
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
			
		Aggregation aggregation = Aggregation.newAggregation(casesOperation);
				
		List<Organization> organizations = mongoTemplate.aggregate(aggregation, "organization", Organization.class).getMappedResults();
		
		List<UserCaseResponse> userCaseResponse = new ArrayList<UserCaseResponse>();		
		for (Organization organization : organizations) { 
			for (Project project : organization.getProjects()) {
				for (Case cs : project.getCases()) {
					if (cs.getUserCases().size() > 0) {
						userCaseResponse.add(
								UserCaseResponse.builder()
									.organization(organization.getDescription())
									.project(project.getDescription())
									.cs(cs.getDescription())
									.index(cs.getIndices())
								.build());						
					}
				}
			}
		}
					
		return userCaseResponse;
	}	
}
