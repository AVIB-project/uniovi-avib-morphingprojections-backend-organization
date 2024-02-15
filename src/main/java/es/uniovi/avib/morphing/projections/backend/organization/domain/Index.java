package es.uniovi.avib.morphing.projections.backend.organization.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "index")
public class Index {
	@Id	
	private String indexId;
			
	@NotNull(message = "Organoization Id may not be null")
	@Field("organization_id")
	private String organizationId;
	
	@NotNull(message = "Project Id may not be null")
	@Field("project_id")
	private String projectId;
	
	@NotNull(message = "Case Id may not be null")
	@Field("case_id")
	private String caseId;
	
	@NotNull(message = "Name may not be null")
	@Field("name")
	private String name;
	
	@NotNull(message = "Description may not be null")
	@Field("description")
	private String description;
	
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
