package com.github.alex1304.jdashevents;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import com.github.alex1304.jdashevents.event.GDEvent;

import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public final class GDEventDispatcher {

	private final EmitterProcessor<GDEvent> processor;
	private final Scheduler scheduler;

	/**
	 * Creates a new GDEventDispatcher that publishes events on the
	 * {@link Schedulers#boundedElastic()} scheduler.
	 */
	public GDEventDispatcher() {
		this(Schedulers.boundedElastic());
	}

	/**
	 * Creates a new GDEventDispatcher with the specified scheduler
	 * 
	 * @param scheduler the scheduler that should be used to publish events
	 */
	public GDEventDispatcher(Scheduler scheduler) {
		this.processor = EmitterProcessor.create(false);
		this.scheduler = requireNonNull(scheduler, "scheduler cannot be null");
	}

	/**
	 * Returns a Flux that can be subscribed to in order to process dispatched
	 * events.
	 * 
	 * @param <E> the type of GD event
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
		processor.onNext(event);
	}
}
