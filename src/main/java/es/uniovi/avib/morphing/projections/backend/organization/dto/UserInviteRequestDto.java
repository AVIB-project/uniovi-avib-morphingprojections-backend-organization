package es.uniovi.avib.morphing.projections.backend.organization.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInviteRequestDto {
	private String email;
	private String organizationId;
	private boolean enabled;	
}
