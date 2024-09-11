package es.uniovi.avib.morphing.projections.backend.organization.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobDto {
	private String name;	
	private String version;
	private String image;
	private String state;
	private String createdBy;
	private Date createdDate;
}
