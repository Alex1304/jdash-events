package com.github.alex1304.jdashevents.scanner;

import com.github.alex1304.jdash.api.GDHttpClient;
import com.github.alex1304.jdash.api.request.GDLevelHttpRequest;
import com.github.alex1304.jdash.component.GDLevel;
import com.github.alex1304.jdash.util.Constants;
import com.github.alex1304.jdashevents.common.CommonEvents;

/**
 * Scans a timely level and emits an event when there's a new one
 *
 * @author Alex1304
 */
public class TimelyLevelEventScanner extends ComponentUpdatedEventScanner<GDLevel> {

	/**
	 * @param client
	 *            - the http client instance to use
	 * @param changedEvent
	 *            - the label of the event it should emit
	 * @param timelyID
	 *            - the ID of the timely level to scan
	 */
	public TimelyLevelEventScanner(GDHttpClient client, String changedEvent, long timelyID) {
		super(client, new GDLevelHttpRequest(Constants.DAILY_LEVEL_ID), CommonEvents.DAILY_LEVEL_CHANGED);
	}

	@Override
	public int compare(GDLevel o1, GDLevel o2) {
		return (int) (o1.getId() - o2.getId());
	}

	
}
