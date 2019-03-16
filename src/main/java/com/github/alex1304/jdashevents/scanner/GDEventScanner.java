package com.github.alex1304.jdashevents.scanner;

import com.github.alex1304.jdash.client.AuthenticatedGDClient;
import com.github.alex1304.jdashevents.event.GDEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Scans for new GD events to emit. A scan consists of repeating a specific
 * request using a GD client and create events according to changes observed in
 * the received response between two consecutive requests.
 */
public interface GDEventScanner {
	/**
	 * Gets which request to perform when scanning.
	 * 
	 * @param client the client that should be used to perform the request
	 * @return the result of the request
	 */
	Mono<?> makeRequest(AuthenticatedGDClient client);
	
	/**
	 * Compares two responses and returns a List containing the events deducted from
	 * the comparison.
	 * 
	 * @param previousResponse the response of the previous request
	 * @param newResponse      the response of the new request
	 * @return the Flux of events that were deducted from the comparison of the two
	 *         responses
	 */
	Flux<GDEvent> scan(Object previousResponse, Object newResponse);
}
