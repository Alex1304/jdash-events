package com.github.alex1304.jdashevents.manager;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.alex1304.jdash.component.GDLevel;
import com.github.alex1304.jdashevents.GDEvent;

/**
 * Test cases for the GDEvent manager
 * 
 * @author Alex1304
 */
public class GDEventManagerTest {
	
	private GDEventManager em;
	private GDEvent<GDLevel> testEvent;
	private boolean testEventFired;
	private GDLevel testLevel = new GDLevel(0, "", 0, "", null, null, 0, 0, false, 0, 0, null, 0, 0, 0, false, 0, 0, 0, false, false, 0, 0, 0, null, null);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		this.em = GDEventManager.getInstance();
		this.testEvent = new GDEvent<GDLevel>("TEST_EVENT", level -> testEventFired = true);
		this.testEventFired = false;
	}

	@Test
	public void test_registerEvent() {
		em.registerEvent(testEvent);
		assertEquals(1, em.getEventStream().count());
	}
	
	@Test
	public void test_matchingName_dispatch() {
		em.registerEvent(testEvent);
		em.dispatch("TEST_EVENT", testLevel);
		assertTrue(testEventFired);
	}
	
	@Test
	public void test_nonMatchingName_dispatch() {
		em.registerEvent(testEvent);
		em.dispatch("RANDOM_NAME_THAT_DONT_MATCH_LOL", testLevel);
		assertFalse(testEventFired);
	}

}
