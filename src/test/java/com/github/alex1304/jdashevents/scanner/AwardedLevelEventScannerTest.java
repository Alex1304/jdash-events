package com.github.alex1304.jdashevents.scanner;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.alex1304.jdash.api.GDHttpClient;
import com.github.alex1304.jdash.component.GDComponentList;
import com.github.alex1304.jdash.component.GDLevelPreview;
import com.github.alex1304.jdash.component.GDSong;
import com.github.alex1304.jdash.component.property.GDLevelDemonDifficulty;
import com.github.alex1304.jdash.component.property.GDLevelDifficulty;
import com.github.alex1304.jdash.component.property.GDLevelLength;
import com.github.alex1304.jdashevents.common.CommonEvents;
import com.github.alex1304.jdashevents.customcomponents.GDUpdatedComponent;
import com.github.alex1304.jdashevents.manager.GDDispatchableEvent;

/**
 * Test case for AwardedLevelEventScanner
 *
 * @author Alex1304
 */
public class AwardedLevelEventScannerTest {
	
	private static GDHttpClient client;
	
	private AwardedLevelEventScanner scanner;
	private GDComponentList<GDLevelPreview> a, b, c, d, e, f, g;
	
	private GDLevelPreview bloodbath, nerves, kotoruption, bloodlust, sonicwave, doramichallenge, doramichallenge2;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = new GDHttpClient();
	}

	@Before
	public void setUp() throws Exception {
		this.scanner = new AwardedLevelEventScanner(client);
		this.a = new GDComponentList<>();
		this.b = new GDComponentList<>();
		this.c = new GDComponentList<>();
		this.d = new GDComponentList<>();
		this.e = new GDComponentList<>();
		this.f = new GDComponentList<>();
		this.g = new GDComponentList<>();

		this.bloodbath = new GDLevelPreview(10565740, "Bloodbath", "Riot", GDLevelDifficulty.DEMON,
				GDLevelDemonDifficulty.EXTREME, 10, new GDSong("dimrain47", "At the speed of light"), 10330, false, 10318375, 1002472,
				GDLevelLength.XL, 0, false, true, false);
		this.nerves = new GDLevelPreview(36227266, "Nerves", "Alex1304", GDLevelDifficulty.DEMON,
				GDLevelDemonDifficulty.HARD, 10,  new GDSong("shut eye", "nerf this"), 23700, false, 33008, 4242,
				GDLevelLength.LONG, 3, true, true, false);
		this.kotoruption = new GDLevelPreview(38693063, "Kotoruption", "Alex1304", GDLevelDifficulty.DEMON,
				GDLevelDemonDifficulty.MEDIUM, 10,  new GDSong("gdsharks", "Nanamori"), 24281, true, 708497, 61886,
				GDLevelLength.LONG, 3, true, true, false);
		this.bloodlust = new GDLevelPreview(42584142, "Bloodlust", "Knobbelboy", GDLevelDifficulty.DEMON,
				GDLevelDemonDifficulty.EXTREME, 10, new GDSong("dimrain47", "At the speed of light"), 24502, true, 1329414, 147305,
				GDLevelLength.XL, 0, false, true, false);
		this.sonicwave = new GDLevelPreview(3062870, "Sonic Wave", "lSunix", GDLevelDifficulty.DEMON,
				GDLevelDemonDifficulty.EXTREME, 10, new GDSong("F-777", "sonic blaster"), -1, false, 3062870, 236209,
				GDLevelLength.XL, 0, false, true, false);
		this.doramichallenge = new GDLevelPreview(25263313, "Dorami Challenge", "Elit3gamer", GDLevelDifficulty.HARD,
				GDLevelDemonDifficulty.HARD, 0, new GDSong("ForeverBound", "Stereo Madness"), 0, false, 132240, 21094,
				GDLevelLength.TINY, 2, false, false, false);
		this.doramichallenge2 = new GDLevelPreview(25263313, "Dorami Challenge", "Elit3gamer", GDLevelDifficulty.HARDER,
				GDLevelDemonDifficulty.HARD, 0, new GDSong("djvi", "back on track"), 0, false, 132240, 21094,
				GDLevelLength.SHORT, 3, false, false, false);
		
		// Base reference
		a.add(bloodlust);
		a.add(doramichallenge);
		a.add(nerves);
		
		// Equals
		b.add(bloodlust);
		b.add(doramichallenge);
		b.add(nerves);
		
		// Added
		c.add(bloodlust);
		c.add(doramichallenge);
		c.add(nerves);
		c.add(kotoruption);
		
		// Deleted
		d.add(bloodlust);
		d.add(nerves);
		
		// Added, not deleted because off page (means that it's deleted/invisible but the level is still awarded)
		e.add(bloodlust);
		e.add(doramichallenge);
		e.add(kotoruption);
		
		// Added and deleted
		f.add(bloodlust);
		f.add(bloodbath);
		f.add(sonicwave);
		f.add(nerves);
		
		// Updated
		g.add(bloodlust);
		g.add(doramichallenge2);
		g.add(nerves);
	}

	@Test
	public void test_noevent_compareAndListEvents() {
		assertEquals(new ArrayList<>(), scanner.compareAndListEvents(a, b));
	}

	@Test
	public void test_added_compareAndListEvents() {
		final List<GDDispatchableEvent> expected = new ArrayList<>();
		expected.add(new GDDispatchableEvent(CommonEvents.AWARDED_LEVEL_ADDED,
				new GDComponentList<>(Arrays.asList(kotoruption))));
		
		assertEquals(expected, scanner.compareAndListEvents(a, c));
	}

	@Test
	public void test_deleted_compareAndListEvents() {
		final List<GDDispatchableEvent> expected = new ArrayList<>();
		expected.add(new GDDispatchableEvent(CommonEvents.AWARDED_LEVEL_DELETED,
				new GDComponentList<>(Arrays.asList(doramichallenge))));
		
		assertEquals(expected, scanner.compareAndListEvents(a, d));
	}

	@Test
	public void test_addedButNotDeletedBecauseOffPage_compareAndListEvents() {
		final List<GDDispatchableEvent> expected = new ArrayList<>();
		expected.add(new GDDispatchableEvent(CommonEvents.AWARDED_LEVEL_ADDED,
				new GDComponentList<>(Arrays.asList(kotoruption))));
		
		assertEquals(expected, scanner.compareAndListEvents(a, e));
	}

	@Test
	public void test_addedAndDeleted_compareAndListEvents() {
		final List<GDDispatchableEvent> expected = new ArrayList<>();
		expected.add(new GDDispatchableEvent(CommonEvents.AWARDED_LEVEL_ADDED,
				new GDComponentList<>(Arrays.asList(bloodbath, sonicwave))));
		expected.add(new GDDispatchableEvent(CommonEvents.AWARDED_LEVEL_DELETED,
				new GDComponentList<>(Arrays.asList(doramichallenge))));
		
		assertEquals(expected, scanner.compareAndListEvents(a, f));
	}
	
	@Test
	public void test_updated_compareAndListEvents() {
		final List<GDDispatchableEvent> expected = new ArrayList<>();
		expected.add(new GDDispatchableEvent(
				CommonEvents.AWARDED_LEVEL_UPDATED,
				new GDComponentList<>(Arrays.asList(new GDUpdatedComponent<>(doramichallenge, doramichallenge2)))
		));

		assertEquals(expected, scanner.compareAndListEvents(a, g));
	}

}
