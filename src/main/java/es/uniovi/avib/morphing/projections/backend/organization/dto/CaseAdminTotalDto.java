package es.uniovi.avib.morphing.projections.backend.organization.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CaseAdminTotalDto {
	private String id;
	@Builder.Default
	private List<JobDto> jobs = new ArrayList<JobDto>();	
	@Builder.Default
	private List<ResourceDto> resources = new ArrayList<ResourceDto>();
}
	