package com.github.alex1304.jdashevents.scanner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.alex1304.jdash.api.GDHttpClient;
import com.github.alex1304.jdash.api.GDHttpRequest;
import com.github.alex1304.jdash.component.GDComponent;
import com.github.alex1304.jdash.component.GDComponentList;
import com.github.alex1304.jdashevents.customcomponents.GDUpdatedComponent;
import com.github.alex1304.jdashevents.manager.GDDispatchableEvent;

/**
 * Scans a list of components and emits events when a level is added, updated,
 * or deleted from the section. It also provides ability to prevent some components
 * from emitting events using predicates.
 * 
 * @param <T>
 *            - the type of component contained in the processed lists
 *
 * @author Alex1304
 */
public abstract class ComponentListUpdatedEventScanner<T extends GDComponent> extends GDEventScanner<GDComponentList<T>> implements Comparator<T> {
	
	private String addedEvent, deletedEvent, updatedEvent;
	
	/**
	 * @param client
	 *            - the HTTP client to use for processing the requests
	 * @param request
	 *            - The request to make to server
	 * @param addedEvent
	 *            - event identifier for when a component is added in the list
	 * @param deletedEvent
	 *            - event identifier for when a component is deleted from the
	 *            list
	 * @param updatedEvent
	 *            - event identifier for when a component is updated in the list
	 */
	public ComponentListUpdatedEventScanner(GDHttpClient client, GDHttpRequest<GDComponentList<T>> request, String addedEvent, String deletedEvent, String updatedEvent) {
		super(client, request);
		this.addedEvent = addedEvent;
		this.deletedEvent = deletedEvent;
		this.updatedEvent = updatedEvent;
	}

	@Override
	public List<GDDispatchableEvent> compareAndListEvents(GDComponentList<T> previousResponse, GDComponentList<T> currentResponse) {
		List<GDDispatchableEvent> eventList = new ArrayList<>();
		
		if (previousResponse == null || currentResponse == null)
			return eventList;
		
		GDComponentList<T> added = new GDComponentList<>(
				currentResponse.stream().filter(x -> !previousResponse.contains(x)).collect(Collectors.toList()));
		
		GDComponentList<T> removed = new GDComponentList<>(
				previousResponse.stream().filter(x -> !currentResponse.contains(x)).collect(Collectors.toList()));
		
		GDComponentList<GDUpdatedComponent<T>> updated = new GDComponentList<>();
		
		// Set containing components in common between previous and current responses
		Set<T> commonList = new HashSet<>();
		commonList.addAll(previousResponse);
		commonList.addAll(currentResponse);
		commonList.removeAll(added);
		commonList.removeAll(removed);
		
		// See which components have been updated
		for (T component : commonList) {
			Optional<T> occurenceInPrevious = previousResponse.stream().filter(x -> x.equals(component)).findAny();
			Optional<T> occurenceInCurrent = currentResponse.stream().filter(x -> x.equals(component)).findAny();
			
			if (occurenceInPrevious.isPresent() && occurenceInCurrent.isPresent() &&
					this.compare(occurenceInPrevious.get(), occurenceInCurrent.get()) != 0)
				updated.add(new GDUpdatedComponent<>(occurenceInPrevious.get(), occurenceInCurrent.get()));
		}
		
		added.removeIf(this.ignoreAddedComponentIf());
		removed.removeIf(this.ignoreRemovedComponentIf());
		updated.removeIf(this.ignoreUpdatedComponentIf());
		
		if (!added.isEmpty())
			eventList.add(new GDDispatchableEvent(addedEvent, added));
		if (!removed.isEmpty())
			eventList.add(new GDDispatchableEvent(deletedEvent, removed));
		if (!updated.isEmpty())
			eventList.add(new GDDispatchableEvent(updatedEvent, updated));
		
		return eventList;
	}
	
	/**
	 * Override this method if you need to ignore some added components.
	 * Ignored components won't create any event related to them
	 * 
	 * @return Predicate
	 */
	public Predicate<T> ignoreAddedComponentIf() {
		return x -> false;
	}

	/**
	 * Override this method if you need to ignore some removed components.
	 * Ignored components won't create any event related to them
	 * 
	 * @return Predicate
	 */
	public Predicate<T> ignoreRemovedComponentIf() {
		return x -> false;
	}

	/**
	 * Override this method if you need to ignore some updated components.
	 * Ignored components won't create any event related to them
	 * 
	 * @return Predicate
	 */
	public Predicate<GDUpdatedComponent<T>> ignoreUpdatedComponentIf() {
		return x -> false;
	}

	/**
	 * Gets the addedEvent
	 *
	 * @return String
	 */
	public String getAddedEvent() {
		return addedEvent;
	}

	/**
	 * Gets the deletedEvent
	 *
	 * @return String
	 */
	public String getDeletedEvent() {
		return deletedEvent;
	}

	/**
	 * Gets the updatedEvent
	 *
	 * @return String
	 */
	public String getUpdatedEvent() {
		return updatedEvent;
	}

}
