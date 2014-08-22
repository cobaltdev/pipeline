package com.cobalt.bamboo.plugin.pipeline.Controllers;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.bamboo.applinks.JiraApplinksService;
import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.PlanExecutionManager;
import com.atlassian.bamboo.plan.PlanKey;
import com.atlassian.bamboo.plan.PlanKeys;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.plan.TopLevelPlan;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;
import com.cobalt.bamboo.plugin.pipeline.cdperformance.CDPerformance;
import com.cobalt.bamboo.plugin.pipeline.cdperformance.CDPerformanceFactory;
import com.cobalt.bamboo.plugin.pipeline.cdperformance.UptimeGrade;
import com.cobalt.bamboo.plugin.pipeline.cdperformance.UptimeGradeFactory;
import com.cobalt.bamboo.plugin.pipeline.cdresult.CDResult;
import com.cobalt.bamboo.plugin.pipeline.cdresult.CDResultFactory;
import com.cobalt.bamboo.plugin.pipeline.cdresult.ContributorBuilder;
import com.cobalt.bamboo.plugin.pipeline.changelist.Change;
import com.cobalt.bamboo.plugin.pipeline.changelist.ChangeListFactory;

/**
 * The main controller of CDPipeline Plugin Project that handles getting the
 * results needed for displaying.
 */
public class MainManagerImpl implements MainManager {
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
	public MainManagerImpl(PlanManager planManager, 
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
	
	@Override
	public List<CDResult> getCDResults() {
		List<CDResult> resultList = new ArrayList<CDResult>();
		
		List<TopLevelPlan> plans = planManager.getAllPlans(TopLevelPlan.class);
		for (Plan plan : plans) {
			
			CDResult result = CDResultFactory.createCDResult(plan, contributorBuilder, planExecutionManager, resultsSummaryManager);
			resultList.add(result);
		}
		
		return resultList;
	}
	
	@Override
	public List<Change> getChangeListForPlan(String planKey) {
		PlanKey planKeyObj = PlanKeys.getPlanKey(planKey); 
		Plan plan = planManager.getPlanByKey(planKeyObj);
		
		if (plan == null) {
			return null;
		}
		
		List<ResultsSummary> buildList = resultsSummaryManager.getResultSummariesForPlan(plan, 0, 0);
		
		return ChangeListFactory.buildChangeList(buildList, contributorBuilder);
	}
	
	@Override
	public CDPerformance getPerformanceStatsForPlan(String planKey) {
		PlanKey planKeyObj = PlanKeys.getPlanKey(planKey);
		Plan plan = planManager.getPlanByKey(planKeyObj);
		
		if(plan == null){
			return null;
		}
		
		List<ResultsSummary> buildList = resultsSummaryManager.getResultSummariesForPlan(plan, 0, 0);
		
		return CDPerformanceFactory.createCDPerformance(buildList, contributorBuilder);
	}
	
	@Override
	public UptimeGrade getUptimeGradeForPlan(String planKey) {
		PlanKey planKeyObj = PlanKeys.getPlanKey(planKey);
		Plan plan = planManager.getPlanByKey(planKeyObj);
		
		if(plan == null){
			return null;
		}
		
		List<ResultsSummary> buildList = resultsSummaryManager.getResultSummariesForPlan(plan, 0, 0);
		
		return UptimeGradeFactory.createUptimeGrade(buildList);
	}

	@Override
	public CDResult getCDResultForPlan(String planKey) {
		PlanKey planKeyObj = PlanKeys.getPlanKey(planKey);
		Plan plan = planManager.getPlanByKey(planKeyObj);
		
		if (plan == null) {
			return null;
		}
		
		return CDResultFactory.createCDResult(plan, contributorBuilder, planExecutionManager, resultsSummaryManager);
	}

	@Override
	public List<TopLevelPlan> getAllPlans() {
		return planManager.getAllPlans(TopLevelPlan.class);
	}
}
