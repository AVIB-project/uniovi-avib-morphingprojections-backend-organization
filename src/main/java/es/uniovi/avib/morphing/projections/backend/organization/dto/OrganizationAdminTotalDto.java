package es.uniovi.avib.morphing.projections.backend.organization.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrganizationAdminTotalDto {
	private String id;
	@Builder.Default
	private List<ProjectAminTotalDto> projects = new ArrayList<ProjectAminTotalDto>();
	@Builder.Default
	private List<UserDto> users = new ArrayList<UserDto>();	
}
