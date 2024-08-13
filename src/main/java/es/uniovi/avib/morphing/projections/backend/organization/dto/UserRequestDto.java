package es.uniovi.avib.morphing.projections.backend.organization.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequestDto {
	private String userId;
	private String organizationId;
	private String externalId;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private String address;
	private String city;	
	private String country;	
	private String phone;
	private String notes;
	private String language;	
	private String role;
	private boolean enabled;	
}
