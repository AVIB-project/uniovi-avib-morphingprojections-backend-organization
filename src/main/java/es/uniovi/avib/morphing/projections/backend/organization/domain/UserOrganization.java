package es.uniovi.avib.morphing.projections.backend.organization.domain;

import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@Document(collection = "user_organization")
public class UserOrganization {
	@Id	
	private String userOrganizationId;
	
	@NotNull(message = "Organization Id may not be null")
	@Field(name = "organization_id", targetType = FieldType.OBJECT_ID)
	private String organizationId;
	
	@NotNull(message = "User Id may not be null")
	@Field(name = "user_id", targetType = FieldType.OBJECT_ID)
	private String userId;
	
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
