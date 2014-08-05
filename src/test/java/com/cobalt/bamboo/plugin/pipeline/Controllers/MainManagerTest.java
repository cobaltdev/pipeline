package com.cobalt.bamboo.plugin.pipeline.Controllers;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.bamboo.applinks.JiraApplinksService;
import com.atlassian.bamboo.plan.PlanExecutionManager;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.plan.TopLevelPlan;
import com.atlassian.bamboo.project.Project;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MainManagerTest {
	private MainManager main;
	private PlanManager planMgr;
	private ResultsSummaryManager resultsSumMgr;
	
	@Before
	public void setUp() {
		planMgr = mock(PlanManager.class);
		resultsSumMgr = mock(ResultsSummaryManager.class);
	}
	
    @Test(expected = IllegalArgumentException.class)
    public void testGetCDResultsWithNullArguments() {
        main = new MainManager(null, null, null, null);
    }
    
    @Test
    public void testGetCDResultsWithNoPlansBySize() {
    	testWithEmptyBuildListBySize(0, 0);
    }
    
    @Test
    public void testGetCDResultsWithOnePlansBySize() {
    	testWithEmptyBuildListBySize(1, 1);
    }
    
    @Test
    public void testGetCDResultsWithSeveralPlansBySize() {
    	testWithEmptyBuildListBySize(5, 5);
    }
    
    @Test
    public void testGetCDResultsProjectPlanNameAndKey() {
    	List<TopLevelPlan> plans = createNPlans(1);
    	when(planMgr.getAllPlans(TopLevelPlan.class)).thenReturn(plans);
    	createEmptyResultsSummaryManager(plans);
    	main = new MainManager(planMgr, resultsSumMgr, setUpJiraApplinksService(), mock(PlanExecutionManager.class));
    	
    	assertEquals("Project name of the cdresult doesn't match.", "Project", 
    					main.getCDResults().get(0).getProjectName());
    	assertEquals("Project key of the cdresult doesn't match.", "projectkey", 
					main.getCDResults().get(0).getProjectKey());
    	assertEquals("Plan name of the cdresult doesn't match.", "Plan 0", 
					main.getCDResults().get(0).getPlanName());
    	assertEquals("Plan key of the cdresult doesn't match.", "plankey0", 
					main.getCDResults().get(0).getPlanKey());
    }
    
    // ========== Private Helper Methods =========
    
    private void testWithEmptyBuildListBySize(int numPlans, int expected) {
    	List<TopLevelPlan> plans = createNPlans(numPlans);
    	when(planMgr.getAllPlans(TopLevelPlan.class)).thenReturn(plans);
    	createEmptyResultsSummaryManager(plans);
    	main = new MainManager(planMgr, resultsSumMgr, setUpJiraApplinksService(), mock(PlanExecutionManager.class));
    	assertEquals("The count of CDResult list does not match", expected, main.getCDResults().size());
    }
    
    private List<TopLevelPlan> createNPlans(int N) {
    	List<TopLevelPlan> plans = new ArrayList<TopLevelPlan>();
    	for (int i = 0; i < N; i++) {
    		TopLevelPlan plan = mock(TopLevelPlan.class);
        	when(plan.getName()).thenReturn("Project" + " - Plan " + i);
        	when(plan.getKey()).thenReturn("plankey" + i);
        	
        	Project project = mock(Project.class);
        	when(plan.getProject()).thenReturn(project);
        	when(project.getName()).thenReturn("Project");
        	when(project.getKey()).thenReturn("projectkey");
        	
        	plans.add(plan);
    	}
    	
    	return plans;
    }
    
    private void createEmptyResultsSummaryManager(List<TopLevelPlan> plans) {
		for (TopLevelPlan pl : plans) {
			when(resultsSumMgr.getResultSummariesForPlan(pl, 0, 0)).thenReturn(new ArrayList<ResultsSummary>());
		}
    }
    
	// Mock up JiraApplinksService that is mainly used in ContributorBuilder
	private JiraApplinksService setUpJiraApplinksService() {
		JiraApplinksService jiraApplinks = mock(JiraApplinksService.class);
	
		Iterable<ApplicationLink> applinks = (Iterable<ApplicationLink>) mock(Iterable.class);
	  	when(jiraApplinks.getJiraApplicationLinks()).thenReturn(applinks);
	
			Iterator<ApplicationLink> applinksIter = (Iterator<ApplicationLink>) mock(Iterator.class);
	  	when(applinks.iterator()).thenReturn(applinksIter);
	  	when(applinksIter.hasNext()).thenReturn(false);
	  	
	  	return jiraApplinks;
	}
}