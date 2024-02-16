package es.uniovi.avib.morphing.projections.backend.organization.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCaseDto {
	@JsonProperty("organization")
	private OrganizationDto organizationDto;
	@JsonProperty("project")
	private ProjectDto projectDto;
	@JsonProperty("case")
	private CaseDto caseDto;
}
