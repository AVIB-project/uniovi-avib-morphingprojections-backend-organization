package es.uniovi.avib.morphing.projections.backend.organization.domain;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCaseResponse {
	private String organization;
	private String project;
	private String cs;
	private List<Index> index;
}
