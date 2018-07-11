package com.github.alex1304.jdashevents.scanner;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.alex1304.jdash.api.GDHttpClient;
import com.github.alex1304.jdash.component.GDTimelyLevel;
import com.github.alex1304.jdashevents.common.CommonEvents;
import com.github.alex1304.jdashevents.customcomponents.GDUpdatedComponent;
import com.github.alex1304.jdashevents.manager.GDDispatchableEvent;

/**
 * Test case for TimelyLevelEventScanner
 *
 * @author Alex1304
 */
public class TimelyLevelEventScannerTest {
	
	private static GDHttpClient client;
	private TimelyLevelEventScanner scanner;
	private GDTimelyLevel a, b;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = new GDHttpClient();
	}

	@Before
	public void setUp() throws Exception {
		this.scanner = new DailyLevelEventScanner(client);
		this.a = new GDTimelyLevel();
		this.b = new GDTimelyLevel();
		
		a.setTimelyNumber(100);
		b.setTimelyNumber(200);
	}

	@Test
	public void test_noevent_compareAndListEvents() {
		assertEquals(new ArrayList<>(), scanner.compareAndListEvents(a, a));
		assertEquals(new ArrayList<>(), scanner.compareAndListEvents(b, b));
	}

	@Test
	public void test_changedEvent_compareAndListEvents() {
		final List<GDDispatchableEvent> expected = new ArrayList<>();
		expected.add(new GDDispatchableEvent(CommonEvents.DAILY_LEVEL_CHANGED, new GDUpdatedComponent<>(a, b)));
		
		assertEquals(expected, scanner.compareAndListEvents(a, b));
	}

}
