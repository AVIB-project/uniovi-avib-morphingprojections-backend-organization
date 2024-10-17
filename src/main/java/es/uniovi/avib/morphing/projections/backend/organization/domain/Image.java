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

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Document(collection = "image")
public class Image {
	@Id	
	private String imageId;
				
	@NotNull(message = "Organization Id may not be null")
	@Field(name = "organization_id", targetType = FieldType.OBJECT_ID)	
	private String organizationId;

	@NotNull(message = "Name may not be null")
	@Field("name")
	private String name;
	
	@Field("description")
	private String description;
		
	@NotNull(message = "Image may not be null")
	@Field("image")
	private String image;
	
	@NotNull(message = "Version may not be null")
	@Field("version")
	private String version;

	@NotNull(message = "Environment may not be null")
	@Field("environment")
	private String environment;
	
	@NotNull(message = "Command may not be null")
	@Field("command")
	private String command;
	
	@Field("parameters")
	private List<String> parameters;
	
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