package com.github.alex1304.jdashevents.scanner;

import java.util.ArrayList;
import java.util.List;

import com.github.alex1304.jdash.client.AuthenticatedGDClient;
import com.github.alex1304.jdash.entity.GDLevel;
import com.github.alex1304.jdash.exception.MissingAccessException;
import com.github.alex1304.jdash.util.GDPaginator;
import com.github.alex1304.jdash.util.LevelSearchFilters;
import com.github.alex1304.jdashevents.event.AwardedLevelAddedGDEvent;
import com.github.alex1304.jdashevents.event.AwardedLevelRemovedGDEvent;
import com.github.alex1304.jdashevents.event.GDEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class AwardedSectionScanner extends AbstractGDEventScanner<GDPaginator<GDLevel>> {

	@Override
	Mono<GDPaginator<GDLevel>> makeRequest0(AuthenticatedGDClient client) {
		return client.browseAwardedLevels(LevelSearchFilters.create(), 0);
	}

	@Override
	Flux<GDEvent> scan0(GDPaginator<GDLevel> previousResponse, GDPaginator<GDLevel> newResponse) {
		List<GDLevel> addedLevels = new ArrayList<>(newResponse.asList());
		addedLevels.removeAll(previousResponse.asList());
		List<GDLevel> removedLevels = new ArrayList<>(previousResponse.asList());
		removedLevels.removeAll(newResponse.asList());
		System.out.println("Scanning Awarded section...");
		
		Flux<GDEvent> removedFlux = Flux.fromIterable(removedLevels)
				.filterWhen(level -> level.refresh()
						.map(refreshedLevel -> refreshedLevel.getStars() == 0 && !refreshedLevel.hasCoinsVerified())
						.onErrorReturn(MissingAccessException.class, true)
						.onErrorReturn(false))
				.map(AwardedLevelRemovedGDEvent::new);

		Flux<GDEvent> addedFlux = Flux.fromIterable(addedLevels)
				.map(AwardedLevelAddedGDEvent::new);
		
		return removedFlux.hasElements().flatMapMany(hasElements -> removedFlux.concatWith(hasElements ? Flux.empty() : addedFlux));
	}
}
