package com.cobalt.bamboo.plugin.pipeline.cdresult;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.atlassian.bamboo.chains.ChainResultsSummary;
import com.atlassian.bamboo.resultsummary.ResultsSummary;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SetLastUpdateTimeTest {

	@Test
	public void testTimeWithBuildComplete() {
		CDResult cdr = new CDResult("Project", "Project - Plan", "project", "plan");
		Date date = new Date();
		Date oldDate = new Date(date.getTime() - 10000);
		ChainResultsSummary build = createBuild(oldDate, oldDate, 5, date, date);
		CDResultFactory.setLastUpdateTime(cdr, build);
		assertEquals("Last update time should be the build complete time", date, cdr.getLastUpdateTime());
	}

	@Test
	public void testTimeWithLastStageComplete() {
		CDResult cdr = new CDResult("Project", "Project - Plan", "project", "plan");
		Date date = new Date();
		Date oldDate = new Date(date.getTime() - 10000);
		ChainResultsSummary build = createBuild(oldDate, oldDate, 4, date, null);
		CDResultFactory.setLastUpdateTime(cdr, build);
		assertEquals("Last update time should be the last stage complete time", date, cdr.getLastUpdateTime());
	}
	
	@Test
	public void testTimeWithMiddleStageComplete() {
		CDResult cdr = new CDResult("Project", "Project - Plan", "project", "plan");
		Date date = new Date();
		Date oldDate = new Date(date.getTime() - 10000);
		ChainResultsSummary build = createBuild(oldDate, oldDate, 2, date, null);
		CDResultFactory.setLastUpdateTime(cdr, build);
		assertEquals("Last update time should be the middle stage complete time", date, cdr.getLastUpdateTime());
	}
	
	@Test
	public void testTimeWithFirstStageComplete() {
		CDResult cdr = new CDResult("Project", "Project - Plan", "project", "plan");
		Date date = new Date();
		Date oldDate = new Date(date.getTime() - 10000);
		ChainResultsSummary build = createBuild(oldDate, oldDate, 0, date, null);
		CDResultFactory.setLastUpdateTime(cdr, build);
		assertEquals("Last update time should be the first stage complete time", date, cdr.getLastUpdateTime());
	}
	
	@Test
	public void testTimeWithBuildStart() {
		CDResult cdr = new CDResult("Project", "Project - Plan", "project", "plan");
		Date date = new Date();
		Date oldDate = new Date(date.getTime() - 10000);
		ChainResultsSummary build = createBuild(oldDate, date, -1, date, null);
		CDResultFactory.setLastUpdateTime(cdr, build);
		assertEquals("Last update time should be the build start time", date, cdr.getLastUpdateTime());
	}
	
	@Test
	public void testTimeWithBuildInQueue() {
		CDResult cdr = new CDResult("Project", "Project - Plan", "project", "plan");
		Date date = new Date();
		Date oldDate = new Date(date.getTime() - 10000);
		ChainResultsSummary build = createBuild(date, null, -1, date, null);
		CDResultFactory.setLastUpdateTime(cdr, build);
		assertEquals("Last update time should be the build queue time", date, cdr.getLastUpdateTime());
	}
	
	@Test
	public void testTimeWithBuildNotInQueue() {
		CDResult cdr = new CDResult("Project", "Project - Plan", "project", "plan");
		Date date = new Date();
		Date oldDate = new Date(date.getTime() - 10000);
		ChainResultsSummary build = createBuild(null, null, -1, date, null);
		CDResultFactory.setLastUpdateTime(cdr, build);
		assertEquals("Last update time shouldn't be set", null, cdr.getLastUpdateTime());
	}
	
	
	// ========== Private Helper Methods ==========
	
	/*
	 * Mock Date returned as given.
	 */
	private ChainResultsSummary createBuild(Date queue, Date start, int completeJob, Date job, Date complete){
		ChainResultsSummary build = mock(ChainResultsSummary.class);
		when(build.getBuildCompletedDate()).thenReturn(complete);
		List<ResultsSummary> jobs = this.createJobResults(completeJob, job);
		when(build.getOrderedJobResultSummaries()).thenReturn(jobs);
		when(build.getBuildDate()).thenReturn(start);
		when(build.getQueueTime()).thenReturn(queue);
		return build;
	}
	
	/*
	 * Any ResultsSummary with index less than n will complete at date.getTime() - 1000
	 * ResultsSummary with nth index will complete at date.getTime()
	 * Any ResultsSummary with index greater than n won't complete (return null)
	 */
	private List<ResultsSummary> createJobResults(int n, Date date){
		List<ResultsSummary> results = new ArrayList<ResultsSummary>();
		Date oldDate = new Date(date.getTime() - 10000);
		for(int i = 0; i < 5; i++){
			ResultsSummary result = mock(ResultsSummary.class);
			if(i < n){
				when(result.getBuildCompletedDate()).thenReturn(oldDate);
			}else if(i > n){
				when(result.getBuildCompletedDate()).thenReturn(null);
			}else{
				when(result.getBuildCompletedDate()).thenReturn(date);
			}
			results.add(result);
		}
		return results;
	}
}
