package com.atex.plugins.map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Geographic point.
 */
public class MapPoint {

    private String longitude;
    private String latitude;
    private String zoom;

    /**
     * Get the longitude.
     *
     * @return the longitude value.
     */
    public String getLongitude() {

        return longitude;

    }

    /**
     * Set the longitude.
     *
     * @param longitude a not null value.
     */
    public void setLongitude(final String longitude) {

        this.longitude = checkNotNull(longitude);

    }

    /**
     * Get the latitude.
     *
     * @return the latitude value.
     */
    public String getLatitude() {

        return latitude;

    }

    /**
     * Set the latitude.
     *
     * @param latitude a not null value.
     */
    public void setLatitude(final String latitude) {

        this.latitude = checkNotNull(latitude);

    }

    /**
     * Get the zoom level.
     *
     * @return the zoom level.
     */
    public String getZoom() {

        return zoom;

    }

    /**
     * Set the zoom level.
     *
     * @param zoom a not null value.
     */
    public void setZoom(final String zoom) {

        this.zoom = checkNotNull(zoom);

    }
}
