package com.github.alex1304.jdashevents;

import java.util.function.Consumer;

import com.github.alex1304.jdash.component.GDComponent;

/**
 * A GD event is an object that has a name and an action on a GD component
 * 
 * @param <T> - the type of the GD component affected by the event.
 * 
 * @author Alex1304
 */
public class GDEvent<T extends GDComponent> {
	
	private String name;
	private Consumer<T> action;
	
	/**
	 * @param name
	 *            - the name of the event
	 * @param action
	 *            - the action that should be performed when the event is
	 *            triggered
	 */
	public GDEvent(String name, Consumer<T> action) {
		this.name = name;
		this.action = action;
	}

	/**
	 * Gets the name of the event
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the action that should be performed when the event is triggered
	 * 
	 * @return Consumer
	 */
	public Consumer<T> getAction() {
		return action;
	}
	
	/**
	 * Executes the action given in this object on the component given as
	 * argument
	 * 
	 * @param component
	 *            - the component on which the action is applied
	 * @throws ClassCastException
	 *             if the component object given isn't compatible with the type
	 *             prameter of this object
	 */
	@SuppressWarnings("unchecked")
	public void executeAction(GDComponent component) {
		this.action.accept((T) component);
	}
	

}
