package com.github.alex1304.jdashevents.scanner;

import com.github.alex1304.jdash.client.AuthenticatedGDClient;
import com.github.alex1304.jdash.entity.GDTimelyLevel;

import reactor.core.publisher.Mono;

public class WeeklyDemonScanner extends TimelyLevelScanner {

	@Override
	public Mono<GDTimelyLevel> typeSafeMakeRequest(AuthenticatedGDClient client) {
		return client.getDailyLevel();
	}

}
