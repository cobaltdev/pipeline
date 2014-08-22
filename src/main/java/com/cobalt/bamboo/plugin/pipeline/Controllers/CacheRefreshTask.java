package com.cobalt.bamboo.plugin.pipeline.Controllers;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.sal.api.scheduling.PluginJob;

public class CacheRefreshTask implements PluginJob {
	private final Logger logger = Logger.getLogger(CacheRefreshTask.class);
	
	@Override
	public void execute(Map<String, Object> jobDataMap) {
		
		final CacheRefreshMonitorImpl monitor = (CacheRefreshMonitorImpl) jobDataMap.get(CacheRefreshMonitorImpl.KEY);
		if (monitor == null) {
			logger.error("Can't refresh cache as scheduled.");
		} else {
			monitor.setLastRun(new Date());
			monitor.refreshCDResults();
		}
	}

}
