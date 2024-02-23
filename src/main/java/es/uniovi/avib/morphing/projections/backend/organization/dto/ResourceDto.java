package es.uniovi.avib.morphing.projections.backend.organization.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResourceDto {
	private String file;
	private String description;
	private String type;
}
	