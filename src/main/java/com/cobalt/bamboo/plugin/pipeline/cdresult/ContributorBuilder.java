package com.cobalt.bamboo.plugin.pipeline.cdresult;

import java.util.Date;
import java.util.Iterator;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.bamboo.applinks.JiraApplinksService;

public class ContributorBuilder {
	private static final String JIRA_USER_AVATAR_PATH = "/secure/useravatar?ownerId=";
	private static final String JIRA_PROFILE_PATH = "/secure/ViewProfile.jspa?name=";
	
	private String jiraBaseUrl;
	
	/**
	 * Constructs a ContributorBuilder object.
	 * 
	 * @param jiraApplinksService to connect to Jira via Application Link.
	 */
	public ContributorBuilder(JiraApplinksService jiraApplinksService){
		// User need to make sure they put the JIRA applink as the primary application link to Jira
		if(jiraApplinksService == null){
			throw new IllegalArgumentException("Arguments can't be null.");
		}
        jiraBaseUrl = null;

        Iterable<ApplicationLink> appLinkService = jiraApplinksService.getJiraApplicationLinks();
        if (appLinkService != null || appLinkService.iterator() != null) {
            Iterator<ApplicationLink> appLinks = jiraApplinksService.getJiraApplicationLinks().iterator();
            if (appLinks.hasNext()) {
                jiraBaseUrl = appLinks.next().getRpcUrl().toString();
            }
        }

	}
	
	/**
	 * Create a Contributor based on the username, last commit date, and full name.
	 * ContributorBuilder will construct the picture url and profile page url using
	 * jiraBaseUrl given by the application link.
	 * 
	 * @param username of the contributor
	 * @param lastCommitDate of the contributor
	 * @param fullName of the contributor from Bamboo
	 * @return the Contributor created. If there are application link to Jira,
	 *         Contributor's picture url and profile page url will be set accordingly, 
	 *         otherwise, those fields will be null.
	 */
	public Contributor createContributor(String username, Date lastCommitDate, String fullName){
		if (jiraBaseUrl != null) {
			String pictureUrl = jiraBaseUrl + JIRA_USER_AVATAR_PATH + username;
			String profilePageUrl = jiraBaseUrl + JIRA_PROFILE_PATH + username;
			return new Contributor(username, lastCommitDate, fullName, pictureUrl, profilePageUrl);
		} else {
			return new Contributor(username, lastCommitDate, fullName, "http://lorempixel.com/32/32/cats/", null);
		}
	}
}
