package es.uniovi.avib.morphing.projections.backend.organization.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectAminTotalDto {
	private String id;
	@Builder.Default
	private List<CaseAdminTotalDto> cases = new ArrayList<CaseAdminTotalDto>();
}
