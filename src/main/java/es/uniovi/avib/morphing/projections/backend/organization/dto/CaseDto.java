package es.uniovi.avib.morphing.projections.backend.organization.dto;

import java.util.ArrayList;
import java.util.List;

import es.uniovi.avib.morphing.projections.backend.organization.domain.Image;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CaseDto {
	private String caseId;
	private String projectId;
	private String name;
	private String description;
	private Image image;
	@Builder.Default
	private List<ResourceDto> resources = new ArrayList<ResourceDto>();
}
	