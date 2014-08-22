package com.cobalt.bamboo.plugin.pipeline.cdperformance;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import org.junit.Test;


public class CDPerformanceTest {

	@Test(expected=IllegalArgumentException.class)
	public void testNumBuildSmallerThanNumSuccess(){
		List<CompletionStats> completions = new ArrayList<CompletionStats>();
		CDPerformance p = new CDPerformance(0, 1, 0, null, null, completions);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testStartDateAfterCompletionDate(){
		List<CompletionStats> completions = new ArrayList<CompletionStats>();
		Date current = new Date();
		CDPerformance p = new CDPerformance(0, 0, 0, current, new Date(current.getTime() - 10000), completions);
	}
	
	@Test
	public void testNoBuild() {
		List<CompletionStats> completions = new ArrayList<CompletionStats>();
		CDPerformance p = new CDPerformance(0, 0, 0, null, null, completions);
		assertEquals("The success percentage is not expected", 0.0, p.getSuccessPercentage(), 0.0);
		assertEquals("The average changes is not expected", -1, p.getAverageChanges(), 0.0);
		assertEquals("The average frequency is not expected", -1, p.getAverageFrequency(), 0.0);
		assertEquals("The length of completion stats is not expected", 0, p.getCompletions().size());
	}

	@Test
	public void testNoSuccessInOneBuild() {
		List<CompletionStats> completions = new ArrayList<CompletionStats>();
		Date current = new Date();
		CDPerformance p = new CDPerformance(1, 0, 0, current, null, completions);
		assertEquals("The success percentage is not expected", 0.0, p.getSuccessPercentage(), 0.0);
		assertEquals("The average changes is not expected", -1, p.getAverageChanges(), 0.0);
		assertEquals("The average frequency is not expected", -1, p.getAverageFrequency(), 0.0);
		assertEquals("The length of completion stats is not expected", 0, p.getCompletions().size());
	}
	
	@Test
	public void testOneSuccessNoCompletionInOneBuild() {
		List<CompletionStats> completions = new ArrayList<CompletionStats>();
		Date current = new Date();
		CDPerformance p = new CDPerformance(1, 1, 1, current, null, completions);
		assertEquals("The success percentage is not expected", 1, p.getSuccessPercentage(), 0.0);
		assertEquals("The average changes is not expected", -1, p.getAverageChanges(), 0.0);
		assertEquals("The average frequency is not expected", -1, p.getAverageFrequency(), 0.0);
		assertEquals("The length of completion stats is not expected", 0, p.getCompletions().size());
	}
	
	@Test
	public void testOneSuccessOneCompletionInOneBuild() {
		List<CompletionStats> completions = new ArrayList<CompletionStats>();
		Date current = new Date();
		completions.add(new CompletionStats(1, current));
		CDPerformance p = new CDPerformance(1, 1, 1, current, current, completions);
		assertEquals("The success percentage is not expected", 1, p.getSuccessPercentage(), 0.0);
		assertEquals("The average changes is not expected", 1, p.getAverageChanges(), 0.0);
		assertEquals("The average frequency is not expected", 0, p.getAverageFrequency(), 0.0);
		assertEquals("The length of completion stats is not expected", 1, p.getCompletions().size());
	}
	
	@Test
	public void testNoSuccessInBuilds(){
		List<CompletionStats> completions = new ArrayList<CompletionStats>();
		Date current = new Date();
		CDPerformance p = new CDPerformance(10, 0, 0, current, null, completions);
		assertEquals("The success percentage is not expected", 0.0, p.getSuccessPercentage(), 0.0);
		assertEquals("The average changes is not expected", -1, p.getAverageChanges(), 0.0);
		assertEquals("The average frequency is not expected", -1, p.getAverageFrequency(), 0.0);
		assertEquals("The length of completion stats is not expected", 0, p.getCompletions().size());
	}
	
	@Test
	public void testSomeSuccessNoCompletionInBuilds(){
		List<CompletionStats> completions = new ArrayList<CompletionStats>();
		Date current = new Date();
		CDPerformance p = new CDPerformance(10, 5, 0, current, null, completions);
		assertEquals("The success percentage is not expected", 0.5, p.getSuccessPercentage(), 0.0);
		assertEquals("The average changes is not expected", -1, p.getAverageChanges(), 0.0);
		assertEquals("The average frequency is not expected", -1, p.getAverageFrequency(), 0.0);
		assertEquals("The length of completion stats is not expected", 0, p.getCompletions().size());
	}
	
	@Test
	public void testAllSuccessNoCompletionInBuilds(){
		List<CompletionStats> completions = new ArrayList<CompletionStats>();
		Date current = new Date();
		CDPerformance p = new CDPerformance(10, 10, 0, current, null, completions);
		assertEquals("The success percentage is not expected", 1, p.getSuccessPercentage(), 0.0);
		assertEquals("The average changes is not expected", -1, p.getAverageChanges(), 0.0);
		assertEquals("The average frequency is not expected", -1, p.getAverageFrequency(), 0.0);
		assertEquals("The length of completion stats is not expected", 0, p.getCompletions().size());
	}
	
	@Test
	public void testSomeSuccessOneCompletionInBuilds(){
		List<CompletionStats> completions = new ArrayList<CompletionStats>();
		Date current = new Date();
		completions.add(new CompletionStats(1, current));
		CDPerformance p = new CDPerformance(10, 1, 8, new Date(current.getTime() - 10000), current, completions);
		assertEquals("The success percentage is not expected", 0.1, p.getSuccessPercentage(), 0.0);
		assertEquals("The average changes is not expected", 8, p.getAverageChanges(), 0.0);
		assertEquals("The average frequency is not expected", 10000.0 / (1000 * 60 * 60 * 24), p.getAverageFrequency(), 0.0); 
		assertEquals("The length of completion stats is not expected", 1, p.getCompletions().size());
	}
	
	@Test
	public void testSomeSuccessSomeCompletionInBuilds(){
		List<CompletionStats> completions = new ArrayList<CompletionStats>();
		Date current = new Date();
		completions.add(new CompletionStats(1, current));
		completions.add(new CompletionStats(1, new Date(current.getTime() - 10000)));
		completions.add(new CompletionStats(1, new Date(current.getTime() - 20000)));
		CDPerformance p = new CDPerformance(10, 5, 30, new Date(current.getTime() - 30000), current, completions);
		assertEquals("The success percentage is not expected", 0.5, p.getSuccessPercentage(), 0.0);
		assertEquals("The average changes is not expected", 10, p.getAverageChanges(), 0.0);
		assertEquals("The average frequency is not expected", 10000.0 / (1000 * 60 * 60 * 24), p.getAverageFrequency(), 0.0);
		assertEquals("The length of completion stats is not expected", 3, p.getCompletions().size());
	}
	
	@Test
	public void testSomeSuccessFirstBuildCompletedInBuilds(){
		List<CompletionStats> completions = new ArrayList<CompletionStats>();
		Date current = new Date();
		completions.add(new CompletionStats(1, current));
		completions.add(new CompletionStats(1, new Date(current.getTime() - 10000)));
		completions.add(new CompletionStats(1, new Date(current.getTime() - 30000)));
		CDPerformance p = new CDPerformance(10, 5, 30, new Date(current.getTime() - 30000), current, completions);
		assertEquals("The success percentage is not expected", 0.5, p.getSuccessPercentage(), 0.0);
		assertEquals("The average changes is not expected", 10, p.getAverageChanges(), 0.0); 
		assertEquals("The average frequency is not expected", 10000.0 / (1000 * 60 * 60 * 24), p.getAverageFrequency(), 0.0); 
		assertEquals("The length of completion stats is not expected", 3, p.getCompletions().size());
	}
	
	@Test
	public void testAllSuccessAllCompletionInBuilds(){
		List<CompletionStats> completions = new ArrayList<CompletionStats>();
		Date current = new Date();
		for(int i = 0; i < 10; i++){
			completions.add(new CompletionStats(1, new Date(current.getTime() - 10000 * i)));
		}
		CDPerformance p = new CDPerformance(10, 10, 100, new Date(current.getTime() - 100000), current, completions);
		assertEquals("The success percentage is not expected", 1, p.getSuccessPercentage(), 0.0);
		assertEquals("The average changes is not expected", 10, p.getAverageChanges(), 0.0);
		assertEquals("The average frequency is not expected", 10000.0 / (1000 * 60 * 60 * 24), p.getAverageFrequency(), 0.0001);
		assertEquals("The length of completion stats is not expected", 10, p.getCompletions().size());
	}
}
