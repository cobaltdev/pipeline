package com.cobalt.bamboo.plugin.pipeline.changelist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.bamboo.applinks.JiraApplinksService;
import com.atlassian.bamboo.author.Author;
import com.atlassian.bamboo.chains.ChainResultsSummary;
import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.commit.CommitFile;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.cobalt.bamboo.plugin.pipeline.cdresult.ContributorBuilder;
import com.google.common.collect.ImmutableList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ChangeListFactoryTest {

	ContributorBuilder cb;
	
	@Before
	public void setup() {
		setUpContributorBuilder();
	}
	
	@Test
	public void testBuildChangeListWithNullBuildList() {
		List<Change> result = ChangeListFactory.buildChangeList(null, cb);
		assertEquals("Change list should be empty when build list passed in is null.", 
					0, result.size());
	}
	
	@Test
	public void testBuildChangeListWithEmptyBuildList() {
		List<Change> result = ChangeListFactory.buildChangeList(new ArrayList<ResultsSummary>(), cb);
		assertEquals("Change list should be empty when build list passed in is empty.", 
				0, result.size());
	}
	
	@Test
	public void testBuildChangeListWithOneBuildNoChange() {
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		ResultsSummary build = mock(ResultsSummary.class);
		ImmutableList.Builder<Commit> builder = new ImmutableList.Builder<Commit>();
		when(build.getCommits()).thenReturn(builder.build());
		when(build.getBuildNumber()).thenReturn(0);
		buildList.add(build);
		
		List<Change> result = ChangeListFactory.buildChangeList(buildList, cb);
		assertEquals("Change list should be empty when there are no changes.", 
				0, result.size());
	}
	
	@Test
	public void testBuildChangeListWithOneBuildNotCompletion() {
		testBuildChangeListWithNBuilds(1, -1, -1, 1) ;
	}
	
	@Test
	public void testBuildChangeListWithOneBuildIsCompletion() {
		testBuildChangeListWithNBuilds(1, 1, -1, 1) ;
	}
	
	@Test
	public void testBuildChangeListWithFiveBuildsNoCompletion() {
		testBuildChangeListWithNBuilds(5, -1, -1, (1 + 2 + 3 + 4 + 5)) ;
	}
	
	@Test
	public void testBuildChangeListWithFiveBuildsOneCompletionAtFirst() {
		testBuildChangeListWithNBuilds(5, 1, -1, (1 + 2 + 3 + 4 + 5)) ;
	}
	
	@Test
	public void testBuildChangeListWithFiveBuildsOneCompletionAtMiddle() {
		testBuildChangeListWithNBuilds(5, 3, -1, (1 + 2)) ;
	}
	
	@Test
	public void testBuildChangeListWithFiveBuildsOneCompletionAtLast() {
		testBuildChangeListWithNBuilds(5, 5, -1, (1 + 2 + 3 + 4)) ;
	}
	
	@Test
	public void testBuildChangeListWithFiveBuildsTwoCompletionAtFirstAndMiddle() {
		testBuildChangeListWithNBuilds(5, 1, 3, (1 + 2)) ;
	}
	
	@Test
	public void testBuildChangeListWithFiveBuildsTwoCompletionAtMiddleAndLast() {
		testBuildChangeListWithNBuilds(5, 3, 5, (1 + 2)) ;
	}
	
	
	// ========== Private Helper Methods ==========
	
	private void testBuildChangeListWithNBuilds(int n, int pos1, int pos2, int expected) {
		List<ResultsSummary> buildList = createBuildList(n, pos1, pos2);
		List<Change> result = ChangeListFactory.buildChangeList(buildList, cb);
		
		assertEquals("Number of changes in change list doesn't match.", expected, result.size());
	}
	
	private void setUpContributorBuilder() {
		JiraApplinksService service = mock(JiraApplinksService.class);
    	
		Iterable<ApplicationLink> applinks = (Iterable<ApplicationLink>) mock(Iterable.class);
    	when(service.getJiraApplicationLinks()).thenReturn(applinks);
    	
		Iterator<ApplicationLink> applinksIter = (Iterator<ApplicationLink>) mock(Iterator.class);
    	when(applinks.iterator()).thenReturn(applinksIter);
    	when(applinksIter.hasNext()).thenReturn(false);
    	
    	cb = new ContributorBuilder(service);
	}
	
	private List<ResultsSummary> createBuildList(int n, int pos1, int pos2) {
		List<ResultsSummary> buildList = new ArrayList<ResultsSummary>();
		for (int i = 1; i <= n; i++) {
			ChainResultsSummary build = mock(ChainResultsSummary.class);
			
			ImmutableList<Commit> commitList = createCommitList(i);
			when(build.getCommits()).thenReturn(commitList);
			when(build.getBuildNumber()).thenReturn(n + 1 - i);
			
			if (i == pos1 || i == pos2) {
				// set to deployment condition
				when(build.isContinuable()).thenReturn(false);
				when(build.isSuccessful()).thenReturn(true);
			} else {
				when(build.isContinuable()).thenReturn(true);
				when(build.isSuccessful()).thenReturn(false);
			}
			
			buildList.add(build);
		}
		return buildList;
	}
	
	private ImmutableList<Commit> createCommitList(int n) {
		ImmutableList.Builder<Commit> builder = new ImmutableList.Builder<Commit>();
		
		for (int i = 0; i < n; i++) {
			Commit commit = mock(Commit.class);
			Author author = mock(Author.class);
			when(commit.getAuthor()).thenReturn(author);
			when(author.getLinkedUserName()).thenReturn("author");
			when(commit.getComment()).thenReturn("comment");
			when(commit.getFiles()).thenReturn(new ArrayList<CommitFile>());
			
			builder.add(commit);
		}
		return builder.build();
	}
}
