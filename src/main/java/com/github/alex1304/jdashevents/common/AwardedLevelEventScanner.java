package com.github.alex1304.jdashevents.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.github.alex1304.jdash.api.GDHttpClient;
import com.github.alex1304.jdash.api.request.GDLevelHttpRequest;
import com.github.alex1304.jdash.api.request.GDLevelSearchHttpRequest;
import com.github.alex1304.jdash.component.GDComponentList;
import com.github.alex1304.jdash.component.GDLevel;
import com.github.alex1304.jdash.component.GDLevelPreview;
import com.github.alex1304.jdash.exceptions.GDAPIException;
import com.github.alex1304.jdash.util.Constants;
import com.github.alex1304.jdashevents.manager.GDDispatchableEvent;

/**
 * Scans the Awarded level section and emits events when a level is added,
 * updated, or deleted from the section.
 *
 * @author Alex1304
 */
public class AwardedLevelEventScanner extends GDEventScanner<GDComponentList<GDLevelPreview>> {

	/**
	 * @param client
	 *            - The client to use for the requests
	 */
	public AwardedLevelEventScanner(GDHttpClient client) {
		super(client,new GDLevelSearchHttpRequest(Constants.LEVEL_SEARCH_TYPE_AWARDED, "", new HashSet<>(),
				new HashSet<>(), 0, false, false, false, false, false, false, false, false, Constants.LEVEL_SEARCH_DIFF_ALL));
	}

	@Override
	public List<GDDispatchableEvent> compareAndListEvents(GDComponentList<GDLevelPreview> previousResponse, GDComponentList<GDLevelPreview> currentResponse) {
		List<GDDispatchableEvent> eventList = new ArrayList<>();
		
		if (previousResponse == null || previousResponse.equals(currentResponse))
			return eventList;
		
		GDComponentList<GDLevelPreview> levelsAdded = new GDComponentList<>(
				currentResponse.stream().filter(x -> !previousResponse.contains(x)).collect(Collectors.toList()));
		
		GDComponentList<GDLevelPreview> levelsRemoved = new GDComponentList<>(
				previousResponse.stream().filter(x -> !currentResponse.contains(x)).collect(Collectors.toList()));
		
		levelsRemoved.removeIf(x -> {
			GDHttpClient client = new GDHttpClient();
			GDLevel level = null;
			try {
				level = client.fetch(new GDLevelHttpRequest(x.getId()));
			} catch (GDAPIException e) {
				return true;
			}
			return level != null && (level.getStars() > 0 || level.hasCoinsVerified());
		});
		
		if (!levelsAdded.isEmpty())
			eventList.add(new GDDispatchableEvent(CommonEvents.AWARDED_LEVEL_ADDED.toString(), levelsAdded));
		if (!levelsRemoved.isEmpty())
			eventList.add(new GDDispatchableEvent(CommonEvents.AWARDED_LEVEL_DELETED.toString(), levelsRemoved));
		
		return eventList;
	}

}
