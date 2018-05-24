package com.github.alex1304.jdashevents.scanner;

import com.github.alex1304.jdash.api.GDHttpClient;
import com.github.alex1304.jdash.api.request.GDUserHttpRequest;
import com.github.alex1304.jdash.component.GDUser;

/**
 * Scans for state changes on a particular user in Geometry Dash
 *
 * @author Alex1304
 */
public abstract class GDUserEventScanner extends ComponentUpdatedEventScanner<GDUser> {

	/**
	 * @param client
	 *            - the http client instance to use
	 * @param changedEvent
	 *            - the label of the event it should emit
	 * @param userID
	 *            - the ID of the user to scan
	 */
	public GDUserEventScanner(GDHttpClient client, String changedEvent, long userID) {
		super(client, new GDUserHttpRequest(userID), changedEvent);
	}
}
