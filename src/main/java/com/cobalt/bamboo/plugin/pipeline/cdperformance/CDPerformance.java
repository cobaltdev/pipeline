package com.cobalt.bamboo.plugin.pipeline.cdperformance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CDPerformance {
	private int totalBuild;
	private int totalSuccess;
	private int numChanges;
	private List<CompletionStats> completions;
	private double totalDays;
	
	/**
	 * Construct a CDPerformance with given information
	 * Throw IllegalArgumentException if totalBuild < totalSuccess or startDate
	 * after lastCompletionDate
	 * @param totalBuild total number of builds of this plan
	 * @param totalSuccess total number of successes of this plan
	 * @param numChanges total number of changes before the most recent completion
	 * @param startDate completed date of the first build
	 * @param lastCompletionDate completed date of the most recent completion
	 * @param completions all completions within this plan
	 */
	public CDPerformance(int totalBuild, int totalSuccess, int numChanges, Date startDate, Date lastCompletionDate,
						List<CompletionStats> completions){
		if(totalBuild < totalSuccess){
			throw new IllegalArgumentException("Total successes should not be greater than total builds");
		}
		this.totalBuild = totalBuild;
		this.totalSuccess = totalSuccess;
		this.numChanges = numChanges;
		this.completions = new ArrayList<CompletionStats>(completions);
		if(startDate != null && lastCompletionDate != null){
			if(lastCompletionDate.compareTo(startDate) < 0){
				throw new IllegalArgumentException("Last completion date should not be before start date");
			}
			this.totalDays = (lastCompletionDate.getTime() - startDate.getTime()) * 1.0 / (1000 * 60 * 60 * 24);
		}else{
			totalDays = -1;
		}
		
	}
	
	/**
	 * Return success percentage calculated by total successes / total builds, 
	 * rounded to 2 decimal places
	 * If no build, return 0.
	 * @return success percentage of this performance statistics, return 0 when
	 *         there's no build
	 */
	public double getSuccessPercentage(){
		if (totalBuild <= 0) {
			return 0;
		}
		double percent = totalSuccess * 1.0 / totalBuild;
		return Math.round(percent * 100.0) / 100.0;	
	}
	
	/**
	 * Return average changes from the creation of this plan to the recent completion,
	 * calculated by total changes / total completions,
	 * rounded to 2 decimal places
	 * If there's no completions, return -1
	 * @return average changes between completions
	 */
	public double getAverageChanges() {
		if (completions.size() > 0) {
			return numChanges * 1.0 / completions.size();
		} else {
			return -1;
		}
	}

	public double getSuccessPercentageFormatted(){
		if (totalBuild <= 0) {
			return 0;
		}
		return totalSuccess * 1.0 / totalBuild;
	}
	
	/**
	 * Return average frequency of completion from the creation of this plan to the
	 * recent completion in days, calculated by total days / total completions,
	 * rounded to 2 decimal places
	 * If there's no completions, return -1
	 * @return average days between completions
	 */
	public double getAverageFrequency() {
		if (completions.size() > 0) {
			return totalDays * 1.0 / completions.size();
		} else {
			return -1;
		}
	}

	/**
	 * Return all completions of this performance statistics
	 * @return list of completions of this performance statistics
	 */
	public List<CompletionStats> getCompletions() {
		return Collections.unmodifiableList(completions);
	}
	
}
