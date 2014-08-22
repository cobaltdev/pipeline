package com.cobalt.bamboo.plugin.pipeline.Controllers;

import java.util.Date;
import java.util.HashMap;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Logger;

import com.atlassian.bamboo.security.BambooPermissionManager;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.scheduling.PluginScheduler;

public class CacheRefreshMonitorImpl implements CacheRefreshMonitor, LifecycleAware {
	static final String KEY = CacheRefreshMonitorImpl.class.getName();
	private static final String JOB_NAME = "Pipeline Self-Refresh";
	
	private final Logger logger = Logger.getLogger(CacheRefreshMonitorImpl.class);
	private final PluginScheduler pluginScheduler;
	private final CacheManager cacheManager;
	
	private long interval = 1800000L; // default refresh interval (0.5 hr)
	private Date lastRun = null;
	
	private CacheRefreshMonitorImpl(PluginScheduler pluginScheduler, CacheManager cacheManager){
		this.pluginScheduler = pluginScheduler;
		this.cacheManager = cacheManager;
	}
	
	@Override
	public void onStart() {
		reschedule(interval);
	}

	@Override
	public void reschedule(long interval) {
		this.interval = interval;
		
		// Set up the scheduled job
		pluginScheduler.scheduleJob(JOB_NAME, 
									CacheRefreshTask.class, 
									new HashMap<String,Object>() {{
										put(KEY, CacheRefreshMonitorImpl.this);
									}}, 
									new Date(), 
									interval);
		logger.info("Pipeline results scheduled to refresh every " + interval + "ms");
	}

	@Override
	public void setLastRun(Date date) {
		this.lastRun = date;
	}
	
	@Override
	public Date getLastRefreshDate() {
		return lastRun;
	}
	
	@Override
	public long getScheduledInterval(){
		return interval;
	}

	@Override
	public void refreshCDResults() {
		logger.info("Started...");
		
		// Change the authentication to be system authority
		Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
		SecurityContextHolder.getContext().setAuthentication(BambooPermissionManager.SYSTEM_AUTHORITY);
		cacheManager.putAllWallBoardData();
		
		// Change the authentication back
		SecurityContextHolder.getContext().setAuthentication(oldAuthentication);
		logger.info("Successfully End.");
	}

}
