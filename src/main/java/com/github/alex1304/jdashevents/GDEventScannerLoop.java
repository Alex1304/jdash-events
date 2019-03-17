package com.github.alex1304.jdashevents;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.github.alex1304.jdash.client.AuthenticatedGDClient;
import com.github.alex1304.jdashevents.scanner.GDEventScanner;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class GDEventScannerLoop {
	
	private final GDEventDispatcher dispatcher;
	private final Flux<Tuple2<GDEventScanner, Object>> loop;
	private Optional<Disposable> disposable;
	
	public GDEventScannerLoop(AuthenticatedGDClient client, GDEventDispatcher dispatcher, Collection<? extends GDEventScanner> scanners, Duration interval) {
		this.dispatcher = Objects.requireNonNull(dispatcher);
		this.disposable = Optional.empty();
		this.loop = Flux.interval(interval)
				.flatMapIterable(__ -> scanners)
				.flatMap(scanner -> scanner.makeRequest(client)
						.onErrorResume(e -> Mono.empty())
						.map(response -> Tuples.of((GDEventScanner) scanner, (Object) response)))
				.log("jdash-events-scanner-loop", Level.FINE, SignalType.values());
	}
	
	public void start() {
		if (disposable.isPresent()) {
			return;
		}
		LoopSubscriber subscriber = new LoopSubscriber();
		disposable = Optional.of(subscriber);
		loop.subscribe(subscriber);
	}
	
	public void stop() {
		disposable.ifPresent(Disposable::dispose);
		disposable = Optional.empty();
	}
	
	private final class LoopSubscriber implements Subscriber<Tuple2<GDEventScanner, Object>>, Disposable {
		
		private final Map<GDEventScanner, Object> previousResponseForEachScanner;
		private Optional<Subscription> sub;
		
		LoopSubscriber() {
			this.previousResponseForEachScanner = new ConcurrentHashMap<>();
			this.sub = Optional.empty();
		}
		
		@Override
		public void dispose() {
			sub.ifPresent(Subscription::cancel);
			sub = Optional.empty();
			previousResponseForEachScanner.clear();
		}
		
		@Override
		public void onSubscribe(Subscription s) {
			s.request(Long.MAX_VALUE);
			this.sub = Optional.of(s);
		}
		
		@Override
		public void onNext(Tuple2<GDEventScanner, Object> tuple) {
			previousResponseForEachScanner.compute(tuple.getT1(), (__, previousResponse) -> {
				Object newResponse = tuple.getT2();
				if (previousResponse != null) {
					tuple.getT1().scan(previousResponse, newResponse).subscribe(dispatcher::dispatch);
				}
				return tuple.getT2();
			});
		}
		
		@Override
		public void onError(Throwable t) {
		}
		
		@Override
		public void onComplete() {
		}
	}
}
