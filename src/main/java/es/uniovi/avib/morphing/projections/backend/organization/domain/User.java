package es.uniovi.avib.morphing.projections.backend.organization.domain;

import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Document(collection = "user")
public class User {
	@Id	
	private String userId;

	@NotNull(message = "Name may not be null")
	@Field("externalId")
	private String externalId;
	
	@NotNull(message = "First name may not be null")
	@Field("firstName")
	private String firstName;
	
	@NotNull(message = "Last name may not be null")
	@Field("lastName")
	private String lastName;	
	
	@NotNull(message = "Username may not be null")
	@Field("username")
	private String username;
		
	@NotNull(message = "Email may not be null")
	@Field("email")
	private String email;

	@NotNull(message = "Language may not be null")
	@Field("language")
	private String language;	

	@Field("address")
	private String address;	
	
	@Field("city")
	private String city;	
	
	@Field("country")
	private String country;	

	@Field("phone")
	private String phone;	

	@Field("notes")
	private String notes;	
	
	@NotNull(message = "Role may not be null")
	@Field("role")
	private String role;	
	
	@NotNull(message = "Active may not be null")
	@Field("active")
	private boolean active;	
		
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
