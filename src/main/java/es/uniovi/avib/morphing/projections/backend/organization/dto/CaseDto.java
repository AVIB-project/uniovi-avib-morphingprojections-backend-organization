package es.uniovi.avib.morphing.projections.backend.organization.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CaseDto {
	private String name;
	private String description;
	@Builder.Default
	private List<IndexDto> indices = new ArrayList<IndexDto>();
}
	