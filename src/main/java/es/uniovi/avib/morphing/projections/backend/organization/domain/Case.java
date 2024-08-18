package es.uniovi.avib.morphing.projections.backend.organization.domain;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "case")
public class Case {
	@Id	
	private String caseId;

	@NotNull(message = "Project Id may not be null")
	@Field("project_id")
	private ObjectId projectId;
	
	@NotNull(message = "Name may not be null")
	@Field("name")
	private String name;
	
	@Field("description")
	private String description;
	
	@Field("type")
	private String type;
	
	@Field("resources")
	List<Resource> resources;
	
	@Field("user_cases")
	List<UserCase> userCases;
	
	@NotNull(message = "Creation Date may not be null")
	@Field("creation_date")
	private Date creationDate;	
	
	@NotNull(message = "Creation by may not be null")
	@Field("creation_by")
	private String creationBy;	
	
	@Field("updated_date")
	private Date updatedDate;	
	
	@Field("updated_by")
	private String updatedBy;	
}
