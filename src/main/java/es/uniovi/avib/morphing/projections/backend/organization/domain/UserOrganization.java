package es.uniovi.avib.morphing.projections.backend.organization.domain;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
	@Field("organization_id")
	private ObjectId organizationId;
	
	@NotNull(message = "User Id may not be null")
	@Field("user_id")
	private ObjectId userId;
		
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
