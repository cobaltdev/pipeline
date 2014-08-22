package com.cobalt.bamboo.plugin.pipeline.cdresult;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.*;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.bamboo.applinks.JiraApplinksService;
import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.PlanExecutionManager;
import com.atlassian.bamboo.project.Project;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;

public class CDResultFactoryTest {
	ContributorBuilder cb;

	@Before
	public void setup() {
		JiraApplinksService jiraApplinksService = mock(JiraApplinksService.class);
		Iterator<ApplicationLink> itr = (Iterator<ApplicationLink>) mock(Iterator.class);
		when(itr.hasNext()).thenReturn(false);
		Iterable<ApplicationLink> iterable = (Iterable<ApplicationLink>) mock(Iterable.class);
		when(iterable.iterator()).thenReturn(itr);	
		when(jiraApplinksService.getJiraApplicationLinks()).thenReturn(iterable);
		
		cb = new ContributorBuilder(jiraApplinksService);	 
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCreateCDResultWithNullArguments() {
		CDResultFactory.createCDResult(null, cb, mock(PlanExecutionManager.class), mock(ResultsSummaryManager.class));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithInvalidPlanName() {
		Plan plan = mock(Plan.class);
		Project project = mock(Project.class);
		when(project.getName()).thenReturn("project");
		when(project.getKey()).thenReturn("projectKey");
		when(plan.getProject()).thenReturn(project);
		when(plan.getName()).thenReturn("projectplan");
		when(plan.getKey()).thenReturn("planKey");
		CDResultFactory.createCDResult(plan, cb, mock(PlanExecutionManager.class), mock(ResultsSummaryManager.class));
	}
	
	@Test
	public void testConstructorWithNormalArguments() {
		Plan plan = mock(Plan.class);
		Project project = mock(Project.class);
		when(project.getName()).thenReturn("Project");
		when(project.getKey()).thenReturn("project");
		when(plan.getProject()).thenReturn(project);
		when(plan.getName()).thenReturn("Project - Plan");
		when(plan.getKey()).thenReturn("plan");
		ResultsSummaryManager rsMng = mock(ResultsSummaryManager.class);
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		when(rsMng.getResultSummariesForPlan(plan, 0, 1)).thenReturn(buildList);
		
		CDResult cdresult = CDResultFactory.createCDResult(plan, cb, mock(PlanExecutionManager.class), rsMng);
		
		assertEquals("Project name doesn't match.", "Project", cdresult.getProjectName());
		assertEquals("Plan name doesn't match.", "Plan", cdresult.getPlanName());
	}
}
