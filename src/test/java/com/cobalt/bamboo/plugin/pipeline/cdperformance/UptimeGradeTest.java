package com.cobalt.bamboo.plugin.pipeline.cdperformance;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Test;

import com.cobalt.bamboo.plugin.pipeline.cdresult.Build;


public class UptimeGradeTest {

	@Test
	public void testNullStartDate() {
		UptimeGrade g = new UptimeGrade(null, 0, false, new Date());
		assertEquals("Uptime percentage is not as expected.", -1, g.getUptimePercentage(), 0.0001);
		assertEquals("Grade is not as expected.", null, g.getGrade());
	}
	
	@Test
	public void testNullCurrentDate() {
		UptimeGrade g = new UptimeGrade(new Date(), 0, false, null);
		assertEquals("Uptime percentage is not as expected.", -1, g.getUptimePercentage(), 0.0001);
		assertEquals("Grade is not as expected.", null, g.getGrade());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testCurrentDateBeforeStartDate() {
		UptimeGrade g = new UptimeGrade(new Date(), 0, false, new Date((new Date()).getTime() - 1000000));
	}
	
	@Test
	public void testStartDateEqualsCurrentBuildDateSuccess() {
		Date current = new Date();
		Date startDate = new Date(current.getTime() - 100000);
		UptimeGrade g = new UptimeGrade(startDate, 0, true, startDate);
		assertEquals("Uptime percentage is not as expected.", 1, g.getUptimePercentage(), 0.0001);
		assertEquals("Grade is not as expected.", "A", g.getGrade());
	}
	
	@Test
	public void testStartDateEqualsCurrentBuildDateFail() {
		Date current = new Date();
		Date startDate = new Date(current.getTime() - 100000);
		UptimeGrade g = new UptimeGrade(startDate, 0, false, startDate);
		assertEquals("Uptime percentage is not as expected.", 0, g.getUptimePercentage(), 0.0001);
		assertEquals("Grade is not as expected.", "F", g.getGrade());
	}
	
	@Test
	public void testStartDateCurrentBuildDateDifferentSuccess() {
		Date current = new Date();
		Date currentBuildDate = new Date(current.getTime() - 100000);
		Date startDate = new Date(current.getTime() - 200000);
		UptimeGrade g = new UptimeGrade(startDate, 0, true, currentBuildDate);
		assertEquals("Uptime percentage is not as expected.", 0.5, g.getUptimePercentage(), 0.0001);
		assertEquals("Grade is not as expected.", "F", g.getGrade());
	}
	
	@Test
	public void testStartDateCurrentBuildDateDifferentFail() {
		Date current = new Date();
		Date currentBuildDate = new Date(current.getTime() - 100000);
		Date startDate = new Date(current.getTime() - 200000);
		UptimeGrade g = new UptimeGrade(startDate, 0, false, currentBuildDate);
		assertEquals("Uptime percentage is not as expected.", 0, g.getUptimePercentage(), 0.0001);
		assertEquals("Grade is not as expected.", "F", g.getGrade());
	}
	
	@Test
	public void testWithTotalUpTimeSuccess() {
		Date current = new Date();
		Date currentBuildDate = new Date(current.getTime() - 100000);
		Date startDate = new Date(current.getTime() - 200000);
		UptimeGrade g = new UptimeGrade(startDate, 50000, true, currentBuildDate);
		assertEquals("Uptime percentage is not as expected.", 0.75, g.getUptimePercentage(), 0.0001);
		assertEquals("Grade is not as expected.", "C", g.getGrade());
	}
	
	@Test
	public void testWithTotalUpTimeFail() {
		Date current = new Date();
		Date currentBuildDate = new Date(current.getTime() - 100000);
		Date startDate = new Date(current.getTime() - 200000);
		UptimeGrade g = new UptimeGrade(startDate, 50000, false, currentBuildDate);
		assertEquals("Uptime percentage is not as expected.", 0.25, g.getUptimePercentage(), 0.0001);
		assertEquals("Grade is not as expected.", "F", g.getGrade());
	}
	
	@Test
	public void testUpdateNoStartGradeWithIncompletedBuild() {
		UptimeGrade g = new UptimeGrade(null, 0, false, null);
		Build build = mock(Build.class);
		when(build.getBuildCompletedDate()).thenReturn(null);
		g.update(build);
		assertEquals("Uptime percentage is not as expected.", -1, g.getUptimePercentage(), 0.0001);
		assertEquals("Grade is not as expected.", null, g.getGrade());
	}
	
	@Test
	public void testUpdateGradeWithIncompletedBuild() {
		Date current = new Date();
		Date currentBuildDate = new Date(current.getTime() - 100000);
		Date startDate = new Date(current.getTime() - 200000);
		UptimeGrade g = new UptimeGrade(startDate, 50000, false, currentBuildDate);
		Build build = mock(Build.class);
		when(build.getBuildCompletedDate()).thenReturn(null);
		g.update(build);
		assertEquals("Uptime percentage is not as expected.", 0.25, g.getUptimePercentage(), 0.0001);
		assertEquals("Grade is not as expected.", "F", g.getGrade());
	}
	
	@Test
	public void testUpdateCurrentSuccessBuildGradeWithSuccessBuild() {
		Date current = new Date();
		Date currentBuildDate = new Date(current.getTime() - 100000);
		Date startDate = new Date(current.getTime() - 200000);
		UptimeGrade g = new UptimeGrade(startDate, 50000, true, currentBuildDate);
		Build build = mock(Build.class);
		when(build.getBuildCompletedDate()).thenReturn(new Date(current.getTime() - 50000));
		when(build.isSuccessful()).thenReturn(true);
		g.update(build);
		assertEquals("Uptime percentage is not as expected.", 0.75, g.getUptimePercentage(), 0.0001);
		assertEquals("Grade is not as expected.", "C", g.getGrade());
	}
	
	@Test
	public void testUpdateCurrentSuccessBuildGradeWithFailBuild() {
		Date current = new Date();
		Date currentBuildDate = new Date(current.getTime() - 100000);
		Date startDate = new Date(current.getTime() - 200000);
		UptimeGrade g = new UptimeGrade(startDate, 50000, true, currentBuildDate);
		Build build = mock(Build.class);
		when(build.getBuildCompletedDate()).thenReturn(new Date(current.getTime() - 50000));
		when(build.isSuccessful()).thenReturn(false);
		g.update(build);
		assertEquals("Uptime percentage is not as expected.", 0.5, g.getUptimePercentage(), 0.0001);
		assertEquals("Grade is not as expected.", "F", g.getGrade());
	}
	
	@Test
	public void testUpdateCurrentFailBuildGradeWithSuccessBuild() {
		Date current = new Date();
		Date currentBuildDate = new Date(current.getTime() - 100000);
		Date startDate = new Date(current.getTime() - 200000);
		UptimeGrade g = new UptimeGrade(startDate, 50000, false, currentBuildDate);
		Build build = mock(Build.class);
		when(build.getBuildCompletedDate()).thenReturn(new Date(current.getTime() - 50000));
		when(build.isSuccessful()).thenReturn(true);
		g.update(build);
		assertEquals("Uptime percentage is not as expected.", 0.5, g.getUptimePercentage(), 0.0001);
		assertEquals("Grade is not as expected.", "F", g.getGrade());
	}
	
	@Test
	public void testUpdateCurrentFailBuildGradeWithFailBuild() {
		Date current = new Date();
		Date currentBuildDate = new Date(current.getTime() - 100000);
		Date startDate = new Date(current.getTime() - 200000);
		UptimeGrade g = new UptimeGrade(startDate, 50000, false, currentBuildDate);
		Build build = mock(Build.class);
		when(build.getBuildCompletedDate()).thenReturn(new Date(current.getTime() - 50000));
		when(build.isSuccessful()).thenReturn(false);
		g.update(build);
		assertEquals("Uptime percentage is not as expected.", 0.25, g.getUptimePercentage(), 0.0001);
		assertEquals("Grade is not as expected.", "F", g.getGrade());
	}
}
