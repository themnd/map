package com.atex.plugins.map;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.atex.onecms.content.aspects.annotations.AspectDefinition;
import com.google.common.collect.Lists;

/**
 * Map aspect bean.
 * It contains a list of points.
 */
@AspectDefinition
public class MapAspectBean {

    /**
     * Name of the aspect.
     */
    public static final String ASPECT_NAME = MapAspectBean.class.getName();

    /**
     * First version of the bean.
     */
    public static final int VERSION_1 = 1;

    /**
     * Always point to latest version.
     */
    public static final int LATEST_VERSION = VERSION_1;

    /**
     * Current version.
     */
    private int version = LATEST_VERSION;

    private List<MapPoint> points = Lists.newArrayList();

    /**
     * Return the latest version.
     *
     * @return
     */
    public int getVersion() {

        return version;

    }

    /**
     * Set the latest version.
     *
     * @param version
     */
    public void setVersion(final int version) {

        this.version = version;

    }

    /**
     * The list of points.
     *
     * @return a not null list.
     */
    public List<MapPoint> getPoints() {
        return points;
    }

    /**
     * Set the list of points.
     *
     * @param points a not null list.
     */
    public void setPoints(final List<MapPoint> points) {
        this.points = checkNotNull(points);
    }

}
