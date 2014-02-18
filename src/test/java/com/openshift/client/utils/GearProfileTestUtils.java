package com.openshift.client.utils;

import com.openshift.client.IDomain;
import com.openshift.client.IGearProfile;
import com.openshift.internal.client.GearProfile;

import java.util.List;

/**
 * Created by corey on 2/17/14.
 */
public class GearProfileTestUtils {
	public static IGearProfile getFirstAvailableGearProfile(IDomain domain) {
		IGearProfile gear = null;
		List<IGearProfile> gears = domain.getAvailableGearProfiles();
		if (gears != null
				&& !gears.isEmpty()) {
			gear = gears.get(0);
		}
		return gear;
	}
}
