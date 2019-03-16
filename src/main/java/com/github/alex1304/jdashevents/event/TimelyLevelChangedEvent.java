package com.github.alex1304.jdashevents.event;

import com.github.alex1304.jdash.entity.GDTimelyLevel;

public class TimelyLevelChangedEvent implements GDEvent {
	
	private final GDTimelyLevel level;

	public TimelyLevelChangedEvent(GDTimelyLevel level) {
		this.level = level;
	}

	public GDTimelyLevel getLevel() {
		return level;
	}

	@Override
	public String toString() {
		return "TimelyLevelChangedEvent [level=" + level + "]";
	}
}
