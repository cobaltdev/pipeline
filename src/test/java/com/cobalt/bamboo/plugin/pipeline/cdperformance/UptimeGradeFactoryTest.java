package com.cobalt.bamboo.plugin.pipeline.cdperformance;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.bamboo.author.Author;
import com.atlassian.bamboo.chains.ChainResultsSummary;
import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.google.common.collect.ImmutableList;


public class UptimeGradeFactoryTest {
	private Date day1;
	private Date day2;
	private Date day3;
	
	
	@Before
	public void setup() {
		day1 = new Date();
		day2 = new Date();
		day3 = new Date();
		day1.setDate(day1.getDate() - 5);
		day2.setDate(day2.getDate() - 4);
		day3.setDate(day3.getDate() - 3);
	}
	
	@Test
	public void testNoBuild() throws Exception {
		UptimeGrade g = UptimeGradeFactory.createUptimeGrade(new ArrayList<ResultsSummary>());
	}

	@Test
	public void testNoSuccessOneBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, "1", 1, day1));
		checkExpected(buildList, 0, "F");
	}
	
	@Test
	public void testOneSuccessOneBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, "1", 1, day1));
		checkExpected(buildList, 1, "A");
	}
	
	@Test
	public void testNoCompletionOneBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, "1", 1, null));
		checkExpected(buildList, -1, null);
	}
	
	@Test
	public void testSuccessSuccessTwoBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, "2", 2, day2));
		buildList.add(getChainResultsSummary(true, "1", 1, day1));
		checkExpected(buildList, 1, "A");
	}
	
	@Test
	public void testSuccessFailTwoBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, "2", 2, day2));
		buildList.add(getChainResultsSummary(true, "1", 1, day1));
		checkExpected(buildList, 0.2, "F");
	}
	
	@Test
	public void testFailSuccessTwoBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, "1", 1, day1));
		checkExpected(buildList, 0.8, "B-");
	}
	
	@Test
	public void testFailFailTwoBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, "1", 1, day1));
		checkExpected(buildList, 0, "F");
	}
	
	@Test
	public void testSuccessImcompleteTwoBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, "2", 2, null));
		buildList.add(getChainResultsSummary(true, "1", 1, day1));
		checkExpected(buildList, 1, "A");
	}
	
	@Test
	public void testIncompleteSuccessTwoBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, "1", 1, null));
		checkExpected(buildList, 1, "A");
	}
	
	@Test
	public void testFailIncompleteTwoBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, "2", 2, null));
		buildList.add(getChainResultsSummary(false, "1", 1, day1));
		checkExpected(buildList, 0, "F");
	}
	
	@Test
	public void testImcompleteFailTwoBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, "1", 1, null));
		checkExpected(buildList, 0, "F");
	}
	
	@Test
	public void testNoCompletionTwoBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, "2", 2, null));
		buildList.add(getChainResultsSummary(false, "1", 1, null));
		checkExpected(buildList, -1, null);
	}
	
	@Test
	public void testIncompleteSuccessFailThreeBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, "3", 3, day3));
		buildList.add(getChainResultsSummary(true, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, "1", 1, null));
		checkExpected(buildList, 0.25, "F");
	}
	
	@Test
	public void testSuccessIncompleteFailThreeBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, "3", 3, day3));
		buildList.add(getChainResultsSummary(false, "2", 2, null));
		buildList.add(getChainResultsSummary(true, "1", 1, day1));
		checkExpected(buildList, 0.4, "F");
	}
	
	@Test
	public void testSuccessFailIncompleteThreeBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, "3", 3, null));
		buildList.add(getChainResultsSummary(false, "2", 2, day2));
		buildList.add(getChainResultsSummary(true, "1", 1, day1));
		checkExpected(buildList, 0.2, "F");
	}
	
	@Test
	public void testIncompleteSuccessIncompleteThreeBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, "3", 3, null));
		buildList.add(getChainResultsSummary(true, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, "1", 1, null));
		checkExpected(buildList, 1, "A");
	}
	
	private void checkExpected(List<ResultsSummary> list, double upPercentage, String grade){
		UptimeGrade g = UptimeGradeFactory.createUptimeGrade(list);
		assertEquals("Uptime percentage is not as expected", upPercentage, g.getUptimePercentage(), 0.001);
		assertEquals("Grade is not as expected", grade, g.getGrade());
		
	}

	private ChainResultsSummary getChainResultsSummary(boolean succ, String name, int buildNumber, Date date){
		ChainResultsSummary result = mock(ChainResultsSummary.class);
		when(result.isSuccessful()).thenReturn(succ);
		Commit c = mock(Commit.class);
		when(c.getComment()).thenReturn("comment");
		Author a = mock(Author.class);
		when(a.getLinkedUserName()).thenReturn(name);
		when(a.getName()).thenReturn(name);
		when(c.getAuthor()).thenReturn(a);
		when(c.getDate()).thenReturn(new Date());
		ImmutableList<Commit> commits = ImmutableList.of(c);
		when(result.getCommits()).thenReturn(commits);
		when(result.getBuildCompletedDate()).thenReturn(date);
		when(result.getBuildNumber()).thenReturn(buildNumber); 
		return result;
	}
}
