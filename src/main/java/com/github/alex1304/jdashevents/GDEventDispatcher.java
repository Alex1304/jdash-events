package com.github.alex1304.jdashevents;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.atomic.AtomicReference;

import org.reactivestreams.Subscription;

import com.github.alex1304.jdashevents.event.GDEvent;

import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.Logger;
import reactor.util.Loggers;

public final class GDEventDispatcher {
	
	private static final Logger LOGGER = Loggers.getLogger(GDEventDispatcher.class);

	private final FluxProcessor<GDEvent, GDEvent> processor;
	private final FluxSink<GDEvent> sink;
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
		this(EmitterProcessor.create(false), FluxSink.OverflowStrategy.BUFFER, scheduler);
	}

	/**
	 * Create a new GDEventDispatcher with customized event processor, overflow
	 * stategy and scheduler.
	 * 
	 * @param processor        the processor that will process the events
	 * @param overflowStrategy the strategy to apply to handle overflow
	 * @param scheduler        the scheduler that shoudl be used to publish events
	 */
	public GDEventDispatcher(FluxProcessor<GDEvent, GDEvent> processor, FluxSink.OverflowStrategy overflowStrategy,
			Scheduler scheduler) {
		this.processor = requireNonNull(processor, "processor cannot be null");
		this.sink = processor.sink(requireNonNull(overflowStrategy, "overflowStrategy cannot be null"));
		this.scheduler = requireNonNull(scheduler, "scheduler cannot be null");
		LOGGER.debug("Event dispatcher created ({})", Integer.toHexString(hashCode()));
	}

	/**
	 * Subscribes to this event dispatcher. Dispatched events can be processed using
	 * the returned Flux.
	 * 
	 * @param <E>        the type of GD event
	 * @param eventClass the class of the event to listen to
	 * @return a Flux emitting the received events for the given type
	 */
	public <E extends GDEvent> Flux<E> on(Class<E> eventClass) {
		requireNonNull(eventClass);
		AtomicReference<Subscription> sub = new AtomicReference<>();
		return processor.publishOn(scheduler)
				.ofType(eventClass)
				.doOnSubscribe(s -> {
					sub.set(s);
					LOGGER.debug("Subscription to {} created ({})", eventClass.getSimpleName(), Integer.toHexString(s.hashCode()));
				})
				.doFinally(signal -> {
					Subscription s = sub.get();
					LOGGER.debug("Subscription to {} ({}) disposed by {}", eventClass.getSimpleName(), Integer.toHexString(s.hashCode()), signal);
				});
	}

	/**
	 * Dispatches a new event.
	 * 
	 * @param event the event to dispatch
	 */
	public void dispatch(GDEvent event) {
		requireNonNull(event);
		LOGGER.debug("Dispatching event {}", LOGGER.isTraceEnabled() ? event.toString() : event.getClass().getSimpleName());
		sink.next(event);
	}
	
	/**
	 * Terminates this event dispatcher. Any call to {@link #dispatch(GDEvent)} will
	 * have no effect, and all subscribers currently subscribed to this dispatcher
	 * via {@link #on(Class)} will receive completion signal.
	 */
	public void shutdown() {
		sink.complete();
		LOGGER.debug("Event dispatcher shut down ({})", Integer.toHexString(hashCode()));
	}
}
