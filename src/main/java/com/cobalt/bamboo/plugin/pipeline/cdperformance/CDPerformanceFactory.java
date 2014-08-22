package com.cobalt.bamboo.plugin.pipeline.cdperformance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.atlassian.bamboo.author.Author;
import com.atlassian.bamboo.chains.ChainResultsSummary;
import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.cobalt.bamboo.plugin.pipeline.cdresult.Contributor;
import com.cobalt.bamboo.plugin.pipeline.cdresult.ContributorBuilder;

public class CDPerformanceFactory {
	
	/**
	 * Create a CDPreformance for the given buildList.
	 * The success percentage is based on all existing builds.
	 * The average frequency and average changes are based on all builds before the most recent completion.
	 * If there's no build, return null.
	 * If there's no completion found, average frequency and average changes will be -1 and the completions list
	 * will be empty.
	 * @param buildList all builds to be analyzed
	 * @param contributorBuilder a contributor builder used to create contributor
	 * @return
	 */
	public static CDPerformance createCDPerformance(List<ResultsSummary> buildList, ContributorBuilder contributorBuilder){
		// If no build, return null
		if(buildList == null || buildList.size() <= 0){
			return null;
		}
		
		// Get total number of builds and completed date of the first build
		// Initialize all counters
		int totalBuild = 0;
		int totalSuccess = 0;
		int totalChanges = 0;
		List<CompletionStats> completions = new ArrayList<CompletionStats>();
		Date lastCompletionDate = null;
		Date startDate = buildList.get(buildList.size() - 1).getBuildCompletedDate();
		
		boolean getRecentCompletion = false;
		CompletionStats currentCompletion = null;
		for(int i = 0; i < buildList.size(); i++){
			
			ChainResultsSummary currentBuild = (ChainResultsSummary) buildList.get(i);
			
			Date currentTime = currentBuild.getBuildCompletedDate();
			
			if (currentTime != null) {
				// Increment number of successes if the current build is a success
				if(currentBuild.isSuccessful()){
					totalSuccess++;
				}
				
				totalBuild++;
				
				// Find the most recent completion and set the completed date of the most recent completion
				if(!getRecentCompletion && !currentBuild.isContinuable() && currentBuild.isSuccessful()){
					getRecentCompletion = true;
					lastCompletionDate = currentTime;
				}
			}
			
			// Before the most recent completion
			if(getRecentCompletion){
				// If current build is a successful completed build
				if(!currentBuild.isContinuable() && currentBuild.isSuccessful()){
					// put the last completion to completions list
					if(currentCompletion != null){
						completions.add(currentCompletion);
					}
					// create a new completion
					currentCompletion = new CompletionStats(currentBuild.getBuildNumber(), currentBuild.getBuildCompletedDate());
				}
				
				// Add changes of current build total changes and current completion
				// Also add all contributors to current completion
				totalChanges += currentBuild.getCommits().size();
				currentCompletion.addNumChanges(currentBuild.getCommits().size());
				addAllAuthorsInCommits(currentCompletion, currentBuild.getCommits(), contributorBuilder);
			}
		}
		
		// Add the oldest completion to the completion list
		if(currentCompletion != null){
			completions.add(currentCompletion);
		}
		return new CDPerformance(totalBuild, totalSuccess, totalChanges, startDate, lastCompletionDate, completions);
	}
	
	/*
	 * Add all contributors of the given commits to the contributors list.
	 */
	protected static void addAllAuthorsInCommits(CompletionStats completion, List<Commit> commits, ContributorBuilder contributorBuilder){
		for(Commit commit : commits) {
			Author author = commit.getAuthor();
			// Get the linked username first to get proper username that the
			// author is using in Atlassian products. (Sometimes the author name
			// might have email or other identification attached.)
			String username = author.getLinkedUserName();
			if (username == null) {
				username = author.getName();
			}

			if (!completion.containsContributor(username)) {
				Contributor contributor = contributorBuilder.createContributor(username, commit.getDate(), author.getFullName());
				completion.addContributor(contributor);
			} else {
				completion.updateContributor(username, commit.getDate());
			}
		}
	}
}
