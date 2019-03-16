package com.github.alex1304.jdashevents.scanner;

import com.github.alex1304.jdash.client.AuthenticatedGDClient;
import com.github.alex1304.jdash.entity.GDTimelyLevel;

import reactor.core.publisher.Mono;

public class WeeklyDemonScanner extends TimelyLevelScanner {

	@Override
	Mono<GDTimelyLevel> makeRequest0(AuthenticatedGDClient client) {
		return client.getDailyLevel();
	}

}
