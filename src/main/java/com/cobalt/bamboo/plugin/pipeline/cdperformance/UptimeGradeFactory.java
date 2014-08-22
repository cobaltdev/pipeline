package com.cobalt.bamboo.plugin.pipeline.cdperformance;

import java.util.Date;
import java.util.List;

import com.atlassian.bamboo.resultsummary.ResultsSummary;

public class UptimeGradeFactory {

	/**
	 * Return a UptimeGrade generated based on the given buildList, ignoring all
	 * builds that in queue but never started yet.
	 * @param buildList to calculate the uptime grade with
	 * @return UptimeGrade based on the given buildList.
	 */
	public static UptimeGrade createUptimeGrade(List<ResultsSummary> buildList) {
		if(buildList == null || buildList.size() <= 0){
			return new UptimeGrade(null, 0, false, null);
		}
		
		// Set startDate to be the first build with completed time.
		Date startDate = null;
		int startBuildIndex = buildList.size();
		while(startDate == null && startBuildIndex > 0){
			startBuildIndex--;
			startDate = buildList.get(startBuildIndex).getBuildCompletedDate();
		}
		
		// Find the most recent completed build.
		Date currentBuildDate = null;
		boolean currentBuildState = false;
		int currentBuildIndex = 0;
		while(currentBuildDate == null && currentBuildIndex < buildList.size()){
			currentBuildDate = buildList.get(currentBuildIndex).getBuildCompletedDate();
			currentBuildState = buildList.get(currentBuildIndex).isSuccessful();
			currentBuildIndex++;
		}
		
		// Calculate the uptime starting from the current build.
		long totalUptime = 0;
		if(currentBuildDate != null){
			long lastBuildTime = currentBuildDate.getTime();
			for(int i = currentBuildIndex; i <= startBuildIndex; i++){
				ResultsSummary currentBuild = buildList.get(i);
				Date currentBuildCompletedDate = currentBuild.getBuildCompletedDate();
				if(currentBuildCompletedDate != null){
					long currentBuildTime = currentBuild.getBuildCompletedDate().getTime();
					if(currentBuild.isSuccessful()){
						totalUptime += lastBuildTime - currentBuildTime;
					}
					lastBuildTime = currentBuildTime;
				}
			}
		}
		
		return new UptimeGrade(startDate, totalUptime, currentBuildState, currentBuildDate);
	}
}
