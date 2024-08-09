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
@Document(collection = "user")
public class User {
	@Id	
	private String userId;

	@NotNull(message = "Name may not be null")
	@Field("external_id")
	private String externalId;
	
	@NotNull(message = "Firstname may not be null")
	@Field("firstname")
	private String firstname;
	
	@NotNull(message = "Lastname may not be null")
	@Field("lastname")
	private String lastName;	
	
	@NotNull(message = "Username may not be null")
	@Field("username")
	private String username;
	
	@NotNull(message = "Password may not be null")
	@Field("password")
	private String password;
	
	@NotNull(message = "Email may not be null")
	@Field("email")
	private String email;
	
	@NotNull(message = "Role may not be null")
	@Field("role")
	private String role;	
	
	@NotNull(message = "Active may not be null")
	@Field("active")
	private boolean active;	
	
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
