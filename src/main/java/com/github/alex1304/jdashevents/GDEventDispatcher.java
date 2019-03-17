package com.github.alex1304.jdashevents;

import java.util.Objects;

import com.github.alex1304.jdashevents.event.GDEvent;

import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public final class GDEventDispatcher {
	
	private final DirectProcessor<GDEvent> processor;

	public GDEventDispatcher() {
		this.processor = DirectProcessor.create();
	}
	
	/**
	 * Returns a Flux that can be subscribed to in order to process dispatched events.
	 * 
	 * @param eventClass the class of the event to listen to
	 * @return a Flux emitting the received events for the given type
	 */
	public <E extends GDEvent> Flux<E> on(Class<E> eventClass) {
		Objects.requireNonNull(eventClass);
		return processor.publishOn(Schedulers.elastic()).ofType(eventClass);
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
}
