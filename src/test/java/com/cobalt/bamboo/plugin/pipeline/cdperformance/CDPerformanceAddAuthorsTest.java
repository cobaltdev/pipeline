package com.cobalt.bamboo.plugin.pipeline.cdperformance;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.bamboo.applinks.JiraApplinksService;
import com.atlassian.bamboo.author.Author;
import com.atlassian.bamboo.commit.Commit;
import com.cobalt.bamboo.plugin.pipeline.cdresult.ContributorBuilder;

public class CDPerformanceAddAuthorsTest {
	private static final int COMMIT_LIST_SIZE = 10; // >= 3
	CompletionStats stat; 
	ContributorBuilder cb;

	@Before
	public void setup() {
		// mockup JiraApplinksService
		JiraApplinksService jiraApplinksService = mock(JiraApplinksService.class);
		Iterator<ApplicationLink> itr = (Iterator<ApplicationLink>) mock(Iterator.class);
		when(itr.hasNext()).thenReturn(false);
		Iterable<ApplicationLink> iterable = (Iterable<ApplicationLink>) mock(Iterable.class);
		when(iterable.iterator()).thenReturn(itr);	
		when(jiraApplinksService.getJiraApplicationLinks()).thenReturn(iterable);
		
		cb = new ContributorBuilder(jiraApplinksService);	 
		stat = new CompletionStats(1, new Date());		
	}
	
	@Test
	public void testNoCommit() {
		List<Commit> commits = createCommitListWithoutAuthors(0);
    	CDPerformanceFactory.addAllAuthorsInCommits(stat, commits, cb);
    	assertEquals("0 commit should have 0 contributor", 0, stat.getContributors().size());   
	}
		
	@Test
	public void testOneCommit() {
		List<Commit> commits = createCommitListWithoutAuthors(1);
		List<Author> authors = createAuthorListWithNames(1);
		for (int i = 0; i < 1; i++) {
	    	when(commits.get(i).getAuthor()).thenReturn(authors.get(i));
	    	when(commits.get(i).getComment()).thenReturn("comment");
		}
		CDPerformanceFactory.addAllAuthorsInCommits(stat, commits, cb);
    	assertEquals("1 commit and 1 contributor", 1, stat.getContributors().size());  
	}
		
	@Test
	public void testManyCommitsUniqueAuthors() {
		// create lists of commits and their corresponding authors
		List<Commit> commits = createCommitListWithoutAuthors(COMMIT_LIST_SIZE);
		List<Author> authors = createAuthorListWithNames(10);
		// assign unique authors to each commit
		for (int i = 0; i < COMMIT_LIST_SIZE; i++) {
	    	when(commits.get(i).getAuthor()).thenReturn(authors.get(i));
	    	when(commits.get(i).getComment()).thenReturn("comment");
		}

		CDPerformanceFactory.addAllAuthorsInCommits(stat, commits, cb);
    	// check that all unique authors are counted
    	assertEquals("10 commits, each w/ unique contributor", COMMIT_LIST_SIZE, stat.getContributors().size());    	
	}
	
	@Test
	public void testMultipleCommitsDuplicateAuthors() {
		List<Commit> commits = createCommitListWithoutAuthors(COMMIT_LIST_SIZE);
		List<Author> authors = createAuthorListWithNames(3);			
		for (int i = 0; i < COMMIT_LIST_SIZE; i++) {
	    	when(commits.get(i).getAuthor()).thenReturn(authors.get(i % 3));
	    	when(commits.get(i).getDate()).thenReturn(new Date());
	    	when(commits.get(i).getComment()).thenReturn("comment");
		}
		
		CDPerformanceFactory.addAllAuthorsInCommits(stat, commits, cb);
    	// check the duplicate authors are only counted once
    	assertEquals("10 commits w/ duplicate contributors", 3, stat.getContributors().size()); 	
	}
	
	
	// ========== Private Helper Methods ==========
	
	/*
	 * Create a list of commits w/o assigning them the authors
	 */
	private List<Commit> createCommitListWithoutAuthors(int numCommits) {
		List<Commit> commits = new ArrayList<Commit>();
		for (int i = 0; i < numCommits; i++) {
			commits.add(mock(Commit.class));
		}
		return commits;
	}

	/*
	 * Create a list of authors w/o assigning them the names
	 */
	private List<Author> createAuthorListWithNames(int numAuthors) {
		List<Author> authors = new ArrayList<Author>();
		for (int i = 0; i < numAuthors; i++) {
			authors.add(mock(Author.class));
		}
		for (int i = 0; i < numAuthors; i++) {
	    	when(authors.get(i).getLinkedUserName()).thenReturn("author" + i);
	    	when(authors.get(i).getName()).thenReturn("author" + i);
		}
		return authors;
	}
}
