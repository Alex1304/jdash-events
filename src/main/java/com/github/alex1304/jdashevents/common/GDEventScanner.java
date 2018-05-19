package com.github.alex1304.jdashevents.common;

import com.github.alex1304.jdash.api.GDHttpClient;
import com.github.alex1304.jdash.api.GDHttpRequest;
import com.github.alex1304.jdash.component.GDComponent;
import com.github.alex1304.jdash.exceptions.GDAPIException;
import com.github.alex1304.jdashevents.manager.GDEventManager;

/**
 * Looks up for data onto Geometry Dash servers in order to determine whether or
 * not an event should be triggered. 
 *
 * @author Alex1304
 */
public abstract class GDEventScanner<T extends GDComponent> {
	
	private GDHttpClient client;
	protected GDHttpRequest<T> request;
	private T previousResponse;
	
	/**
	 * @param client
	 *            - the HTTP client to use for processing the requests
	 * @param request
	 *            - The request to make to server
	 * @param event
	 *            - The event to dispatch
	 */
	public GDEventScanner(GDHttpClient client, GDHttpRequest<T> request) {
		this.client = client;
		this.request = request;
		this.previousResponse = null;
	}
	
	/**
	 * Performs the scan. The request to GD servers is executed, and then will
	 * call the method
	 * {@link GDEventScanner#eventToDispatch(GDComponent, GDComponent)} and
	 * immediately dispatches the returned event hen applicable.
	 * 
	 * @throws GDAPIException
	 *             if a problem occured while sending the request to GD servers
	 */
	public void scan() throws GDAPIException {
		T response = client.fetch(request);

		String event = this.eventToDispatch(previousResponse, response);

		if (event != null)
			GDEventManager.getInstance().dispatch(event, response);

		this.previousResponse = response;
	}
	
	/**
	 * Compares the previous response of the request and the current one in
	 * order to determine the event to dispach. There are two things you need to
	 * know when implementing this method:
	 * <ul>
	 * <li><code>previousResponse</code> will be null if the scan() method has
	 * never been called before</li>
	 * <li>If no event is supposed to dispatch after comparing the two
	 * responses, this method must return null</li>
	 * </ul>
	 * 
	 * @param previousResponse
	 *            - the response resulting of the previous call of scan(). This
	 *            is null if it's the first call of scan()
	 * @param currentResponse
	 *            - the response returned by the current call of scan()
	 * @return String - the event name to dispatch, or null if nothing should be
	 *         dispatched
	 */
	public abstract String eventToDispatch(T previousResponse, T currentResponse);
}
 