package com.github.alex1304.jdashevents.scanner;

import com.github.alex1304.jdash.api.GDHttpClient;
import com.github.alex1304.jdashevents.common.CommonEvents;

/**
 * Implementation of the timely level scanner for Weekly Demon
 *
 * @author Alex1304
 */
public class WeeklyDemonEventScanner extends TimelyLevelEventScanner {

	/**
	 * @param client
	 *            - the HTTP client to use for processing the requests
	 */
	public WeeklyDemonEventScanner(GDHttpClient client) {
		super(client, CommonEvents.WEEKLY_DEMON_CHANGED, true);
	}
}
