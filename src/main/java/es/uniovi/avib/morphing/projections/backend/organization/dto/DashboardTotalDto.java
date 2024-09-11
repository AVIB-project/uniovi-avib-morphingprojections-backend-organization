package es.uniovi.avib.morphing.projections.backend.organization.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardTotalDto {
	private int totalUsers;
	private int totalCases;
	private int totalResources;
	private int totalJobs;
}
	