package com.github.alex1304.jdashevents;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;

import com.github.alex1304.jdash.client.AuthenticatedGDClient;
import com.github.alex1304.jdash.client.GDClientBuilder;
import com.github.alex1304.jdash.client.GDClientBuilder.Credentials;
import com.github.alex1304.jdash.exception.GDLoginFailedException;
import com.github.alex1304.jdashevents.event.AwardedLevelAddedEvent;
import com.github.alex1304.jdashevents.event.GDEvent;
import com.github.alex1304.jdashevents.scanner.AwardedSectionScanner;
import com.github.alex1304.jdashevents.scanner.DailyLevelScanner;
import com.github.alex1304.jdashevents.scanner.GDEventScanner;
import com.github.alex1304.jdashevents.scanner.WeeklyDemonScanner;

import reactor.core.publisher.Mono;

public class TestMain {

	public static void main(String[] args) throws GDLoginFailedException {
		if (args.length < 2) {
			System.err.println("Please provide GD account login details in arguments");
			return;
		}
		// Build an authenticated GD client
		AuthenticatedGDClient client = GDClientBuilder.create()
				.withHost("gdps.alex1304.com/database")
				.buildAuthenticated(new Credentials(args[0], args[1]))
				.block();
		// Create the event dispatcher
		GDEventDispatcher dispatcher = new GDEventDispatcher();
		// Subscribe to any events you want!
		dispatcher.on(GDEvent.class)
				.map(event -> "GD event fired! " + event)
				.subscribe(System.out::println);
		// In order to dispatch new events, you can either do it manually...
		client.getLevelById(32)
				.map(AwardedLevelAddedEvent::new)
				.subscribe(dispatcher::dispatch, null, null, s -> s.request(1));
		// ...or automatically via a scanner loop!
		// Scanners are capable of refreshing a certain resource in the game at regular intervals
		// in order to produce new events.
		Collection<GDEventScanner> scanners = new ArrayList<>();
		scanners.add(new AwardedSectionScanner());
		scanners.add(new DailyLevelScanner());
		scanners.add(new WeeklyDemonScanner());
		// Create the scanner loop and start scanning for events
		GDEventScannerLoop scannerLoop = new GDEventScannerLoop(client, dispatcher, scanners, Duration.ofSeconds(5));
		scannerLoop.start();
		
		Mono.never().block();
	}

}
