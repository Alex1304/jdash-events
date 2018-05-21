package com.github.alex1304.jdashevents.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.github.alex1304.jdash.component.GDComponent;
import com.github.alex1304.jdashevents.GDEvent;

/**
 * An event manager allows to register/remove a GD event,
 * and trigger them.
 * 
 * @author Alex1304
 */
public class GDEventManager implements Iterable<GDEvent<?>> {

	private List<GDEvent<?>> registeredEvents;
	private static GDEventManager instance = null;
	
	private GDEventManager() {
		this.registeredEvents = new ArrayList<>();
	}
	
	/**
	 * Returns a unique instance of GDEventManager
	 *
	 * @return GDEventManager
	 */
	public static GDEventManager getInstance() {
		if (instance == null)
			instance = new GDEventManager();
		
		return instance;
	}
	
	/**
	 * Registers a new event
	 * 
	 * @param event
	 *            - the event to register
	 */
	public void registerEvent(GDEvent<?> event) {
		this.registeredEvents.add(event);
	}
	
	/**
	 * Executes the action of all events with the given name on the given
	 * component.
	 * 
	 * @param eventName
	 *            - the name of the event(s) to dispatch
	 * @param component
	 *            - the component to be processed
	 */
	public void dispatch(String eventName, GDComponent component) {
		for (GDEvent<?> event : registeredEvents) {
			if (event.getName().equals(eventName)) {
				try {
					event.executeAction(component); 
				} catch (ClassCastException e) {
				}
			}
		}
	}
	
	/**
	 * Executes the action of all events having the same name as the given
	 * dispatchable event object and applies on the associated component
	 * 
	 * @param event
	 *            - the dispatchable event to dispatch
	 */
	public void dispatch(GDDispatchableEvent event) {
		this.dispatch(event.getEvent(), event.getComponent());
	}
	
	@Override
	public Iterator<GDEvent<?>> iterator() {
		return registeredEvents.iterator();
	}
	
	/**
	 * Gets the stream of the registered events list
	 * 
	 * @return Stream
	 */
	public Stream<GDEvent<?>> getEventStream() {
		return registeredEvents.stream();
	}
}
