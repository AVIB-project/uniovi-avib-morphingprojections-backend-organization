package es.uniovi.avib.morphing.projections.backend.organization.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Organization;
import es.uniovi.avib.morphing.projections.backend.organization.domain.User;

@Getter
@Builder
public class OrganizationUserTotalDto {
	private String id;	
	private List<Organization> organizations;
	private List<User> users;	
}
	