package com.github.alex1304.jdashevents;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;

import com.github.alex1304.jdash.client.AuthenticatedGDClient;
import com.github.alex1304.jdashevents.event.GDEvent;

import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

public final class GDEventDispatcher {
	
	private final DirectProcessor<GDEvent> processor;
	private final Scheduler scheduler;
	private final GDEventScannerLoop scannerLoop;

	public GDEventDispatcher(AuthenticatedGDClient client, Scheduler scheduler, Duration loopInterval) {
		this.processor = DirectProcessor.create();
		this.scheduler = Objects.requireNonNull(scheduler);
		this.scannerLoop = new GDEventScannerLoop(client, this, new ArrayList<>(), loopInterval);
	}
	
	/**
	 * Returns a Flux that can be subscribed to in order to process dispatched events.
	 * 
	 * @param eventClass the class of the event to listen to
	 * @return a Flux emitting the received events for the given type
	 */
	public <E extends GDEvent> Flux<E> on(Class<E> eventClass) {
		Objects.requireNonNull(eventClass);
		return processor.publishOn(scheduler).ofType(eventClass);
	}
	
	/**
	 * Dispatches a new event.
	 * 
	 * @param event the event to dispatch
	 */
	public void dispatch(GDEvent event) {
		Objects.requireNonNull(event);
		processor.sink().next(event);
	}
	
	/**
	 * Gets the scanner loop used by this dispatcher to automatically dispatch some events.
	 * 
	 * @return the scanner loop
	 */
	public GDEventScannerLoop getScannerLoop() {
		return scannerLoop;
	}
}
