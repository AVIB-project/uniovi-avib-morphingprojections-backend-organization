package es.uniovi.avib.morphing.projections.backend.organization.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CaseDto {
	private String name;
	private String description;
	private List<IndexDto> indices;
}
