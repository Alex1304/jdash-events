package com.github.alex1304.jdashevents.scanner;

import java.util.HashSet;
import java.util.function.Predicate;

import com.github.alex1304.jdash.api.GDHttpClient;
import com.github.alex1304.jdash.api.request.GDLevelHttpRequest;
import com.github.alex1304.jdash.api.request.GDLevelSearchHttpRequest;
import com.github.alex1304.jdash.component.GDLevel;
import com.github.alex1304.jdash.component.GDLevelPreview;
import com.github.alex1304.jdash.exceptions.GDAPIException;
import com.github.alex1304.jdash.util.Constants;
import com.github.alex1304.jdashevents.common.CommonEvents;

/**
 * Scans for events in the section of awarded levels
 *
 * @author Alex1304
 */
public class AwardedLevelEventScanner extends ComponentListUpdatedEventScanner<GDLevelPreview> {

	/**
	 * @param client
	 *            - the HTTP client to use for processing the requests
	 */
	public AwardedLevelEventScanner(GDHttpClient client) {
		super(
			client,
			new GDLevelSearchHttpRequest(Constants.LEVEL_SEARCH_TYPE_AWARDED, "", new HashSet<>(),
				new HashSet<>(), 0, false, false, false, false, false, false, false, false, Constants.LEVEL_SEARCH_DIFF_ALL),
			CommonEvents.AWARDED_LEVEL_ADDED, CommonEvents.AWARDED_LEVEL_DELETED,
			CommonEvents.AWARDED_LEVEL_UPDATED
		);
	}

	@Override
	public int compare(GDLevelPreview o1, GDLevelPreview o2) {
		return (o1.getStars() != o2.getStars() ||
				o1.getDifficulty() != o2.getDifficulty() ||
				o1.getFeaturedScore() != o2.getFeaturedScore() ||
				o1.isEpic() != o2.isEpic() ||
				o1.isDemon() != o2.isDemon() ||
				o1.isAuto() != o2.isAuto() ||
				o1.hasCoinsVerified() != o2.hasCoinsVerified() ||
				o1.getCoinCount() != o2.getCoinCount() ||
				o1.getDemonDifficulty() != o2.getDemonDifficulty() ||
				o1.getLength() != o2.getLength() ||
				!o1.getSongTitle().equals(o2.getSongTitle())) ? -1 : 0;
	}
	
	@Override
	public Predicate<GDLevelPreview> ignoreRemovedComponentIf() {
		return x -> {
			GDHttpClient client = new GDHttpClient();
			GDLevel level = null;
			try {
				level = client.fetch(new GDLevelHttpRequest(x.getId()));
			} catch (GDAPIException e) {
				return true;
			}
			return level != null && (level.getStars() > 0 || level.hasCoinsVerified());
		};
	}

}
