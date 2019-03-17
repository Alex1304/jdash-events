package com.github.alex1304.jdashevents.event;

import java.util.Objects;

import com.github.alex1304.jdash.entity.GDLevel;

public class AwardedLevelUpdatedEvent implements GDEvent {
	
	private final GDLevel oldLevel;
	private final GDLevel newLevel;
	
	public AwardedLevelUpdatedEvent(GDLevel oldLevel, GDLevel newLevel) {
		this.oldLevel = Objects.requireNonNull(oldLevel);
		this.newLevel = Objects.requireNonNull(newLevel);
	}
	
	public GDLevel getOldLevel() {
		return oldLevel;
	}
	
	public GDLevel getNewLevel() {
		return newLevel;
	}

	@Override
	public String toString() {
		return "AwardedLevelUpdatedEvent [oldLevel=" + oldLevel + ", newLevel=" + newLevel + "]";
	}
}
