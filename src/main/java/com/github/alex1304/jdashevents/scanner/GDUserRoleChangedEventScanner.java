package com.github.alex1304.jdashevents.scanner;

import com.github.alex1304.jdash.api.GDHttpClient;
import com.github.alex1304.jdash.component.GDUser;
import com.github.alex1304.jdashevents.common.CommonEvents;

/**
 * Scans for changes on a user's role in Geometry Dash
 *
 * @author Alex1304
 */
public class GDUserRoleChangedEventScanner extends GDUserEventScanner {

	/**
	 * @param client
	 *            - the http client instance to use
	 * @param userID
	 *            - the ID of the user to scan
	 */
	public GDUserRoleChangedEventScanner(GDHttpClient client, long userID) {
		super(client, CommonEvents.USER_ROLE_CHANGED, userID);
	}

	@Override
	public int compare(GDUser o1, GDUser o2) {
		return o1.getRole() != o2.getRole() ? -1 : 0;
	}

}
