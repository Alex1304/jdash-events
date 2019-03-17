package com.github.alex1304.jdashevents.event;

import java.util.Objects;

import com.github.alex1304.jdash.entity.GDLevel;

public class AwardedLevelAddedEvent implements GDEvent {
	
	private final GDLevel addedLevel;

	public AwardedLevelAddedEvent(GDLevel addedLevel) {
		this.addedLevel = Objects.requireNonNull(addedLevel);
	}

	public GDLevel getAddedLevel() {
		return addedLevel;
	}

	@Override
	public String toString() {
		return "AwardedLevelAddedEvent [addedLevel=" + addedLevel + "]";
	}
}
