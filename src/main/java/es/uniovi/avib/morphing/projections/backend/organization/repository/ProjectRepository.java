package es.uniovi.avib.morphing.projections.backend.organization.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import es.uniovi.avib.morphing.projections.backend.organization.domain.Project;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
	List<Project> findByOrganizationId(ObjectId organizationId);
}
