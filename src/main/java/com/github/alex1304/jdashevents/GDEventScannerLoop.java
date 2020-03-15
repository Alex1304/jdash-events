package com.github.alex1304.jdashevents;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
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

	private static final AtomicReferenceFieldUpdater<GDEventScannerLoop, Disposable> DISPOSABLE_REF =
			AtomicReferenceFieldUpdater.newUpdater(GDEventScannerLoop.class, Disposable.class, "disposable");
	private volatile Disposable disposable;
	
	public GDEventScannerLoop(AuthenticatedGDClient client, GDEventDispatcher dispatcher, Collection<? extends GDEventScanner> scanners, Duration interval) {
		this.dispatcher = requireNonNull(dispatcher);
		this.loop = Flux.interval(interval)
				.flatMapIterable(__ -> scanners)
				.flatMap(scanner -> scanner.makeRequest(client)
						.onErrorResume(e -> Mono.empty())
						.map(response -> Tuples.of((GDEventScanner) scanner, (Object) response)))
				.log(GDEventScannerLoop.class.getName(), Level.FINE, SignalType.values());
	}
	
	public void start() {
		LoopSubscriber subscriber = new LoopSubscriber(dispatcher);
		if (DISPOSABLE_REF.compareAndSet(this, null, subscriber)) {
			loop.subscribe(subscriber);
		}
	}
	
	public void stop() {
		Disposable d = disposable;
		if (DISPOSABLE_REF.compareAndSet(this, d, null)) {
			d.dispose();
		}
	}
	
	private static final class LoopSubscriber implements Subscriber<Tuple2<GDEventScanner, Object>>, Disposable {
		
		private final GDEventDispatcher dispatcher;
		private final ConcurrentHashMap<GDEventScanner, Object> previousResponseForEachScanner;

		private static final AtomicReferenceFieldUpdater<LoopSubscriber, Subscription> SUB_REF =
				AtomicReferenceFieldUpdater.newUpdater(LoopSubscriber.class, Subscription.class, "sub");
		private volatile Subscription sub;
		
		LoopSubscriber(GDEventDispatcher dispatcher) {
			this.dispatcher = dispatcher;
			this.previousResponseForEachScanner = new ConcurrentHashMap<>();
		}
		
		@Override
		public void dispose() {
			Subscription s = sub;
			if (SUB_REF.compareAndSet(this, s, null)) {
				s.cancel();
				previousResponseForEachScanner.clear();
			}
		}
		
		@Override
		public void onSubscribe(Subscription s) {
			if (SUB_REF.compareAndSet(this, null, s)) {
				s.request(Long.MAX_VALUE);
			}
		}
		
		@Override
		public void onNext(Tuple2<GDEventScanner, Object> tuple) {
			GDEventScanner scanner = tuple.getT1();
			Object newResponse = tuple.getT2();
			previousResponseForEachScanner.merge(tuple.getT1(), newResponse, (oldV, newV) -> {
				scanner.scan(oldV, newV).subscribe(dispatcher::dispatch);
				return newV;
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
