package es.uniovi.avib.morphing.projections.backend.organization.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectAminDto {
	private String id;	
	private String name;
	private String description;
	@Builder.Default
	private List<CaseAdminDto> cases = new ArrayList<CaseAdminDto>();
}
