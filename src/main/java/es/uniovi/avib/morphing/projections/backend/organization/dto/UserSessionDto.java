package es.uniovi.avib.morphing.projections.backend.organization.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSessionDto {
	private String id;
	private String userId;
	private String username;
	private String ipAddress;
	private long start;
	private long lastAccess;
}
