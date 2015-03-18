package com.atex.plugins.map.util;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.atex.onecms.content.ContentId;
import com.atex.onecms.content.ContentManager;
import com.atex.onecms.content.ContentResult;
import com.atex.onecms.content.ContentVersionId;
import com.atex.onecms.content.ContentWrite;
import com.atex.onecms.content.IdUtil;
import com.atex.onecms.content.Subject;
import com.atex.onecms.content.SubjectUtil;
import com.atex.onecms.content.aspects.Aspect;
import com.atex.onecms.content.repository.ContentModifiedException;
import com.atex.plugins.map.MapAspectBean;
import com.google.common.collect.Maps;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.policy.ContentPolicy;

/**
 * Util that allow you to easily access to map beans.
 */
public abstract class MapAspectBeanUtil {

    private static final Logger LOGGER = Logger.getLogger(MapAspectBeanUtil.class.getName());

    private static final Subject SYSTEM_SUBJECT = new Subject("98", null);

    public static MapAspectBean getMapAspectBean(final CmClient cmClient, final ContentId contentId) {

        final ContentManager contentManager = cmClient.getContentManager();
        final Subject subject = SubjectUtil.fromCaller(cmClient.getPolicyCMServer().getCurrentCaller());
        final ContentVersionId versionedId = contentManager.resolve(contentId, subject);
        if (versionedId != null) {
            final Map<String, Object> aspects = Maps.newHashMap();
            aspects.put(MapAspectBean.ASPECT_NAME, MapAspectBean.class);
            ContentResult<Object> result = contentManager.get(
                    versionedId,
                    null,
                    null,
                    aspects,
                    Subject.NOBODY_CALLER);

            if (result.getStatus().isOk()) {
                final Aspect<MapAspectBean> aspect = result.getContent().getAspect(MapAspectBean.ASPECT_NAME);
                if (aspect != null) {
                    return aspect.getData();
                }
            } else {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE,
                            String.format(
                                    "Tried to fetch %s with variant %s but ContentManager result was %d.",
                                    IdUtil.toVersionedIdString(versionedId),
                                    MapAspectBean.ASPECT_NAME,
                                    result.getStatus().getDetailCode()));
                }
            }
        } else {
            LOGGER.log(Level.WARNING, "Could not resolve to versioned id: "
                    + IdUtil.toIdString(contentId));
        }
        return null;

    }

    public static void setMapAspectBean(final CmClient cmClient, final ContentId contentId, final MapAspectBean bean)
            throws CMException {

        final ContentManager contentManager = cmClient.getContentManager();
        final Subject subject = SubjectUtil.fromCaller(cmClient.getPolicyCMServer().getCurrentCaller());
        final ContentVersionId versionedId = contentManager.resolve(contentId, subject);

        try {
            ContentResult<Object> mapRead = contentManager.get(versionedId, null, Object.class, null, subject);
            if (mapRead != null) {
                ContentWrite<Object> mapUpdate = new ContentWrite<Object>(mapRead.getContent());
                mapUpdate.setAspect(MapAspectBean.ASPECT_NAME, bean);
                contentManager.update(contentId, mapUpdate, subject);
            }
        } catch (ContentModifiedException e) {
            throw new CMException(e);
        }

    }

    public static MapAspectBean getMapAspectBean(final ContentPolicy policy) throws CMException {

        return new AspectUtil(policy).getAspect(MapAspectBean.ASPECT_NAME, MapAspectBean.class);
    }

    public static void setMapAspectBean(final ContentPolicy policy, final MapAspectBean bean) throws CMException {

        new AspectUtil(policy).setAspect(MapAspectBean.ASPECT_NAME, bean);

    }

}
