package com.cobalt.bamboo.plugin.pipeline.cdperformance;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
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

import com.cobalt.bamboo.plugin.pipeline.cdresult.Contributor;

public class CompletionStats {
	private int buildNumber;
	private int numChanges;
	private Date completedDate;
	private Map<String, Contributor> contributors;
	
	/**
	 * Construct a CompletionStats with given build number and completed date
	 * and set number of changes to be 0 with no contributors
	 * @param buildNumber build number of the successful completed build
	 * @param completed completed data of the completed build
	 */
	public CompletionStats(int buildNumber, Date completed){
		this.buildNumber = buildNumber;
		this.completedDate = new Date(completed.getTime());
		this.numChanges = 0;
		contributors = new HashMap<String, Contributor>();
	}
	
	/**
	 * Add given number of changes to the number of changes of this completion
	 * @param changes number of changes to add to this completion
	 */
	void addNumChanges(int changes){
		this.numChanges += changes;
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
		} else {
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
			if(lastCommitTime.compareTo(c.getLastCommitTime()) > 0){
				contributors.put(username, new Contributor(username, c.getCommitCount() + 1, lastCommitTime, c.getFullname(), c.getPictureUrl(), c.getProfilePageUrl()));
			} else {
				contributors.put(username, new Contributor(username, c.getCommitCount() + 1, c.getLastCommitTime(), c.getFullname(), c.getPictureUrl(), c.getProfilePageUrl()));
			}
		}else{
			throw new IllegalArgumentException(username + " doesn't exist in the Contributors List.");
		}
	}
	
	/**
	 * Return the build number of this completion
	 * @return build number of this completion
	 */
	public int getBuildNumber(){
		return buildNumber;
	}
	
	/**
	 * Return the number of changes within this completion
	 * @return number of changes within this completion
	 */
	public int getNumChanges(){
		return numChanges;
	}
	
	/**
	 * Return the completed date of this completion with format
	 * @return completed data of this completion
	 */
	public Date getCompletedDate(){
		return completedDate;
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
	 * Return all contributors sorted by the time of their last commit, contributor
	 * who commits most recently comes first.
	 * Tie breaks by number of commits (one with more commits comes first)
	 * 
	 * @return a sorted list of Contributors by most recent contribute
	 */
	public List<Contributor> getContributorsSortedByLatestCommit(){
		return this.getContributorsSortedBy(new RecentCommitComparator());
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
