package com.atex.plugins.map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utility methods to create {@link MapConfig} from
 * {@link com.atex.plugins.map.MapConfigPolicy}.
 */
public abstract class MapConfigFactory {

    /**
     * Create the MapConfig bean from the policy.
     *
     * @param policy a not null policy.
     * @return a not null value.
     */
    public static MapConfig createMapConfig(final MapConfigPolicy policy) {

        checkNotNull(policy);

        final MapConfig config = new MapConfig();
        config.setApiKey(policy.getGoogleMapKey());
        config.setRegion(policy.getGoogleMapRegion());
        config.setLanguage(policy.getGoogleMapLanguage());
        config.setLongitude(policy.getLongitude());
        config.setLatitude(policy.getLatitude());
        config.setZoom(policy.getZoom());
        config.setMapHeight(policy.getMapHeight());
        config.setMapClass(policy.getMapClass());
        return config;
    }
}
