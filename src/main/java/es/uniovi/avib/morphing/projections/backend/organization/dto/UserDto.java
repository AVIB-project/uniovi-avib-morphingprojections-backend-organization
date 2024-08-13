package es.uniovi.avib.morphing.projections.backend.organization.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {
	  private String userId;
	  private String externalId;
	  private String organizationId;	  
	  private String firstName;
	  private String lastName;
	  private String username;
	  private String email;
	  private String language;
	  private String address;
	  private String city;
	  private String country;
	  private String phone;
	  private String notes;
	  private String role;
	  private String active;
	  private Date creationDate;
	  private String creationBy;
	  private Date updatedDate;
	  private String updatedBy; 
}
