package com.cobalt.bamboo.plugin.pipeline.cdresult;

/**
 * Customized states that only represent states concerned by the pipeline view.
 */
public enum CDPipelineState {
	CD_SUCCESS, CD_FAILED, CD_IN_PROGRESS, CD_NOT_BUILT, CD_MANUALLY_PAUSED, CD_QUEUED
}
