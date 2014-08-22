package com.cobalt.bamboo.plugin.pipeline.cdresult;

import java.util.Date;

public class Contributor {
	private String username;
	private int commitCount;
	private Date lastCommit;
	private String fullname;
	private String pictureUrl;
	private String profilePageUrl;
	
	/**
	 * Constructs a Contributor object.
	 * 
	 * @param username Contributor's username on Bamboo
	 * @param commitTime Contributor's commit date
	 * @param fullname Contributor's full name on Jira, 
	 *                 null if username not found on Jira
	 * @param pictureUrl Contributor's pictureUrl from Jira,
	 *                   null if username not found on Jira 
	 * @param profilePageUrl Contributor's profilePageUrl from Jira,
	 *                       null if username not found on Jira
	 */
	public Contributor(String username, Date commitTime, String fullname, String pictureUrl, String profilePageUrl) {
		this(username, 1, commitTime, fullname, pictureUrl, profilePageUrl);
		
	}
	
	/**
	 * Constructs a Contributor object.
	 * 
	 * @param username Contributor's username on Bamboo
	 * @param commitCount Contributor's commits count
	 * @param commitTime Contributor's commit date
	 * @param fullname Contributor's full name on Jira, 
	 *                 null if username not found on Jira
	 * @param pictureUrl Contributor's pictureUrl from Jira,
	 *                   null if username not found on Jira 
	 * @param profilePageUrl Contributor's profilePageUrl from Jira,
	 *                       null if username not found on Jira
	 */
	public Contributor(String username, int commitCount, Date commitTime, String fullname, String pictureUrl, String profilePageUrl) {
		this.username = username;
		this.commitCount = commitCount;
		this.lastCommit = commitTime;
		this.fullname = fullname;
		this.pictureUrl = pictureUrl;
		this.profilePageUrl = profilePageUrl;
	}
	
	/**
	 * Gets the username of this contributor.
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Gets the number of commits of this contributor after the last deployment.
	 * 
	 * @return the number of commits
	 */
	public int getCommitCount(){
		return commitCount;
	}
	
	/**
	 * Gets the most recent commit time of this contributor.
	 * 
	 * @return the date and time of last commit
	 */
	public Date getLastCommitTime(){
		return lastCommit;
	}
	
	/**
	 * Gets the contributor's first name and last name from Bamboo if 
	 * contributor (author) is linked to a Bamboo user.
	 * 
	 * @return the user's first and last name from Bamboo. If contributor is
	 *         not linked to a Bamboo user, username is returned.
	 */
	public String getFullname() {
		return fullname;
	}
	
	/**
	 * Gets a link to the user's profile picture on Jira.
	 * 
	 * @return the url of the user's profile picture from Jira.
	 */
	public String getPictureUrl() {
		return pictureUrl;
	}
	
	/**
	 * Gets a link to the user's profile page on Jira.
	 * 
	 * @return the url of the user's profile page on Jira.
	 */
	public String getProfilePageUrl() {
		return profilePageUrl;
	}
	
	/**
	 * Updates the last commit time if the given time is more recent.
	 * 
	 * @param commitTime a commit time of this contributor
	 */
	void updateLastCommitTime(Date commitTime){
		if(commitTime.compareTo(lastCommit) > 0){
			lastCommit = commitTime;
		}
	}
	
	/**
	 * Increments the number of commits by 1.
	 */
	void incrementCommitCount(){
		commitCount++;
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof Contributor){
			Contributor other = (Contributor) o;
			if(other.username.equals(this.username)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return this.username.hashCode();
	}
	
}
