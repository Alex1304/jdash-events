package com.github.alex1304.jdashevents.scanner;

import com.github.alex1304.jdash.api.GDHttpClient;
import com.github.alex1304.jdashevents.common.CommonEvents;

/**
 * Implementation of the timely level scanner for Daily Level
 *
 * @author Alex1304
 */
public class DailyLevelEventScanner extends TimelyLevelEventScanner {

	/**
	 * @param client
	 *            - the HTTP client to use for processing the requests
	 */
	public DailyLevelEventScanner(GDHttpClient client) {
		super(client, CommonEvents.DAILY_LEVEL_CHANGED, false);
	}
}
