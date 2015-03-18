package com.atex.plugins.map.util;

import com.google.gson.Gson;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.ContentPolicy;

/**
 * Utility that help you in storing an aspect into the content.
 */
public class AspectUtil {

    public static final String ASPECTS_GROUP = "aspects";

    private final ContentPolicy policy;
    private final Gson gson;

    public AspectUtil(final ContentPolicy policy) {

        this.policy = policy;
        this.gson = new Gson();
    }

    public <T> T getAspect(final String aspectName, final Class<T> tClass) throws CMException {

        return getPojo(ASPECTS_GROUP, aspectName, tClass);
    }

    private <T> T getPojo(final String group, final String aspectName, final Class<T> tClass) throws CMException {

        final String json = policy.getComponent(group, aspectName);
        if (json == null) {
            return null;
        }

        return gson.fromJson(json, tClass);

    }

    private <T> void setPojo(final String group, final String aspectName, final T pojo) throws CMException {

        final String json = gson.toJson(pojo);
        policy.setComponent(group, aspectName, json);

    }

    /**
     * Store the aspect into the content.
     *
     * @param aspectName the name of the aspect.
     * @param data the aspect data.
     * @param <T> the aspect type.
     * @throws CMException
     */
    public <T> void setAspect(final String aspectName, final T data) throws CMException {

        setPojo(ASPECTS_GROUP, aspectName, data);

    }

}
