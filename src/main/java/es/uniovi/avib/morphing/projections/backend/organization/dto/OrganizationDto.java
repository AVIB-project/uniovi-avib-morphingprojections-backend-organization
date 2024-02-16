package es.uniovi.avib.morphing.projections.backend.organization.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrganizationDto {
	private String name;
	private String description;
}
