package com.github.alex1304.jdashevents;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.github.alex1304.jdash.client.AuthenticatedGDClient;
import com.github.alex1304.jdashevents.scanner.GDEventScanner;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class GDEventScannerLoop {
	
	private final GDEventDispatcher dispatcher;
	private final Flux<Tuple2<GDEventScanner, ?>> loop;
	private Optional<Disposable> disposable;
	
	GDEventScannerLoop(AuthenticatedGDClient client, GDEventDispatcher dispatcher, Collection<? extends GDEventScanner> scanners, Duration interval) {
		this.dispatcher = Objects.requireNonNull(dispatcher);
		this.disposable = Optional.empty();
		this.loop = Flux.interval(interval)
				.log()
				.flatMapIterable(__ -> scanners)
				.flatMap(scanner -> scanner.makeRequest(client)
						.onErrorResume(e -> Mono.empty())
						.map(response -> Tuples.of(scanner, response)));
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
	
	private final class LoopSubscriber implements Subscriber<Tuple2<GDEventScanner, ?>>, Disposable {
		
		private final Map<GDEventScanner, Object> previousResponseForEachScanner;
		private Subscription sub;
		
		LoopSubscriber() {
			this.previousResponseForEachScanner = new ConcurrentHashMap<>();
			this.sub = null;
		}
		
		@Override
		public void dispose() {
			if (sub != null) {
				throw new IllegalStateException("Attempt to cancel a non-existant subscription");
			}
			previousResponseForEachScanner.clear();
			sub.cancel();
		}
		@Override
		public void onSubscribe(Subscription s) {
			s.request(Long.MAX_VALUE);
			this.sub = s;
		}
		@Override
		public void onNext(Tuple2<GDEventScanner, ?> tuple) {
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
