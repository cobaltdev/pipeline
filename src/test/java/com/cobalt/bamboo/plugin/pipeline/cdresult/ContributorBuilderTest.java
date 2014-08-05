package com.cobalt.bamboo.plugin.pipeline.cdresult;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Iterator;

import org.junit.Test;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.bamboo.applinks.JiraApplinksService;
import com.atlassian.bamboo.jira.rest.JiraRestService;

public class ContributorBuilderTest {
	JiraApplinksService jiraApplinks;
	JiraRestService jiraRest;
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorWithNullArguments() {
		ContributorBuilder builder = new ContributorBuilder(null);
	}
	
	@Test
	public void testCreateContributorWithNoApplinks() {
		setUpNoApplinksJiraApplinksService();
		testCreateContributor(null, null);
	}
	
	@Test
	public void testCreateContributorWithNormalApplinks() {
		setUpNormalJiraApplinksService();
		testCreateContributor("www.baseurl.com/secure/useravatar?ownerId=username", 
							"www.baseurl.com/secure/ViewProfile.jspa?name=username");
	}
	
	
	// ========== Private Helper Methods ==========
	
	// Tests createContributor with the given expected results
	private void testCreateContributor(String expectedPicUrl, String expectedProfUrl) {
		ContributorBuilder builder = new ContributorBuilder(jiraApplinks);
		Date commitDate = new Date();
		Contributor contributor = builder.createContributor("username", commitDate, "User");
		assertEquals("Username of the contributor returned doesn't match", 
					"username", contributor.getUsername());
		assertEquals("Commit date of the contributor returned doesn't match", 
					commitDate, contributor.getLastCommitTime());
		assertEquals("Full name of the contributor returned doesn't match.", 
					"User", contributor.getFullname());
		assertEquals("Picture URL of the contributor returned doesn't match.", 
					expectedPicUrl, contributor.getPictureUrl());
		assertEquals("Profile page URL of the contributor returned doesn't match.", 
					expectedProfUrl, contributor.getProfilePageUrl());
	}
	
	// Set up JiraApplinksService that represents the situation of no applinks
	private void setUpNoApplinksJiraApplinksService() {
		jiraApplinks = mock(JiraApplinksService.class);
    	
		Iterable<ApplicationLink> applinks = (Iterable<ApplicationLink>) mock(Iterable.class);
    	when(jiraApplinks.getJiraApplicationLinks()).thenReturn(applinks);
    	
		Iterator<ApplicationLink> applinksIter = (Iterator<ApplicationLink>) mock(Iterator.class);
    	when(applinks.iterator()).thenReturn(applinksIter);
    	when(applinksIter.hasNext()).thenReturn(false);
	}
	
	// Set up JiraApplinksService that represents the situation of at least one applinks
	private void setUpNormalJiraApplinksService() {
		jiraApplinks = mock(JiraApplinksService.class);
    	
		Iterable<ApplicationLink> applinks = (Iterable<ApplicationLink>) mock(Iterable.class);
    	when(jiraApplinks.getJiraApplicationLinks()).thenReturn(applinks);
    	
		Iterator<ApplicationLink> applinksIter = (Iterator<ApplicationLink>) mock(Iterator.class);
    	when(applinks.iterator()).thenReturn(applinksIter);
    	when(applinksIter.hasNext()).thenReturn(true);
    	
    	ApplicationLink appLink = mock(ApplicationLink.class);
    	when(applinksIter.next()).thenReturn(appLink);
    	try {
			when(appLink.getRpcUrl()).thenReturn(new URI("www.baseurl.com"));
		} catch (URISyntaxException e) { }
	}
}
