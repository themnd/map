package com.atex.plugins.map;

import java.util.Arrays;
import java.util.logging.Level;

import com.atex.onecms.content.ContentResult;
import com.atex.onecms.content.ContentWrite;
import com.atex.onecms.content.LegacyContentAdapter;
import com.atex.onecms.content.aspects.Aspect;
import com.atex.plugins.baseline.policy.BaselinePolicy;
import com.atex.plugins.map.util.AspectUtil;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.policymvc.PolicyModelDomain;
import com.polopoly.model.DescribesModelType;

/**
 * The policy used to store the geo points.
 */
@DescribesModelType
public class MapPolicy extends BaselinePolicy implements LegacyContentAdapter<MapAspectBean> {

    private final AspectUtil aspectUtil;

    /**
     * Constructor.
     *
     * @param cmClient the {@link com.polopoly.cm.client.CmClient}.
     */
    public MapPolicy(final CmClient cmClient) {

        this.aspectUtil = new AspectUtil(this);
    }

    /**
     * Return the map.
     *
     * @return a not null bean.
     */
    public MapAspectBean getMap() {

        try {
            final MapAspectBean map = aspectUtil.getAspect(MapAspectBean.ASPECT_NAME, MapAspectBean.class);
            if (map != null) {
                return map;
            }
        } catch (CMException e) {
            logger.log(Level.SEVERE, "cannot find map for " + getContentId().getContentIdString() + ": "
                    + e.getMessage(), e);
        }

        // no map have been found, return an empty aspect.

        return new MapAspectBean();

    }

    /**
     * Set the map.
     *
     * @param map the map.
     * @throws CMException
     */
    public void setMap(final MapAspectBean map) throws CMException {

        aspectUtil.setAspect(MapAspectBean.ASPECT_NAME, map);

    }

    @Override
    public ContentResult<MapAspectBean> legacyToNew(final PolicyModelDomain policyModelDomain) throws CMException {
        final MapAspectBean bean = getMap();
        return new ContentResult<>(
                null,
                MapAspectBean.class.getName(),
                new Aspect<>(MapAspectBean.class.getName(), bean),
                null,
                null,
                Arrays.<Aspect>asList(
                        new Aspect<>(MapAspectBean.ASPECT_NAME, getMap())
                ));
    }

    @Override
    public void newToLegacy(final ContentWrite<MapAspectBean> dataWrite) throws CMException {
        MapAspectBean bean = dataWrite.getContentData();
        if (bean == null) {
            bean = dataWrite.getAspect(MapAspectBean.ASPECT_NAME, MapAspectBean.class);
        }
        if (bean != null) {
            setMap(bean);
        }
    }
}
