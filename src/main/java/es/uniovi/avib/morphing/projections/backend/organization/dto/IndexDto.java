package es.uniovi.avib.morphing.projections.backend.organization.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IndexDto {
	private String name;
	private String description;
	private String type;	
}
