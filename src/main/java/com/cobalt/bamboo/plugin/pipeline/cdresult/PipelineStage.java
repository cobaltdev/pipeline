package com.cobalt.bamboo.plugin.pipeline.cdresult;

import com.atlassian.bamboo.builder.BuildState;
import com.atlassian.bamboo.builder.LifeCycleState;
import com.atlassian.bamboo.chains.ChainStageResult;

/**
 * Represents a single stage in the pipeline.
 */
public class PipelineStage {
	
	private ChainStageResult stageResult;
	
	/**
	 * Constructs a PipelineStage object.
	 * 
	 * @param stageName name of this pipeline stage
	 * @param buildState state of this pipeline stage in the build
	 */
	public PipelineStage(ChainStageResult stageResult) {
		this.stageResult = stageResult;
	}
	
	/**
	 * Gets the stage name of this pipeline stage
	 * 
	 * @return the stageName
	 */
	public String getStageName() {
		return stageResult.getName();
	}
	
	/**
	 * Gets the life cycle state of this pipeline stage.
	 * String representation of the state, via toString(), are
	 * 'FINISHED', 'IN_PROGRESS', 'NOT_BUILT', 'PENDING', and 'QUEUED'.
	 * 
	 * @return the life cycle state, which can be FINISHED, IN_PROGRESS, 
	 *         NOT_BUILT, PENDING, or QUEUED.
	 */
	public LifeCycleState getLifeCycleState() {
		return stageResult.getLifeCycleState();
	}
	
	/**
	 * Gets the build state of this pipeline stage.
	 * String representation of the state, via toString(), are
	 * 'FAILED', 'UNKNOWN', and 'SUCCESSFUL'.
	 * 
	 * @return the build state, which can be FAILED, UNKNOWN, or SUCCESSFUL.
	 */
	public BuildState getBuildState() {
		return stageResult.getState();
	}
	
	
	/**
	 * Return true if this pipeline stage was set to be manual upon creation.
	 * 
	 * @return true if this pipeline stage was set to be manual upon creation,
	 *         false otherwise.
	 */
	public boolean isManual() {
		return stageResult.isManual();
	}
	
	/**
	 * Get the CDPipelineState, which is based on buildState and lifeCycleState but 
	 * only includes the states that are to the interest of our users, of this pipeline stage.
	 * 
	 * @return the CDPipelineState, which can be SUCCESS, FAILED, IN_PROGRESS (default), 
	 * 			NOT_BUILT, MANUALLY_PAUSED
	 */
	public CDPipelineState getCDPipelineState() {
		// Check the buildState. Only check lifeCycleState if buildState is UNKNOWN
		if (getBuildState() == BuildState.SUCCESS) {
			return CDPipelineState.CD_SUCCESS;
		} else if  (getBuildState() == BuildState.FAILED) {
			return CDPipelineState.CD_FAILED;
		} else { 	// BuildState == UNKNOWN) 
			if (getLifeCycleState() == LifeCycleState.IN_PROGRESS) {
				return CDPipelineState.CD_IN_PROGRESS;
			} else if (getLifeCycleState() == LifeCycleState.NOT_BUILT && isManual()) {
				return CDPipelineState.CD_MANUALLY_PAUSED;
			} else { 
				return CDPipelineState.CD_NOT_BUILT;
			}
		}
	}
}
