package es.uniovi.avib.morphing.projections.backend.organization.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class CaseUserDto {
	private List<OrganizationDto> organizations = new ArrayList<OrganizationDto>();
}