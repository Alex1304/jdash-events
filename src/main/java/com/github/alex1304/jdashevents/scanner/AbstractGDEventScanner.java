package com.github.alex1304.jdashevents.scanner;

import com.github.alex1304.jdash.client.AuthenticatedGDClient;
import com.github.alex1304.jdashevents.event.GDEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

abstract class AbstractGDEventScanner<R> implements GDEventScanner {

	@Override
	public Mono<?> makeRequest(AuthenticatedGDClient client) {
		return makeRequest0(client);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Flux<GDEvent> scan(Object previousResponse, Object newResponse) {
		return scan0((R) previousResponse, (R) newResponse);
	}
	
	abstract Mono<R> makeRequest0(AuthenticatedGDClient client);
	abstract Flux<GDEvent> scan0(R previousResponse, R newResponse);
}
