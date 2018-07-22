package com.github.alex1304.jdashevents.scanner;

import java.util.List;

import com.github.alex1304.jdash.api.GDHttpClient;
import com.github.alex1304.jdash.api.GDHttpRequest;
import com.github.alex1304.jdash.component.GDComponent;
import com.github.alex1304.jdash.exceptions.GDAPIException;
import com.github.alex1304.jdashevents.manager.GDDispatchableEvent;
import com.github.alex1304.jdashevents.manager.GDEventManager;

/**
 * Looks up for data onto Geometry Dash servers in order to determine whether or
 * not an event should be triggered.
 * 
 * @param <T>
 *            - The type of component to scan
 *
 * @author Alex1304
 */
public abstract class GDEventScanner<T extends GDComponent> {
	
	protected GDHttpClient client;
	protected GDHttpRequest<T> request;
	private T previousResponse;
	
	/**
	 * @param client
	 *            - the HTTP client to use for processing the requests
	 * @param request
	 *            - The request to make to server
	 */
	public GDEventScanner(GDHttpClient client, GDHttpRequest<T> request) {
		this.client = client;
		this.request = request;
		this.previousResponse = null;
	}
	
	/**
	 * Performs the scan. The request to GD servers is executed, and then will
	 * call the method
	 * {@link GDEventScanner#compareAndListEvents(GDComponent, GDComponent)} and
	 * immediately dispatches the returned event when applicable.
	 * 
	 * @throws GDAPIException
	 *             if a problem occured while sending the request to GD servers
	 */
	public synchronized void scan() throws GDAPIException {
		T response = client.fetch(request);

		List<GDDispatchableEvent> eventList = this.compareAndListEvents(previousResponse, response);

		for (GDDispatchableEvent event : eventList)
			GDEventManager.getInstance().dispatch(event);

		this.previousResponse = response;
	}
	
	/**
	 * Compares the previous response of the request with the current one in
	 * order to determine which events to emit. There are two things you need to
	 * know when implementing this method:
	 * <ul>
	 * <li><code>previousResponse</code> will be null if it's the first
	 * scan</li>
	 * <li>If no event is supposed to dispatch after comparing the two
	 * responses, this method must return an empty list</li>
	 * </ul>
	 * 
	 * @param previousResponse
	 *            - the response resulting of the previous request. This is null
	 *            if it's the first request
	 * @param currentResponse
	 *            - the response returned by the current request
	 * @return List - the list of events to be dispatched later, resulting of
	 *         the comparison of the two responses
	 */
	public abstract List<GDDispatchableEvent> compareAndListEvents(T previousResponse, T currentResponse);
}
 