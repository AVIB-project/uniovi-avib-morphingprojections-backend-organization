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
import es.uniovi.avib.morphing.projections.backend.organization.domain.Case;
import es.uniovi.avib.morphing.projections.backend.organization.dto.CaseProjectDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseService {
	private final CaseRepository caseRepository;
	private final MongoTemplate mongoTemplate;
	
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
	
	public Case save(Case _case) {
		log.debug("save: save case");
		
		return caseRepository.save(_case);
	}
	
	public void deleteById(String caseId) {
		log.debug("deleteById: delete case with id: {}", caseId);
		
		caseRepository.deleteById(caseId);
	}
}
