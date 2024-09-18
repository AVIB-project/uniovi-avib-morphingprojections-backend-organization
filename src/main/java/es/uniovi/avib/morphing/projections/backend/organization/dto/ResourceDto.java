package es.uniovi.avib.morphing.projections.backend.organization.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResourceDto {
	private String bucket;
	private String file;
	private String description;
	private String type;
	private String createdBy;
	private Date creationDate;
	private String updatedBy;
	private Date updatedDate;	
}
	