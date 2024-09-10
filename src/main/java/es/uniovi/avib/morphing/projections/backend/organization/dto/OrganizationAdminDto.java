package es.uniovi.avib.morphing.projections.backend.organization.dto;

import java.util.ArrayList;
import java.util.List;

import es.uniovi.avib.morphing.projections.backend.organization.domain.Image;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrganizationAdminDto {
	private String id;
	private String name;
	private String description;
	private List<Image> image;
	@Builder.Default
	private List<ProjectAminDto> projects = new ArrayList<ProjectAminDto>();
}
