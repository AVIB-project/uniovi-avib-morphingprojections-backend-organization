package es.uniovi.avib.morphing.projections.backend.organization.domain;

import java.util.Date;

import org.bson.types.ObjectId;
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
@Document(collection = "user_case")
public class UserCase {
	@Id	
	private String userCaseId;
	
	@NotNull(message = "Case Id may not be null")
	@Field("case_id")
	private ObjectId caseId;
	
	@NotNull(message = "User Id may not be null")
	@Field("user_id")
	private ObjectId userId;
	
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
