package com.github.alex1304.jdashevents.scanner;

import com.github.alex1304.jdash.client.AuthenticatedGDClient;
import com.github.alex1304.jdashevents.event.GDEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@link GDEventScanner} that utilizes parametrized types.
 * 
 * @param <R> the type of objects to scan
 */
public interface TypeSafeGDEventScanner<R> extends GDEventScanner {

	@Override
	default Mono<?> makeRequest(AuthenticatedGDClient client) {
		return typeSafeMakeRequest(client);
	}

	@Override
	@SuppressWarnings("unchecked")
	default Flux<GDEvent> scan(Object previousResponse, Object newResponse) {
		return typeSafeScan((R) previousResponse, (R) newResponse);
	}
	
	Mono<R> typeSafeMakeRequest(AuthenticatedGDClient client);
	Flux<GDEvent> typeSafeScan(R previousResponse, R newResponse);
}
