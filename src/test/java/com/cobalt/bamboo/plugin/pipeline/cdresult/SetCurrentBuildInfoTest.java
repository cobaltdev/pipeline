package com.cobalt.bamboo.plugin.pipeline.cdresult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.atlassian.bamboo.chains.ChainResultsSummary;
import com.atlassian.bamboo.chains.ChainStageResult;
import com.atlassian.bamboo.plan.PlanExecutionManager;
import com.atlassian.bamboo.resultsummary.ResultsSummary;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class SetCurrentBuildInfoTest {
	
	@Test
	public void testWithNoBuilds() {
		CDResult cdr = new CDResult("Project", "Project - Plan", "project", "plan");
		CDResultFactory.setCurrentBuildInfo(cdr, createNBuildResults(0), mock(PlanExecutionManager.class));
		assertEquals("Date of cdresult's lastUpdateTime should be null (default) when there are no builds.", 
						null, cdr.getLastUpdateTime());
		assertEquals("cdresult's currentBuild's buildNumber should be -1 (default) when there are no builds.",
						-1, cdr.getCurrentBuild().getBuildNumber());
		assertEquals("cdresult's currentBuild's buildKey should be null (default) when there are no builds.",
				null, cdr.getCurrentBuild().getBuildKey());
	}
	
	@Test
	public void testWithOnlyOneBuild() {
		testForNBuilds(1);
	}
	
	@Test
	public void testWithSmallNumberOfBuilds() {
		testForNBuilds(5);
	}
	
	@Test
	public void testWithBiggerNumberOfBuilds() {
		testForNBuilds(30);
	}

	// ========== Private Methods ==========
	
	// Test cdresult's lastUpdateTime and currentBuild's build-key and build-number
	// against the first element in the build list (assumingly the most current build).
	private void testForNBuilds(int N) {
		List<ResultsSummary> buildList = createNBuildResults(N);
		CDResult cdr = new CDResult("Project", "Project - Plan", "project", "plan");
		CDResultFactory.setCurrentBuildInfo(cdr, buildList, mock(PlanExecutionManager.class));

		ChainResultsSummary expectedBuild = (ChainResultsSummary) buildList.get(0);
		
		assertEquals("Date of cdresult's lastUpdateTime doesn't match.", 
					expectedBuild.getBuildCompletedDate().getTime(), cdr.getLastUpdateTime().getTime());
		assertEquals("Build-key of cdresult's currentBuild doesn't match.", 
					expectedBuild.getBuildResultKey(), cdr.getCurrentBuild().getBuildKey());
		assertEquals("Build-number of cdresult's currentBuild doesn't match.", 
					expectedBuild.getBuildNumber(), cdr.getCurrentBuild().getBuildNumber());
	}
	
	private List<ResultsSummary> createNBuildResults(int N) {
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		for (int i = N; i >= 1; i--) {
			ChainResultsSummary build = mock(ChainResultsSummary.class);
			when(build.getBuildCompletedDate()).thenReturn(new Date(i));
			when(build.getBuildResultKey()).thenReturn("Build " + i);
			when(build.getBuildNumber()).thenReturn(i);
			when(build.getStageResults()).thenReturn(new ArrayList<ChainStageResult>());
			when(build.getOrderedJobResultSummaries()).thenReturn(new ArrayList<ResultsSummary>());
			
			buildList.add(build);
		}
		
		return buildList;
	}
}
