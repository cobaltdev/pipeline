package com.cobalt.bamboo.plugin.pipeline.cdresult;

import java.util.Date;
import java.util.List;

import com.atlassian.bamboo.author.Author;
import com.atlassian.bamboo.chains.ChainResultsSummary;
import com.atlassian.bamboo.chains.ChainStageResult;
import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.plan.ExecutionStatus;
import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.PlanExecutionManager;
import com.atlassian.bamboo.progressbar.ProgressBar;
import com.atlassian.bamboo.progressbar.ProgressBarImpl;
import com.atlassian.bamboo.project.Project;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;

public class CDResultFactory {
	
	private static final int MAX_BUILD_TO_GET = 10;
	
	/**
	 * Return a CDResult with project name, plan name, days, changes, contributors info since
	 * last deployment and current build information (name, last update time, pipeline stages
	 * with status) based on the given plan and will get all builds from the given 
	 * ResultsSummaryManager.
	 * 
	 * @return a fully constructed CDResult object
	 */
	public static CDResult createCDResult(Plan plan, ContributorBuilder contributorBuilder, 
			   PlanExecutionManager planExecutionManager, ResultsSummaryManager resultsSummaryManager) {
		
		if (plan == null) {
			throw new IllegalArgumentException("Passed in null arguments.");
		}
		
		String planName = plan.getName();
		String planKey = plan.getKey();
		
		Project project = plan.getProject();
		String projectName = project.getName();
		String projectKey = project.getKey();
		
		if (projectName == null || planName == null || !planName.startsWith(projectName + " - ")) {
			throw new IllegalArgumentException("Invalid plan name."
					+ "(plan name should be in the format of \"[project] - [plan]\"");
		}
		
		// planName is in the format of "[project] - [plan]"
		// Strip planName so that it contains purely the plan's name.
		String strippedPlanName = planName.substring(projectName.length() + 3);
		
		CDResult cdresult = new CDResult(projectName, strippedPlanName, projectKey, planKey);
		
		List<ResultsSummary> buildList = resultsSummaryManager.getResultSummariesForPlan(plan, 0, 1);
		if(buildList == null || buildList.size() <= 0){
			// set special current build when there are no builds
			Build currentBuild = new Build(null, null);
			cdresult.setCurrentBuild(currentBuild);
			return cdresult;
		}
		
		setLastDeploymentInfo(cdresult, plan, resultsSummaryManager, contributorBuilder);
		setCurrentBuildInfo(cdresult, buildList, planExecutionManager);
		
		return cdresult;
	}
	
	
	/*
	 * Set the lastDeploymentTime, numChanges, and contributors info since last
	 * deployment in the cdresult. 
	 * If there are no builds, cdresult will maintain the default values. 
	 * If there are no last deployment, lastDeploymentTime will be default, and 
	 * changes and contributors will be since the first build.
	 */
	protected static void setLastDeploymentInfo(CDResult cdresult, Plan plan, ResultsSummaryManager resultsSummaryManager, 
												ContributorBuilder contributorBuilder) {

		// At this point: at least one build in the build list
		
		int totalChanges = 0;
		
		List<ResultsSummary> partialList = resultsSummaryManager.getResultSummariesForPlan(plan, 0, 1);
		// Do nothing if no builds in the build list
		if(partialList == null || partialList.size() <= 0){
			return;
		}
		
		// add changes and contributors of the first build
		// into cdresult
		totalChanges += partialList.get(0).getCommits().size();
		addAllAuthorsInCommits(cdresult, partialList.get(0).getCommits(), contributorBuilder);
		
		int startIndex = 1;
		boolean found = false;
		// get previous builds until reaching the end of builds
		while(!found && (startIndex == 1 || partialList.size() >= MAX_BUILD_TO_GET)){
			
			// get previous builds
			partialList = resultsSummaryManager.getResultSummariesForPlan(plan, startIndex, MAX_BUILD_TO_GET);
			
			for (int i = 0; i < partialList.size(); i++) { 
				ChainResultsSummary currentBuild = (ChainResultsSummary) partialList.get(i);	
				
				// check if current build is the last deployment
				if (!currentBuild.isContinuable() && currentBuild.isSuccessful()) {
					cdresult.setLastDeploymentTime(currentBuild.getBuildCompletedDate()); 
					found = true;
					break;
				}
				
				List<Commit> commits = currentBuild.getCommits();
				totalChanges += commits.size();
				addAllAuthorsInCommits(cdresult, commits, contributorBuilder);
			}
			
			//update starting point
			startIndex += MAX_BUILD_TO_GET;
		}
		
		// set #changes (contributors and date are set in the progress)
		cdresult.setNumChanges(totalChanges);
	}
	
	/*
	 * Set the current build information (build number, build key...), last build
	 * updated time and all pipeline stages.
	 * Current build may be null if there's no builds in build list.
	 * Last build updated time will be most recent time from build queued, started, completed
	 * and jobs completed time.
	 * Last build updated time may be null if it hasn't queued yet.
	 */
	protected static void setCurrentBuildInfo(CDResult cdresult, List<ResultsSummary> buildList, 
												PlanExecutionManager planExecutionManager) {
		if (buildList != null && buildList.size() > 0) {
			
			// get the last build and set current build info.
			ResultsSummary currentResult = buildList.get(0);
			
			// get build progress info if currentResult is building
			ExecutionStatus status = planExecutionManager.getExecutionStatus(currentResult.getPlanResultKey());
			ProgressBar progressBar = null;
			if (status != null) {  // currentResult currently building
				progressBar = new ProgressBarImpl(status);
			}
			
			// set the current build and pipeline stages.
			ChainResultsSummary pipeline = (ChainResultsSummary) currentResult;
			
			Build currentBuild = new Build(pipeline, progressBar);
			cdresult.setCurrentBuild(currentBuild);
			
			setPipelineStages(cdresult, pipeline);
			
			// set last update time.
			setLastUpdateTime(cdresult, pipeline);
		} else {
			// set special current build when there are no builds
			Build currentBuild = new Build(null, null);
			cdresult.setCurrentBuild(currentBuild);
		}
	}
	
	
	/*
	 * Add all contributors of the given commits to the contributors list.
	 * Construct of list of changes since last completion
	 */
	protected static void addAllAuthorsInCommits(CDResult cdresult, List<Commit> commits, 
													ContributorBuilder contributorBuilder) {
		for(Commit commit : commits) {
			Author author = commit.getAuthor();
			// Get the linked username first to get proper username that the
			// author is using in Atlassian products. (Sometimes the author name
			// might have email or other identification attached.)
			String username = author.getLinkedUserName();
			if (username == null) {
				username = author.getName();
			}

			if (!cdresult.containsContributor(username)) {
				Contributor contributor = contributorBuilder.createContributor(username, commit.getDate(), author.getFullName());
				cdresult.addContributor(contributor);
			} else {
				cdresult.updateContributor(username, commit.getDate());
			}
		}
	}

	
	/*
	 * Set the list of PipelineStage in cdresult with the given build result.
	 */
	protected static void setPipelineStages(CDResult cdresult, ChainResultsSummary buildResult) {
		cdresult.resetPipelineStagesList();
		List<ChainStageResult> stages = buildResult.getStageResults();
		for (ChainStageResult stageResult : stages) {
			PipelineStage stage = new PipelineStage(stageResult);
			cdresult.addPipelineStageToList(stage);
		}
	}
	
	/*
	 * Set the last update time to be:
	 * - last build complete time
	 * - If not completed, then complete time of last completed job 
	 * - If no job complted, then build start time
	 * - If not started, then build queued time
	 */
	protected static void setLastUpdateTime(CDResult cdresult, ChainResultsSummary buildResult){
		Date lastUpdate = buildResult.getBuildCompletedDate();
		if(lastUpdate == null){
			Date startTime = buildResult.getBuildDate();
			if (startTime == null){
				// update last update time to be build started time
				cdresult.updateLastUpdateTime(buildResult.getQueueTime());
			} else {
				// update last update time to be build queued time
				cdresult.updateLastUpdateTime(startTime);
			}
		}else{
			// update last update time to be build completed time
			cdresult.updateLastUpdateTime(lastUpdate);
		}
		
		// update last update time with all jobs completed time (if any)
		List<ResultsSummary> jobResults = buildResult.getOrderedJobResultSummaries();
		for(ResultsSummary jobResult : jobResults){
			cdresult.updateLastUpdateTime(jobResult.getBuildCompletedDate());
		}
	}
}
