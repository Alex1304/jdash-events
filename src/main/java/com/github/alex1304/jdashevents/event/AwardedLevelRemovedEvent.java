package com.github.alex1304.jdashevents.event;

import java.util.Objects;

import com.github.alex1304.jdash.entity.GDLevel;

public class AwardedLevelRemovedEvent implements GDEvent {
	
	private final GDLevel removedLevel;

	public AwardedLevelRemovedEvent(GDLevel removedLevel) {
		this.removedLevel = Objects.requireNonNull(removedLevel);
	}

	public GDLevel getAddedLevel() {
		return removedLevel;
	}

	@Override
	public String toString() {
		return "AwardedLevelRemovedEvent [removedLevel=" + removedLevel + "]";
	}
}
