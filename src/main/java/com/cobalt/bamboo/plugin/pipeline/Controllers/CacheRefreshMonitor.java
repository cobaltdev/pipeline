package com.cobalt.bamboo.plugin.pipeline.Controllers;

import java.util.Date;

public interface CacheRefreshMonitor {
	
	/**
	 * Reschedule the cache refresh to start from now with the given interval
	 * @param the interval between two refreshes
	 */
	public void reschedule(long interval);
	
	/**
	 * Set the last run time to the given date
	 * @param the last run time to set
	 */
	public void setLastRun(Date date);
	
	/**
	 * Clear the cached cdresults, get all cdresults and store in the cache
	 */
	public void refreshCDResults();
	
	/**
	 * Return the datetime of last cache refresh
	 * @return the datetime of last cache refresh; if not exists, return null.
	 */
	public Date getLastRefreshDate();
	
	/**
	 * Return the interval between two scheduled refreshes
	 * @return the interval between two scheduled refreshes
	 */
	public long getScheduledInterval();
}
