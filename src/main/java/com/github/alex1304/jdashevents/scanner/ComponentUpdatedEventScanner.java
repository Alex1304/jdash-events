package com.github.alex1304.jdashevents.scanner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.github.alex1304.jdash.api.GDHttpClient;
import com.github.alex1304.jdash.api.GDHttpRequest;
import com.github.alex1304.jdash.component.GDComponent;
import com.github.alex1304.jdashevents.customcomponents.GDUpdatedComponent;
import com.github.alex1304.jdashevents.manager.GDDispatchableEvent;

/**
 * Scans a particular component and creates a dispatchable event if it's updated
 *
 * @author Alex1304
 */
public abstract class ComponentUpdatedEventScanner<T extends GDComponent> extends GDEventScanner<T> implements Comparator<T> {

	private String changedEvent;

	/**
	 * @param client
	 *            - the HTTP client to use for processing the requests
	 * @param request
	 *            - The request to make to server
	 * @param changedEvent
	 *            - the name of the event telling the change
	 */
	public ComponentUpdatedEventScanner(GDHttpClient client, GDHttpRequest<T> request, String changedEvent) {
		super(client, request);
		this.changedEvent = changedEvent;
	}

	@Override
	public List<GDDispatchableEvent> compareAndListEvents(T previousResponse, T currentResponse) {
		List<GDDispatchableEvent> eventList = new ArrayList<>();
		
		if (previousResponse != null && currentResponse != null &&
				this.compare(previousResponse, currentResponse) != 0)
			eventList.add(new GDDispatchableEvent(changedEvent, new GDUpdatedComponent<>(previousResponse, currentResponse)));
		
		return eventList;
	}

	/**
	 * Gets the name of the event telling the change
	 *
	 * @return String
	 */
	public String getChangedEvent() {
		return changedEvent;
	}

}
