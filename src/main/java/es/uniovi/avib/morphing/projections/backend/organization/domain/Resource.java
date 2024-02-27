package es.uniovi.avib.morphing.projections.backend.organization.domain;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Document(collection = "resource")
public class Resource {
	@Id	
	private String resourceId;
				
	@NotNull(message = "Case Id may not be null")
	@Field("case_id")
	private ObjectId caseId;

	@NotNull(message = "Bucket may not be null")
	@Field("bucket")
	private String bucket;
	
	@NotNull(message = "File may not be null")
	@Field("file")
	private String file;
	
	@NotNull(message = "Description may not be null")
	@Field("description")
	private String description;
	
	@NotNull(message = "Type may not be null")
	@Field("type")
	private String type;
	
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
