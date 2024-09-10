package es.uniovi.avib.morphing.projections.backend.organization.dto;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Image;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Organization;

@Getter
@Builder
public class UserOrganizationDto {
	private String id;
	private String organization_id;
	private String user_id;
	private String createdBy;
	private Date createdDate;
	private List<Image> image;
	private List<Organization> organizations;
}
	