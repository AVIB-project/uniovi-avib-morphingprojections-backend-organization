package es.uniovi.avib.morphing.projections.backend.organization.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IndexResponse {
	private String name;
	private String description;
	private String type;
	
}
