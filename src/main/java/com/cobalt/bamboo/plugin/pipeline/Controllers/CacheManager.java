package com.cobalt.bamboo.plugin.pipeline.Controllers;

import java.util.List;

import com.cobalt.bamboo.plugin.pipeline.cache.WallBoardData;
import com.cobalt.bamboo.plugin.pipeline.cdresult.CDResult;

/**
 * CacheManager manages all activities for cache.
 */
public interface CacheManager {
	
	/**
	 * Put WallBoardData for all plans into the cache. All of existing data (if any)
	 * will be replaced.
	 */
	public void putAllWallBoardData();
	
	/**
	 * Update the WallBoardData in the cache for the given plan. If the plan already
	 * exists in the cache, the associated WallBoardData will be replaced. If the plan
	 * doesn't exist in the cache yet, a new plan will be created with it's new WallBoardData.
	 * 
	 * @param planKey of the plan to update in the cache
	 */
	/**
	 * Update the WallBoardData in the cache for the given plan. If the plan already
	 * exists in the cache, the associated WallBoardData will be replaced. If the plan
	 * doesn't exist in the cache yet, a new plan will be created with it's new WallBoardData.
	 * 
	 * @param planKey of the plan to update in the cache
	 * @param updateUptimeGrade A boolean to indicate whether to update the UptimeGrade in the cache
	 */
	public void updateWallBoardDataForPlan(String planKey, boolean updateUptimeGrade);
	
	/**
	 * Get WallBoardData for all of the plans.
	 * 
	 * @return a list of WallBoardData representing all plans.
	 */
	public List<WallBoardData> getAllWallBoardData();
	
	/**
	 * Clear everything in the cache.
	 */
	public void clearCache();
}
