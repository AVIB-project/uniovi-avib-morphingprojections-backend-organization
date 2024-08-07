package es.uniovi.avib.morphing.projections.backend.organization.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import es.uniovi.avib.morphing.projections.backend.organization.domain.Project;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
}
