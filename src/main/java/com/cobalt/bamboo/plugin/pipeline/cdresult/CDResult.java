package com.cobalt.bamboo.plugin.pipeline.cdresult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * CDResult contains all the information needed for one single row
 * of the table display for CDPipeline Plugin Project.
 */
public class CDResult {
	private String projectName, planName;
	private String projectKey, planKey;
	private Date lastDeploymentTime, lastUpdate;
	private int numChanges;
	
	private Map<String, Contributor> contributors;
	private Build currentBuild;
	private List<PipelineStage> pipelineStages;

	/**
	 * Construct a CDResult Object.
	 * Access modifier left out intentionally for package protection.
	 * 
	 * @param projectName
	 * @param planName
	 */
	CDResult(String projectName, String planName, String projectKey, String planKey) {
		this.projectName = projectName;
		this.planName = planName;
		this.projectKey = projectKey;
		this.planKey = planKey;
		
		contributors = new HashMap<String, Contributor>();
		pipelineStages = new ArrayList<PipelineStage>();
	}
	
	/**
	 * Set lastDeploymentTime to the given date.
	 * By default, the lastDeploymentTime will be null.
	 * Access modifier left out intentionally for package protection.
	 * 
	 * @param lastDeployment
	 */
	void setLastDeploymentTime(Date lastDeployment) {
		if(lastDeployment != null){
			this.lastDeploymentTime = new Date(lastDeployment.getTime());
		}
	}
	
	/**
	 * Update last update time with the given date.
	 * By default, the lastUpdateTime will be null.
	 * If current lastUpdateTime is null, set last update time to the given date;
	 * otherwise, update last update time if the given date is more recent.
	 * Access modifier left out intentionally for package protection.
	 * 
	 * @param lastUpdate
	 */
	void updateLastUpdateTime(Date lastUpdate) {
		if(lastUpdate != null && (this.lastUpdate == null || this.lastUpdate.before(lastUpdate))) {
				this.lastUpdate = new Date(lastUpdate.getTime());
		}
	}
	
	/**
	 * Set number of changes to the given number.
	 * By default, numChanges will be 0.
	 * Access modifier left out intentionally for package protection.
	 * 
	 * @param numChanges
	 */
	void setNumChanges(int numChanges) {
		this.numChanges = numChanges;
	}
	
	/**
	 * Add given contributor to contributors set.
	 * Throws IllegalArgumentException if contributor already exists in Contributors List.
	 * By default, contributors will be an empty list.
	 * Access modifier left out intentionally for package protection.
	 * 
	 * @param contributor
	 */
	void addContributor(Contributor contributor) {
		if(contributors.containsKey(contributor.getUsername())){
			throw new IllegalArgumentException(contributor.getUsername() + " already exists in the Contributors List.");
		}else{
			contributors.put(contributor.getUsername(), contributor);
		}
	}
	
	/**
	 * Update the contributor with the given username with the new commit time
	 * and increment its number of commits.
	 * Access modifier left out intentionally for package protection.
	 * Throws IllegalArgumentException if contributor doesn't exist in Contributors List.
	 * 
	 * @param username
	 * @param lastCommitTime
	 */
	void updateContributor(String username, Date lastCommitTime){
		if(contributors.containsKey(username)){
			Contributor c = contributors.get(username);
			c.incrementCommitCount();
			c.updateLastCommitTime(lastCommitTime);
		}else{
			throw new IllegalArgumentException(username + " doesn't exist in the COntributors List.");
		}
	}
	
	/**
	 * Resets the pipelineStages list to an empty list.
	 * Access modifier left out intentionally for package protection.
	 */
	void resetPipelineStagesList() {
		pipelineStages.clear();
	}
	
	/**
	 * Add given PipelineStage to the pipelineStages list.
	 * By default, pipelineStages will be an empty list.
	 * Access modifier left out intentionally for package protection.
	 * 
	 * @param stage
	 */
	void addPipelineStageToList(PipelineStage stage) {
		pipelineStages.add(stage);
	}
	
	/**
	 * Set current build information to the given Build.
	 * 
	 * @param current
	 */
	void setCurrentBuild(Build current){
		this.currentBuild = current;
	}
	
	/**
	 * Get the project name associated with this result.
	 * 
	 * @return project name
	 */
	public String getProjectName() {
		return projectName;
	}
	
	/**
	 * Get the plan name associated with this result.
	 * 
	 * @return plan name
	 */
	public String getPlanName() {
		return planName;
	}
	
	/**
	 * Get the project key associated with this result.
	 * 
	 * @return project key
	 */
	public String getProjectKey() {
		return projectKey;
	}
	
	/**
	 * Get the plan key associated with this result.
	 * 
	 * @return plan key
	 */
	public String getPlanKey() {
		return planKey;
	}
	
	/**
	 * Return last deployment date and time.
	 * Return null if there's no deployment yet.
	 * 
	 * @return date/time of last deployment
	 */
	public Date getLastDeploymentTime(){
		if(lastDeploymentTime == null){
			return null;
		}
		return new Date(lastDeploymentTime.getTime());
	}
	
	/**
	 * Return days since last deployment until current date/time.
	 * Return -1 if current date is before last deployment date or 
	 * no last deployment is found.
	 * 
	 * @return the number of days from last deployment date to current
	 */
	public int getDaysSinceDeploymentFromCurrent(){
		Date currentDate = new Date();
		if(this.lastDeploymentTime == null || this.lastDeploymentTime.compareTo(currentDate) > 0){
			return -1;
		}
		long deploymentTime = this.lastDeploymentTime.getTime();
		long currentTime = currentDate.getTime();
		return (int) ((currentTime - deploymentTime) / (1000 * 60 * 60 * 24));
	}
	
	/**
	 * Return days since last update until current date/time.
	 * Return -1 if current date is before last update date or 
	 * no last update is found.
	 * 
	 * @return the number of days from last update date to current
	 */
	public int getDaysSinceLastUpdateFromCurrent(){
		Date currentDate = new Date();
		if(this.lastUpdate == null || this.lastUpdate.compareTo(currentDate) > 0){
			return -1;
		}
		long updateTime = this.lastUpdate.getTime();
		long currentTime = currentDate.getTime();
		return (int) ((currentTime - updateTime) / (1000 * 60 * 60 * 24));
	}
	
	/**
	 * Return days since last deployment until last update date/time.
	 * Return -1 if last update date is before last deployment date or 
	 * no last deployment or last update is found.
	 * 
	 * @return the number of days from last deployment date to last update date
	 */
	public int getDaysSinceDeploymentFromLastUpdate(){
		if(this.lastUpdate == null || this.lastDeploymentTime == null || this.lastDeploymentTime.compareTo(this.lastUpdate) > 0){
			return -1;
		}
		long deploymentTime = this.lastDeploymentTime.getTime();
		long currentTime = this.lastUpdate.getTime();
		return (int) ((currentTime - deploymentTime) / (1000 * 60 * 60 * 24));
	}
	
	/**
	 * Return last update (the most current build) date and time.
	 * Return null if there are no builds.
	 * 
	 * @return date/time of last update
	 */
	public Date getLastUpdateTime(){
		if(lastUpdate == null){
			return null;
		}else{
			return new Date(lastUpdate.getTime());
		}
	}
	
	/**
	 * Return number of changes(commits) since last deployment.
	 * 
	 * @return number of changes since last deployment
	 */
	public int getNumChanges(){
		return this.numChanges;
	}
	
	/**
	 * Return current build information. If there are no build, the
	 * build number of current build returned will be -1 and build key will be null.
	 * 
	 * @return a Build object that contains information about the most
	 *         current build.
	 */
	public Build getCurrentBuild(){
		return this.currentBuild;
	}
	
	/**
	 * Check if this already contains the contributor with the given username.
	 * 
	 * @param username of the contributor
	 * @return true if this.contributors already contains the contributor with
	 *         the given username, false otherwise. 
	 */
	public boolean containsContributor(String username) {
		return contributors.containsKey(username);
	}
	
	/**
	 * Return all contributors since last deployment.
	 * 
	 * @return a Set of Contributor
	 */
	Set<Contributor> getContributors(){
		Set<Contributor> results = new HashSet<Contributor>();
		for(String name : contributors.keySet()){
			results.add(contributors.get(name));
		}
		return Collections.unmodifiableSet(results);
	}
	
	/**
	 * Return all pipeline stages of the current build.
	 * 
	 * @return an ordered list of PipelineStates starting with the first stage.
	 */
	public List<PipelineStage> getPipelineStages(){
		return Collections.unmodifiableList(pipelineStages);
	}
	
	/**
	 * Return all contributors sorted by the time of their last commit, contributor
	 * who commits most recently comes first.
	 * Tie breaks by number of commits (one with more commits comes first)
	 * 
	 * @return a sorted list of Contributors by most recent contribute
	 */
	public List<Contributor> getContributorsSortedByLatestCommit(){
		return this.getContributorsSortedBy(new RecentCommitComparator());
	}
	
	// Return a list of all Contributors sorted by the given comparator of Contributors.
	private List<Contributor> getContributorsSortedBy(Comparator<Contributor> c){
		TreeSet<Contributor> results = new TreeSet<Contributor>(c);
		results.addAll(contributors.values());
		Iterator<Contributor> it = results.descendingIterator();
		List<Contributor> list = new ArrayList<Contributor>();
		while(it.hasNext()){
			list.add(it.next());
		}
		return list;
	}
	
	// A Comparator which compares the contributor by number of commits.
	// Contributor with more commits is greater than contributor with less commits.
	// For Contributors with the same number of commits, Contributor with more recent
	// last commit date is greater.
	private class CommitCountComparator implements Comparator<Contributor>{

		@Override
		public int compare(Contributor a, Contributor b) {
			if(a.getCommitCount() != b.getCommitCount()){
				return a.getCommitCount() < b.getCommitCount() ? -1 : 1;
			}else{
				int result = a.getLastCommitTime().compareTo(b.getLastCommitTime());
				if(result == 0){
					return 1;
				}
				return result; 
			}
		}
		
	}
	
	// A Comparator which compares the contributor by number of commits.
	// Contributor with more recent last commit date is greater than contributor
	// with less recent last commit date.
	// For Contributors with the same last commit date, Contributor with more
	// commits is greater.
	private class RecentCommitComparator implements Comparator<Contributor>{

		@Override
		public int compare(Contributor a, Contributor b) {
			int result = a.getLastCommitTime().compareTo(b.getLastCommitTime());
			if(result == 0){
				return a.getCommitCount() < b.getCommitCount() ? -1 : 1;
			}
			return result;
		}
		
	}
}
