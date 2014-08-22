package com.cobalt.bamboo.plugin.pipeline.cache;

import com.cobalt.bamboo.plugin.pipeline.cdperformance.UptimeGrade;
import com.cobalt.bamboo.plugin.pipeline.cdresult.CDResult;

public class WallBoardData {
	public String planKey;
	public CDResult cdresult;
	public UptimeGrade uptimeGrade;
	
	public WallBoardData(String planKey, CDResult cdresult, UptimeGrade uptimeGrade) {
		this.planKey = planKey;
		this.cdresult = cdresult;
		this.uptimeGrade = uptimeGrade;
	}
}
