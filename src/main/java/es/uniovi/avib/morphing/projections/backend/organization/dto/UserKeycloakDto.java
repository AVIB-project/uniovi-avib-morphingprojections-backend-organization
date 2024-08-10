package es.uniovi.avib.morphing.projections.backend.organization.dto;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserKeycloakDto {
	private String userId;
	private String username;
	private String firstname;
	private String lastname;
	private String email;
	private String address;
	private String password;
	private List<String> realmRoles;
	private Map<String, List<String>> clientRoles;
	private Map<String, List<String>> attributes;
	private boolean enabled;
}
