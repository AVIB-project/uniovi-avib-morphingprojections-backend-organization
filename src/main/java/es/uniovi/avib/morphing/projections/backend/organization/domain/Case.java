package es.uniovi.avib.morphing.projections.backend.organization.domain;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

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
	@Field(name = "project_id", targetType = FieldType.OBJECT_ID)
	private String projectId;
	
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
	
	@NotNull(message = "Creation by may not be null")
	@Field("creation_by")
	@CreatedBy	
	private String creationBy;	
	
	@NotNull(message = "Creation Date may not be null")
	@Field("creation_date")
	@CreatedDate
	private Date creationDate;	
		
	@Field("updated_by")
	@LastModifiedBy		
	private String updatedBy;
	
	@Field("updated_date")
	@LastModifiedDate	
	private Date updatedDate;		
}
