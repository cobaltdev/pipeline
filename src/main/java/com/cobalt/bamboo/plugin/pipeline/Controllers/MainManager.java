package com.cobalt.bamboo.plugin.pipeline.Controllers;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.bamboo.applinks.JiraApplinksService;
import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.PlanExecutionManager;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.plan.TopLevelPlan;
import com.atlassian.bamboo.project.Project;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;
import com.cobalt.bamboo.plugin.pipeline.cdresult.CDResult;
import com.cobalt.bamboo.plugin.pipeline.cdresult.CDResultFactory;
import com.cobalt.bamboo.plugin.pipeline.cdresult.ContributorBuilder;

/**
 * The main controller of CDPipeline Plugin Project that handles getting the
 * results needed for displaying.
 */
public class MainManager {
	private PlanManager planManager;
	private ResultsSummaryManager resultsSummaryManager;
	private PlanExecutionManager planExecutionManager;
	private ContributorBuilder contributorBuilder;
	
	/**
	 * Constructs a MainManager object.
	 * 
	 * @param planManager The PlanMananger (within Bamboo) to get information about plans.
	 * @param resultsSummaryManager The ResultsSummaryManager (within Bamboo) to get information
	 *                              about builds.
	 * @param jiraApplinksService The JiraApplinksService (within Bamboo) to get information
	 *                            about the application link to Jira.
	 * @param planExecutionManager The PlanExecutionManager to get information about a currently
	 *                             building build.
	 */
	public MainManager(PlanManager planManager, 
			           ResultsSummaryManager resultsSummaryManager,
			           JiraApplinksService jiraApplinksService, 
			           PlanExecutionManager planExecutionManager) {
        if (planManager == null || resultsSummaryManager == null || planExecutionManager == null) {
            throw new IllegalArgumentException("Null arguments not allowed");
        }
        
		this.planManager = planManager;
		this.resultsSummaryManager = resultsSummaryManager;
		this.planExecutionManager = planExecutionManager;
		this.contributorBuilder = new ContributorBuilder(jiraApplinksService);
	}
	
	/**
	 * Get all the results needed for displaying the CDPipeline table.
	 * 
	 * @return a list of CDResults, where each CDResult represents one row.
	 *         See CDResults for more details.
	 */
	public List<CDResult> getCDResults() {
		List<CDResult> resultList = new ArrayList<CDResult>();
		
		List<TopLevelPlan> plans = planManager.getAllPlans(TopLevelPlan.class);
			for (Plan plan : plans) {
				String planName = plan.getName();
				String planKey = plan.getKey();
				
				Project project = plan.getProject();
				String projectName = project.getName();
				String projectKey = project.getKey();
				
				List<ResultsSummary> buildList = resultsSummaryManager.getResultSummariesForPlan(plan, 0, 0);
				
				CDResult result = CDResultFactory.createCDResult(projectName, planName, projectKey, planKey, 
																buildList, contributorBuilder, planExecutionManager);
				resultList.add(result);
			}
		
		return resultList;
	}
}
