package com.github.alex1304.jdashevents.scanner;

import com.github.alex1304.jdash.api.GDHttpClient;
import com.github.alex1304.jdash.api.request.GDTimelyLevelHttpRequest;
import com.github.alex1304.jdash.component.GDTimelyLevel;

/**
 * Scans a timely level and emits an event when there's a new one
 *
 * @author Alex1304
 */
public class TimelyLevelEventScanner extends ComponentUpdatedEventScanner<GDTimelyLevel> {

	/**
	 * @param client
	 *            - the http client instance to use
	 * @param changedEvent
	 *            - the label of the event it should emit
	 * @param timelyID
	 *            - the ID of the timely level to scan
	 */
	public TimelyLevelEventScanner(GDHttpClient client, String changedEvent, boolean weekly) {
		super(client, new GDTimelyLevelHttpRequest(weekly, client), changedEvent);
	}

	@Override
	public int compare(GDTimelyLevel o1, GDTimelyLevel o2) {
		return (int) (o1.getTimelyNumber() - o2.getTimelyNumber());
	}

	
}
