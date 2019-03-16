package com.github.alex1304.jdashevents.scanner;

import com.github.alex1304.jdash.entity.GDTimelyLevel;
import com.github.alex1304.jdashevents.event.GDEvent;
import com.github.alex1304.jdashevents.event.TimelyLevelChangedEvent;

import reactor.core.publisher.Flux;

abstract class TimelyLevelScanner extends AbstractGDEventScanner<GDTimelyLevel> {

	@Override
	final Flux<GDEvent> scan0(GDTimelyLevel previousResponse, GDTimelyLevel newResponse) {
		return previousResponse.equals(newResponse) ? Flux.empty() : Flux.just(new TimelyLevelChangedEvent(newResponse));
	}

}
