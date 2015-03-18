package com.atex.plugins.map;

import com.atex.plugins.baseline.policy.BaselinePolicy;
import com.google.common.base.Strings;
import com.polopoly.model.DescribesModelType;

/**
 * Policy for the plugin configuration.
 */
@DescribesModelType
public class MapConfigPolicy extends BaselinePolicy {

    private static final String GMAP_KEY = "gmapkey";
    private static final String GMAP_REGION = "gmapregion";
    private static final String GMAP_LANGUAGE = "gmaplanguage";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String ZOOM = "zoom";
    private static final String MAPHEIGHT = "mapheight";
    private static final String MAPCLASS = "mapclass";
    private static final String DEFAULT_LONGITUDE = "9.184872";
    private static final String DEFAULT_LATITUDE = "45.503557";
    private static final String DEFAULT_ZOOM = "7";

    /**
     * Return the configured google map key.
     *
     * @return a not null String.
     */
    public String getGoogleMapKey() {

        return Strings.nullToEmpty(getChildValue(GMAP_KEY, ""));
    }

    /**
     * Return the configured google map region.
     *
     * @return a not null String.
     */
    public String getGoogleMapRegion() {

        return Strings.nullToEmpty(getChildValue(GMAP_REGION, ""));
    }

    /**
     * Return the configured google map language.
     *
     * @return a not null String.
     */
    public String getGoogleMapLanguage() {

        return Strings.nullToEmpty(getChildValue(GMAP_LANGUAGE, ""));
    }

    /**
     * Return the default longitude.
     *
     * @return a not null String.
     */
    public String getLongitude() {

        return Strings.nullToEmpty(getChildValue(LONGITUDE, DEFAULT_LONGITUDE));
    }

    /**
     * Return the default latitude.
     *
     * @return a not null String.
     */
    public String getLatitude() {

        return Strings.nullToEmpty(getChildValue(LATITUDE, DEFAULT_LATITUDE));
    }

    /**
     * Return the default zoom.
     *
     * @return a not null String.
     */
    public String getZoom() {

        return Strings.nullToEmpty(getChildValue(ZOOM, DEFAULT_ZOOM));
    }

    /**
     * Return the map height.
     *
     * @return a not null String.
     */
    public String getMapHeight() {

        return Strings.nullToEmpty(getChildValue(MAPHEIGHT, ""));
    }

    /**
     * Return the map css class.
     *
     * @return a not null String.
     */
    public String getMapClass() {

        return Strings.nullToEmpty(getChildValue(MAPCLASS, ""));
    }
}
