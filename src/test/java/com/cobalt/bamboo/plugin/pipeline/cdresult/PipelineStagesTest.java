package com.cobalt.bamboo.plugin.pipeline.cdresult;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.atlassian.bamboo.builder.BuildState;
import com.atlassian.bamboo.builder.LifeCycleState;
import com.atlassian.bamboo.chains.ChainResultsSummary;
import com.atlassian.bamboo.chains.ChainStageResult;


public class PipelineStagesTest {
	ChainResultsSummary noStage, oneStage, chain1, chain2;
	
	
	@Before
	public void setUp(){
		setUpNoStageChain();
		setUpOneStageChain();
		setUpChain1();
	}
	
	@Test
	public void testEmptyStageChain() {
		CDResult cdr = new CDResult("test", "test", "test", "test");
		CDResultFactory.setPipelineStages(cdr, noStage);
		List<PipelineStage> pipes = cdr.getPipelineStages();
		assertEquals("The pipeline stages list should be empty", 0, pipes.size());
	}

	@Test
	public void testOneStageChain(){
		CDResult cdr = new CDResult("test", "test", "test", "test");
		CDResultFactory.setPipelineStages(cdr, oneStage);
		List<PipelineStage> pipes = cdr.getPipelineStages();
		assertEquals("The pipeline stages list should have one element", 1, pipes.size());
		assertEquals("The stage name should match", "test", pipes.get(0).getStageName());
		assertEquals("The stage life cycle state should match", LifeCycleState.FINISHED, pipes.get(0).getLifeCycleState());
		assertEquals("The stage build state should match", BuildState.SUCCESS, pipes.get(0).getBuildState());
	}
	
	@Test
	public void testLengthOfStagesChainWithIdenticalName(){
		ChainResultsSummary chain5 = this.getIdenticalStageChainWithGivenSize(5);
		CDResult cdr = new CDResult("test", "test", "test", "test");
		CDResultFactory.setPipelineStages(cdr, chain5);
		List<PipelineStage> pipes = cdr.getPipelineStages();
		assertEquals("The pipeline stages list should match", 5, pipes.size());
		ChainResultsSummary chain100 = this.getIdenticalStageChainWithGivenSize(100);
		cdr = new CDResult("test", "test", "test", "test");
		CDResultFactory.setPipelineStages(cdr, chain100);
		pipes = cdr.getPipelineStages();
		assertEquals("The pipeline stages list should match", 100, pipes.size());
	}
	
	@Test
	public void testFirstStageInChainWithDifferentStates(){
		CDResult cdr = new CDResult("test", "test", "test", "test");
		CDResultFactory.setPipelineStages(cdr, chain1);
		List<PipelineStage> pipes = cdr.getPipelineStages();
		assertEquals("The first stage name should match", "a", pipes.get(0).getStageName());
		assertEquals("The first stage life cycle state should match", LifeCycleState.FINISHED, pipes.get(0).getLifeCycleState());
		assertEquals("The first stage build state should match", BuildState.SUCCESS, pipes.get(0).getBuildState());
	}
	
	@Test
	public void testSecondStageInChainWithDifferentStates(){
		CDResult cdr = new CDResult("test", "test", "test", "test");
		CDResultFactory.setPipelineStages(cdr, chain1);
		List<PipelineStage> pipes = cdr.getPipelineStages();
		assertEquals("The second stage name should match", "b", pipes.get(1).getStageName());
		assertEquals("The second stage life cycle state should match", LifeCycleState.FINISHED, pipes.get(1).getLifeCycleState());
		assertEquals("The second stage build state should match", BuildState.FAILED, pipes.get(1).getBuildState());
	}
	
	@Test
	public void testThirdStageInChainWithDifferentStates(){
		CDResult cdr = new CDResult("test", "test", "test", "test");
		CDResultFactory.setPipelineStages(cdr, chain1);
		List<PipelineStage> pipes = cdr.getPipelineStages();
		assertEquals("The third stage name should match", "c", pipes.get(2).getStageName());
		assertEquals("The third stage life cycle state should match", LifeCycleState.NOT_BUILT, pipes.get(2).getLifeCycleState());
		assertEquals("The third stage state should match", BuildState.UNKNOWN, pipes.get(2).getBuildState());
	}
	
	
	// ========== Private Helper Methods ==========
	
	private ChainStageResult getStageWithGivenNameAndState(String name, LifeCycleState lifeState, BuildState buildState){
		ChainStageResult stageResult = mock(ChainStageResult.class);
		when(stageResult.getName()).thenReturn(name);
		when(stageResult.getLifeCycleState()).thenReturn(lifeState);
		when(stageResult.getState()).thenReturn(buildState);
		return stageResult;
	}
	
	private ChainResultsSummary getStageChainWithGivenStages(List<ChainStageResult> stages){
		ChainResultsSummary chain = mock(ChainResultsSummary.class);
		when(chain.getStageResults()).thenReturn(stages);
		return chain;
	}
	
	private ChainResultsSummary getIdenticalStageChainWithGivenSize(int n){
		ChainResultsSummary chain = mock(ChainResultsSummary.class);
		List<ChainStageResult> stages = new ArrayList<ChainStageResult>();
		for(int i = 0; i < n; i++){
			stages.add(this.getStageWithGivenNameAndState("a", LifeCycleState.FINISHED, BuildState.SUCCESS));
		}
		when(chain.getStageResults()).thenReturn(stages);
		return chain;
	}
	
	private void setUpNoStageChain(){
		noStage = getStageChainWithGivenStages(new ArrayList<ChainStageResult>());
	}
	
	private void setUpOneStageChain(){
		List<ChainStageResult> stages = new ArrayList<ChainStageResult>();
		stages.add(this.getStageWithGivenNameAndState("test", LifeCycleState.FINISHED, BuildState.SUCCESS));
		oneStage = this.getStageChainWithGivenStages(stages);
	}
	
	private void setUpChain1(){
		List<ChainStageResult> stages = new ArrayList<ChainStageResult>();
		stages.add(this.getStageWithGivenNameAndState("a", LifeCycleState.FINISHED, BuildState.SUCCESS));
		stages.add(this.getStageWithGivenNameAndState("b", LifeCycleState.FINISHED, BuildState.FAILED));
		stages.add(this.getStageWithGivenNameAndState("c", LifeCycleState.NOT_BUILT, BuildState.UNKNOWN));
		chain1 = this.getStageChainWithGivenStages(stages);
	}
}
