package com.github.alex1304.jdashevents.scanner;

import com.github.alex1304.jdash.entity.GDTimelyLevel;
import com.github.alex1304.jdashevents.event.GDEvent;
import com.github.alex1304.jdashevents.event.TimelyLevelChangedEvent;

import reactor.core.publisher.Flux;

abstract class TimelyLevelScanner implements TypeSafeGDEventScanner<GDTimelyLevel> {

	@Override
	public final Flux<GDEvent> typeSafeScan(GDTimelyLevel previousResponse, GDTimelyLevel newResponse) {
		return previousResponse.equals(newResponse) ? Flux.empty() : Flux.just(new TimelyLevelChangedEvent(newResponse));
	}

}
