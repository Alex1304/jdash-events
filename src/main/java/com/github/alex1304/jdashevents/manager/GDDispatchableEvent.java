package com.github.alex1304.jdashevents.manager;

import com.github.alex1304.jdash.component.GDComponent;

/**
 * Associates a GD component with an event name so it's ready to dispatch.
 *
 * @author Alex1304
 */
public class GDDispatchableEvent {

	private String event;
	private GDComponent component;
	
	/**
	 * @param event
	 *            - The event name
	 * @param component
	 *            - The component concerned by the event
	 */
	public GDDispatchableEvent(String event, GDComponent component) {
		this.event = event;
		this.component = component;
	}

	/**
	 * Gets the event name
	 *
	 * @return String
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * Gets the component concerned by the event
	 *
	 * @return GDComponent
	 */
	public GDComponent getComponent() {
		return component;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((component == null) ? 0 : component.hashCode());
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof GDDispatchableEvent))
			return false;
		GDDispatchableEvent other = (GDDispatchableEvent) obj;
		if (component == null) {
			if (other.component != null)
				return false;
		} else if (!component.equals(other.component))
			return false;
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GDDispatchableEvent [event=" + event + ", component=" + component + "]";
	}
}
