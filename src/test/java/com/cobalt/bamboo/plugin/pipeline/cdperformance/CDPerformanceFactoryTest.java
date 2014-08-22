package com.cobalt.bamboo.plugin.pipeline.cdperformance;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.*;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.bamboo.applinks.JiraApplinksService;
import com.atlassian.bamboo.author.Author;
import com.atlassian.bamboo.chains.ChainResultsSummary;
import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.cobalt.bamboo.plugin.pipeline.cdresult.ContributorBuilder;
import com.google.common.collect.ImmutableList;


public class CDPerformanceFactoryTest {
	private Date day1;
	private Date day2;
	private Date day3;
	private Date day4;
	private Date day5;
	private Date current;
	ContributorBuilder cb;
	
	@Before
	public void setup(){
		// mockup JiraApplinksService
		JiraApplinksService jiraApplinksService = mock(JiraApplinksService.class);
		Iterator<ApplicationLink> itr = (Iterator<ApplicationLink>) mock(Iterator.class);
		when(itr.hasNext()).thenReturn(false);
		Iterable<ApplicationLink> iterable = (Iterable<ApplicationLink>) mock(Iterable.class);
		when(iterable.iterator()).thenReturn(itr);	
		when(jiraApplinksService.getJiraApplicationLinks()).thenReturn(iterable);
				
		cb = new ContributorBuilder(jiraApplinksService);
		day1 = new Date();
		day2 = new Date();
		day3 = new Date();
		day4 = new Date();
		day5 = new Date();
		day1.setDate(day1.getDate() - 5);
		day2.setDate(day2.getDate() - 4);
		day3.setDate(day3.getDate() - 3);
		day4.setDate(day4.getDate() - 2);
		day5.setDate(day5.getDate() - 1);
		current = new Date();
	}
	
	@Test
	public void testNoBuild(){
		CDPerformance cdp = CDPerformanceFactory.createCDPerformance(new ArrayList<ResultsSummary>(), cb);
		assertEquals("No build, return null", null, cdp);
	}
	
	@Test
	public void testNoSuccessOneBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, false, "1", 1, day1));
		checkExpected(buildList, 0, -1, -1, new ArrayList<Integer>(), new ArrayList<Integer>());
	}
	
	@Test
	public void testOneSuccessNoCompletionOneBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, true, "1", 1, day1));
		checkExpected(buildList, 1, -1, -1, new ArrayList<Integer>(), new ArrayList<Integer>());
	}
	
	@Test
	public void testOneCompletionOneBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, true, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(1);
		buildNums.add(1);
		checkExpected(buildList, 1, 1, 0, changes, buildNums);
	}
	
	@Test
	public void testNoSuccessTwoBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, false, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, false, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		checkExpected(buildList, 0, -1, -1, changes, buildNums);
	}
	
	@Test
	public void testFailSuccessTwoBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, true, "2", 2, day2));
		buildList.add(getChainResultsSummary(true, false, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		checkExpected(buildList, 0.5, -1, -1, changes, buildNums);
	}
	
	@Test
	public void testSuccessFailTwoBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, false, "2", 2, day2));
		buildList.add(getChainResultsSummary(true, true, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		checkExpected(buildList, 0.5, -1, -1, changes, buildNums);
	}
	
	@Test
	public void testTwoSuccessTwoBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, true, "2", 2, day2));
		buildList.add(getChainResultsSummary(true, true, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		checkExpected(buildList, 1, -1, -1, changes, buildNums);
	}
	
	@Test
	public void testFailCompletionTwoBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, true, "2", 2, day2));
		buildList.add(getChainResultsSummary(true, false, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(2);
		buildNums.add(2);
		checkExpected(buildList, 0.5, 2, 1, changes, buildNums);
	}
	
	@Test
	public void testCompletionFailTwoBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, false, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, true, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(1);
		buildNums.add(1);
		checkExpected(buildList, 0.5, 1, 0, changes, buildNums);
	}
	
	@Test
	public void testSuccessCompletionTwoBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, true, "2", 2, day2));
		buildList.add(getChainResultsSummary(true, true, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(2);
		buildNums.add(2);
		checkExpected(buildList, 1, 2, 1, changes, buildNums);
	}
	
	@Test
	public void testCompletionSuccessTwoBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, true, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, true, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(1);
		buildNums.add(1);
		checkExpected(buildList, 1, 1, 0, changes, buildNums);
	}
	
	@Test
	public void testTwoCompletionTwoBuild(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, true, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, true, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(1);
		buildNums.add(2);
		changes.add(1);
		buildNums.add(1);
		checkExpected(buildList, 1, 1, 0.5, changes, buildNums);
	}
	
	@Test
	public void testFFFFF(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, false, "5", 5, day5));
		buildList.add(getChainResultsSummary(true, false, "4", 4, day4));
		buildList.add(getChainResultsSummary(false, false, "3", 3, day3));
		buildList.add(getChainResultsSummary(true, false, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, false, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		checkExpected(buildList, 0, -1, -1, changes, buildNums);
	}
	
	@Test
	public void testFFSSF(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, false, "5", 5, day5));
		buildList.add(getChainResultsSummary(true, true, "4", 4, day4));
		buildList.add(getChainResultsSummary(true, true, "3", 3, day3));
		buildList.add(getChainResultsSummary(true, false, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, false, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		checkExpected(buildList, 0.4, -1, -1, changes, buildNums);
	}
	
	@Test
	public void testSFFFS(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, true, "5", 5, day5));
		buildList.add(getChainResultsSummary(true, false, "4", 4, day4));
		buildList.add(getChainResultsSummary(false, false, "3", 3, day3));
		buildList.add(getChainResultsSummary(true, false, "2", 2, day2));
		buildList.add(getChainResultsSummary(true, true, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		checkExpected(buildList, 0.4, -1, -1, changes, buildNums);
	}
	
	@Test
	public void testSSSSS(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, true, "5", 5, day5));
		buildList.add(getChainResultsSummary(true, true, "4", 4, day4));
		buildList.add(getChainResultsSummary(true, true, "3", 3, day3));
		buildList.add(getChainResultsSummary(true, true, "2", 2, day2));
		buildList.add(getChainResultsSummary(true, true, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		checkExpected(buildList, 1, -1, -1, changes, buildNums);
	}
	
	@Test
	public void testCFFFF(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, false, "5", 5, day5));
		buildList.add(getChainResultsSummary(true, false, "4", 4, day4));
		buildList.add(getChainResultsSummary(true, false, "3", 3, day3));
		buildList.add(getChainResultsSummary(true, false, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, true, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(1);
		buildNums.add(1);
		checkExpected(buildList, 0.2, 1, 0, changes, buildNums);
	}
	
	@Test
	public void testFFCFF(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, false, "5", 5, day5));
		buildList.add(getChainResultsSummary(true, false, "4", 4, day4));
		buildList.add(getChainResultsSummary(false, true, "3", 3, day3));
		buildList.add(getChainResultsSummary(true, false, "2", 2, day2));
		buildList.add(getChainResultsSummary(true, false, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(3);
		buildNums.add(3);
		checkExpected(buildList, 0.2, 3, 2, changes, buildNums);
	}
	
	@Test
	public void testFFFFC(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, true, "5", 5, day5));
		buildList.add(getChainResultsSummary(true, false, "4", 4, day4));
		buildList.add(getChainResultsSummary(true, false, "3", 3, day3));
		buildList.add(getChainResultsSummary(true, false, "2", 2, day2));
		buildList.add(getChainResultsSummary(true, false, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(5);
		buildNums.add(5);
		checkExpected(buildList, 0.2, 5, 4, changes, buildNums);
	}
	
	@Test
	public void testCFSFF(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, false, "5", 5, day5));
		buildList.add(getChainResultsSummary(true, false, "4", 4, day4));
		buildList.add(getChainResultsSummary(true, true, "3", 3, day3));
		buildList.add(getChainResultsSummary(true, false, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, true, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(1);
		buildNums.add(1);
		checkExpected(buildList, 0.4, 1, 0, changes, buildNums);
	}
	
	@Test
	public void testFFSFC(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, true, "5", 5, day5));
		buildList.add(getChainResultsSummary(true, false, "4", 4, day4));
		buildList.add(getChainResultsSummary(true, true, "3", 3, day3));
		buildList.add(getChainResultsSummary(true, false, "2", 2, day2));
		buildList.add(getChainResultsSummary(true, false, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(5);
		buildNums.add(5);
		checkExpected(buildList, 0.4, 5, 4, changes, buildNums);
	}
	
	@Test
	public void testCCFFF(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, false, "5", 5, day5));
		buildList.add(getChainResultsSummary(true, false, "4", 4, day4));
		buildList.add(getChainResultsSummary(true, false, "3", 3, day3));
		buildList.add(getChainResultsSummary(false, true, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, true, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(1);
		buildNums.add(2);
		changes.add(1);
		buildNums.add(1);
		checkExpected(buildList, 0.4, 1, 0.5, changes, buildNums);
	}
	
	@Test
	public void testCFFCF(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, false, "5", 5, day5));
		buildList.add(getChainResultsSummary(false, true, "4", 4, day4));
		buildList.add(getChainResultsSummary(false, false, "3", 3, day3));
		buildList.add(getChainResultsSummary(true, false, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, true, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(3);
		buildNums.add(4);
		changes.add(1);
		buildNums.add(1);
		checkExpected(buildList, 0.4, 2, 1.5, changes, buildNums);
	}
	
	@Test
	public void testCFFFC(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, true, "5", 5, day5));
		buildList.add(getChainResultsSummary(true, false, "4", 4, day4));
		buildList.add(getChainResultsSummary(true, false, "3", 3, day3));
		buildList.add(getChainResultsSummary(true, false, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, true, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(4);
		buildNums.add(5);
		changes.add(1);
		buildNums.add(1);
		checkExpected(buildList, 0.4, 2.5, 2, changes, buildNums);
	}
	
	@Test
	public void testFCFCF(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, false, "5", 5, day5));
		buildList.add(getChainResultsSummary(false, true, "4", 4, day4));
		buildList.add(getChainResultsSummary(true, false, "3", 3, day3));
		buildList.add(getChainResultsSummary(false, true, "2", 2, day2));
		buildList.add(getChainResultsSummary(true, false, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(2);
		buildNums.add(4);
		changes.add(2);
		buildNums.add(2);
		checkExpected(buildList, 0.4, 2, 1.5, changes, buildNums);
	}
	
	@Test
	public void testFCFFC(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, true, "5", 5, day5));
		buildList.add(getChainResultsSummary(true, false, "4", 4, day4));
		buildList.add(getChainResultsSummary(true, false, "3", 3, day3));
		buildList.add(getChainResultsSummary(false, true, "2", 2, day2));
		buildList.add(getChainResultsSummary(true, false, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(3);
		buildNums.add(5);
		changes.add(2);
		buildNums.add(2);
		checkExpected(buildList, 0.4, 2.5, 2, changes, buildNums);
	}
	
	@Test
	public void testFFFCC(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, true, "5", 5, day5));
		buildList.add(getChainResultsSummary(false, true, "4", 4, day4));
		buildList.add(getChainResultsSummary(true, false, "3", 3, day3));
		buildList.add(getChainResultsSummary(true, false, "2", 2, day2));
		buildList.add(getChainResultsSummary(true, false, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(1);
		buildNums.add(5);
		changes.add(4);
		buildNums.add(4);
		checkExpected(buildList, 0.4, 2.5, 2, changes, buildNums);
	}
	
	@Test
	public void testCCFFC(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, true, "5", 5, day5));
		buildList.add(getChainResultsSummary(true, false, "4", 4, day4));
		buildList.add(getChainResultsSummary(true, false, "3", 3, day3));
		buildList.add(getChainResultsSummary(false, true, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, true, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(3);
		buildNums.add(5);
		changes.add(1);
		buildNums.add(2);
		changes.add(1);
		buildNums.add(1);
		checkExpected(buildList, 0.6, 5.0 / 3, 4.0 / 3, changes, buildNums); 
	}
	
	@Test
	public void testFCCCF(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(true, false, "5", 5, day5));
		buildList.add(getChainResultsSummary(false, true, "4", 4, day4));
		buildList.add(getChainResultsSummary(false, true, "3", 3, day3));
		buildList.add(getChainResultsSummary(false, true, "2", 2, day2));
		buildList.add(getChainResultsSummary(true, false, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(1);
		buildNums.add(4);
		changes.add(1);
		buildNums.add(3);
		changes.add(2);
		buildNums.add(2);
		checkExpected(buildList, 0.6, 4.0/3, 1, changes, buildNums); 
	}
	
	@Test
	public void testCCCCC(){
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		buildList.add(getChainResultsSummary(false, true, "5", 5, day5));
		buildList.add(getChainResultsSummary(false, true, "4", 4, day4));
		buildList.add(getChainResultsSummary(false, true, "3", 3, day3));
		buildList.add(getChainResultsSummary(false, true, "2", 2, day2));
		buildList.add(getChainResultsSummary(false, true, "1", 1, day1));
		List<Integer> changes = new ArrayList<Integer>();
		List<Integer> buildNums = new ArrayList<Integer>();
		changes.add(1);
		buildNums.add(5);
		changes.add(1);
		buildNums.add(4);
		changes.add(1);
		buildNums.add(3);
		changes.add(1);
		buildNums.add(2);
		changes.add(1);
		buildNums.add(1);
		checkExpected(buildList, 1, 1, 0.8, changes, buildNums);
	}
	
	private void checkExpected(List<ResultsSummary> list, double percentage, double changes, double frequency, 
								List<Integer> completionsChanges, List<Integer> buildNumbers){
		CDPerformance cdp = CDPerformanceFactory.createCDPerformance(list, cb);
		assertEquals("Success percentage isn't as expected", percentage, cdp.getSuccessPercentage(), 0.0001);
		assertEquals("Average changes isn't as expected", changes, cdp.getAverageChanges(), 0.0001);
		assertEquals("Average frequency isn't as expected", frequency, cdp.getAverageFrequency(), 0.0001);
		List<CompletionStats> completions = cdp.getCompletions();
		assertEquals("Number of completions isn't as expected", buildNumbers.size(), completions.size());
		for(int i = 0; i < completions.size(); i++){
			assertEquals("Build number of completion " + i + " isn't as expected", (int) buildNumbers.get(i), completions.get(i).getBuildNumber());
			assertEquals("Changes of completion " + i + " isn't as expected", (int) completionsChanges.get(i), completions.get(i).getNumChanges());
		}
	}
	
	private ChainResultsSummary getChainResultsSummary(boolean cont, boolean succ, String name, int buildNumber, Date date){
		ChainResultsSummary result = mock(ChainResultsSummary.class);
		when(result.isContinuable()).thenReturn(cont);
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
