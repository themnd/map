package com.atex.plugins.map;

/**
 * Map config bean.
 */
public class MapConfig {

    private String apiKey;
    private String region;
    private String language;
    private String longitude;
    private String latitude;
    private String zoom;
    private String mapHeight;
    private String mapClass;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(final String apiKey) {
        this.apiKey = apiKey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(final String region) {
        this.region = region;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(final String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(final String latitude) {
        this.latitude = latitude;
    }

    public String getZoom() {
        return zoom;
    }

    public void setZoom(final String zoom) {
        this.zoom = zoom;
    }

    public String getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(final String mapHeight) {
        this.mapHeight = mapHeight;
    }

    public String getMapClass() {
        return mapClass;
    }

    public void setMapClass(final String mapClass) {
        this.mapClass = mapClass;
    }
}
