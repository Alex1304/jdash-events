package com.github.alex1304.jdashevents.common;

import java.util.ArrayList;
import java.util.List;

import com.github.alex1304.jdash.api.GDHttpClient;
import com.github.alex1304.jdash.api.request.GDLevelHttpRequest;
import com.github.alex1304.jdash.component.GDLevel;
import com.github.alex1304.jdashevents.manager.GDDispatchableEvent;

/**
 * Scans the Daily level to determine whether it has changed or not.
 *
 * @author Alex1304
 */
public class DailyLevelEventScanner extends GDEventScanner<GDLevel> {

	/**
	 * @param client
	 *            - The client to use for the requests
	 */
	public DailyLevelEventScanner(GDHttpClient client) {
		super(client, new GDLevelHttpRequest(-1));
	}

	@Override
	public List<GDDispatchableEvent> compareAndListEvents(GDLevel previousResponse, GDLevel currentResponse) {
		List<GDDispatchableEvent> eventList = new ArrayList<>();
		
		if (previousResponse == null || currentResponse == null ||
				previousResponse.getId() == currentResponse.getId())
			return eventList;
		
		eventList.add(new GDDispatchableEvent(CommonEvents.DAILY_LEVEL_CHANGED.toString(), currentResponse));
		return eventList;
	}

}
