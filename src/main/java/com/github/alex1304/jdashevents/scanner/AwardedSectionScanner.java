package com.github.alex1304.jdashevents.scanner;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.alex1304.jdash.client.AuthenticatedGDClient;
import com.github.alex1304.jdash.entity.GDLevel;
import com.github.alex1304.jdash.exception.MissingAccessException;
import com.github.alex1304.jdash.util.GDPaginator;
import com.github.alex1304.jdash.util.LevelSearchFilters;
import com.github.alex1304.jdashevents.event.AwardedLevelAddedEvent;
import com.github.alex1304.jdashevents.event.AwardedLevelRemovedEvent;
import com.github.alex1304.jdashevents.event.AwardedLevelUpdatedEvent;
import com.github.alex1304.jdashevents.event.GDEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

public class AwardedSectionScanner implements TypeSafeGDEventScanner<GDPaginator<GDLevel>> {

	@Override
	public Mono<GDPaginator<GDLevel>> typeSafeMakeRequest(AuthenticatedGDClient client) {
		return client.browseAwardedLevels(LevelSearchFilters.create(), 0);
	}

	@Override
	public Flux<GDEvent> typeSafeScan(GDPaginator<GDLevel> previousResponse, GDPaginator<GDLevel> newResponse) {
		HashSet<GDLevel> previousLevels = new HashSet<>(previousResponse.asList());
		HashSet<GDLevel> newLevels = new HashSet<>(newResponse.asList());
		HashSet<GDLevel> allLevels = new HashSet<>();
		allLevels.addAll(newLevels);
		allLevels.addAll(previousLevels);
		
		Set<GDLevel> possiblyAdded = allLevels.stream()
				.filter(l -> !previousLevels.contains(l))
				.collect(Collectors.toSet());
		Set<GDLevel> possiblyRemoved = allLevels.stream()
				.filter(l -> !newLevels.contains(l))
				.collect(Collectors.toSet());
		Set<GDLevel> possiblyUpdated = allLevels.stream()
				.filter(l -> previousLevels.contains(l) && newLevels.contains(l))
				.collect(Collectors.toSet());
		
		Flux<GDEvent> removedFlux = Flux.fromIterable(possiblyRemoved)
				.filterWhen(level -> level.refresh()
						.map(refreshedLevel -> refreshedLevel.getStars() == 0 && !refreshedLevel.hasCoinsVerified())
						.onErrorReturn(MissingAccessException.class, true)
						.onErrorReturn(false))
				.map(AwardedLevelRemovedEvent::new);
		Flux<GDEvent> addedFlux = Flux.fromIterable(possiblyAdded)
				.filterWhen(__ -> removedFlux.hasElements().map(b -> !b))
				.map(AwardedLevelAddedEvent::new);
		Flux<GDEvent> updatedFlux = Flux.fromIterable(possiblyUpdated)
				.map(level -> Tuples.of(previousLevels.stream().filter(level::equals).findAny().get(), newLevels.stream().filter(level::equals).findAny().get()))
				.filter(tuple -> tuple.getT1().getStars() != tuple.getT2().getStars()
						|| tuple.getT1().getDifficulty() != tuple.getT2().getDifficulty()
						|| tuple.getT1().getDemonDifficulty() != tuple.getT2().getDemonDifficulty()
						|| tuple.getT1().getFeaturedScore() != tuple.getT2().getFeaturedScore()
						|| tuple.getT1().isEpic() != tuple.getT2().isEpic()
						|| tuple.getT1().isAuto() != tuple.getT2().isAuto()
						|| tuple.getT1().isDemon() != tuple.getT2().isDemon())
				.map(tuple -> new AwardedLevelUpdatedEvent(tuple.getT1(), tuple.getT2()));
				
		return Flux.concat(addedFlux, removedFlux, updatedFlux);
	}
}
