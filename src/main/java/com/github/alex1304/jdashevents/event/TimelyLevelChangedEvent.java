package com.github.alex1304.jdashevents.event;

import com.github.alex1304.jdash.entity.GDTimelyLevel;

public class TimelyLevelChangedEvent implements GDEvent {
	
	private final GDTimelyLevel timelyLevel;

	public TimelyLevelChangedEvent(GDTimelyLevel timelyLevel) {
		this.timelyLevel = timelyLevel;
	}

	public GDTimelyLevel getTimelyLevel() {
		return timelyLevel;
	}

	@Override
	public String toString() {
		return "TimelyLevelChangedEvent [level=" + timelyLevel + "]";
	}
}
