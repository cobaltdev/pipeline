package com.cobalt.bamboo.plugin.pipeline.cdperformance;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.cobalt.bamboo.plugin.pipeline.cdresult.Contributor;


public class CompletionStatsTest {
	Contributor test1, test2, test3;
	Date current;
	
	@Before
	public void setUpContributors(){
		current = new Date();
		test1 = new Contributor("test 1", current, null, null, null);
		test2 = new Contributor("test 2", new Date(current.getTime() - 1000), null, null, null);
		test3 = new Contributor("test 3", new Date(current.getTime() - 10000), null, null, null);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void addIdenticalContributorsTest(){
		CompletionStats stat = new CompletionStats(0, current);
		stat.addContributor(test1);
		Contributor i1 = new Contributor("test 1", current, null, null, null);
		stat.addContributor(i1);
	}
	
	// Test update contributors
	@Test (expected = IllegalArgumentException.class)
	public void updateNotExistContributorTest(){
		CompletionStats stat = new CompletionStats(0, current);		
		stat.updateContributor("test 1", current);
	}
	
	@Test
	public void updateExistContributorWithNoTimeUpdateTest(){
		CompletionStats stat = new CompletionStats(0, current);		
		stat.addContributor(test1);
		stat.updateContributor("test 1", new Date(current.getTime() - 1000));
		assertEquals("Only one Contributor in the list", 1, stat.getContributorsSortedByLatestCommit().size());
		for(Contributor c : stat. getContributorsSortedByLatestCommit()){
			assertEquals("Increment the number of commits of the contributor", 2, c.getCommitCount());
			assertEquals("Last Commit Time shouldn't be changed", current, c.getLastCommitTime());
		}
	}
	
	@Test
	public void updateExistContributorWithTimeUpdateTest(){
		CompletionStats stat = new CompletionStats(0, current);		
		stat.addContributor(test2);
		stat.updateContributor("test 2", current);
		assertEquals("Only one Contributor in the list", 1, stat.getContributorsSortedByLatestCommit().size());
		for(Contributor c : stat. getContributorsSortedByLatestCommit()){
			assertEquals("Increment the number of commits of the contributor", 2, c.getCommitCount());
			assertEquals("Last Commit Time shouldn be updated", current, c.getLastCommitTime());
		}
	}
	
	@Test
	public void lastCommitTimeSortingTestOfContributors(){
		CompletionStats stat = new CompletionStats(0, current);		
		stat.addContributor(test1);
		stat.addContributor(test2);
		stat.addContributor(test3);
		List<Contributor> contributors = stat.getContributorsSortedByLatestCommit();
		assertEquals("There should not be dulplicate identical Contributors", 3, contributors.size());
		assertEquals("The list of Contributors should be sorted by last commit time", test1.getUsername(), contributors.get(0).getUsername());
		assertEquals("The list of Contributors should be sorted by last commit time", test2.getUsername(), contributors.get(1).getUsername());
		assertEquals("The list of Contributors should be sorted by last commit time", test3.getUsername(), contributors.get(2).getUsername());
	}
}
