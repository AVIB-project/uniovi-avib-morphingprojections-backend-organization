package es.uniovi.avib.morphing.projections.backend.organization.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequestDto {
	private String organizationId;
	private String username;
	private String firstname;
	private String lastname;
	private String email;
	private String address;
	private String password;
	private String role;
	private boolean enabled;	
}