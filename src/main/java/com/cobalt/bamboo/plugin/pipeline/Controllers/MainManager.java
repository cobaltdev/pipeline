package com.cobalt.bamboo.plugin.pipeline.Controllers;

import java.util.List;

import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.TopLevelPlan;
import com.cobalt.bamboo.plugin.pipeline.cdperformance.CDPerformance;
import com.cobalt.bamboo.plugin.pipeline.cdperformance.UptimeGrade;
import com.cobalt.bamboo.plugin.pipeline.cdresult.CDResult;
import com.cobalt.bamboo.plugin.pipeline.changelist.Change;

public interface MainManager {
	
	/**
	 * Get all the results needed for displaying the CDPipeline table.
	 * 
	 * @return a list of CDResults, where each CDResult represents one row.
	 *         See CDResults for more details.
	 */
	public List<CDResult> getCDResults();
	
	/**
	 * Get the result needed for displaying the CDPipeline table for the plan
	 * specified by the given playkey.
	 * @param planKey of the plan to look for
	 * @return the CDResult of the given plan. Return null if no plan can be
	 *         found for the given plankey.
	 */
	public CDResult getCDResultForPlan(String planKey);
	
	/**
	 * Get a list of Changes since the last pipeline completion for the plan 
	 * specified by the given plankey.
	 * 
	 * @param planKey of the plan to look for
	 * @return the list of changes since last pipeline completion for the given plan.
	 *         List may be empty if there are no changes. Return null if no plan can
	 *         be found from the given plankey.
	 */
	public List<Change> getChangeListForPlan(String planKey);
	
	/**
	 * Get a CDPerformance for the plan specified by the given plankey.
	 * If no plan found or no build in the plan, return null.
	 * @param planKey planKey of the plan to look for
	 * @return the continuous delivery performance statistics for the given plan.
	 *         Return null if no plan can be found for the given plankey or no build
	 *         in the plan.
	 */
	public CDPerformance getPerformanceStatsForPlan(String planKey);
	
	/**
	 * Get a UptimeGrade for the plan specified by the given planKey.
	 * If no plan found or no build in the plan, return null.
	 * 
	 * @param planKey planKey of the plan to look for
	 * @return the UptimeGrade, representing the uptime percentage, of the plan.
	 *         Return null if no plan can be found for the given plankey or no build
	 *         in the plan.
	 */
	public UptimeGrade getUptimeGradeForPlan(String planKey);
	
	/**
	 * Get all TopLevelPlans directly from Bamboo.
	 * @return a List of TopLevelPlan
	 */
	public List<TopLevelPlan> getAllPlans();
}
