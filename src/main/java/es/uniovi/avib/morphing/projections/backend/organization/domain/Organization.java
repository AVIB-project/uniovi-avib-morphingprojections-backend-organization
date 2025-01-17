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

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "organization")
public class Organization {
	@Id	
	private String organizationId;

	@NotNull(message = "Name may not be null")
	@Field("name")
	private String name;
	
	@Field("description")
	private String description;
	
	@Field("projects")
	private List<Project> projects;
	
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
