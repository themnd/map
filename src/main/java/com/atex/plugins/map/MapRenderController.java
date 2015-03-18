package com.atex.plugins.map;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.atex.onecms.content.IdUtil;
import com.atex.plugins.map.util.MapAspectBeanUtil;
import com.google.gson.Gson;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.model.Model;
import com.polopoly.model.ModelPathUtil;
import com.polopoly.render.RenderRequest;
import com.polopoly.siteengine.dispatcher.ControllerContext;
import com.polopoly.siteengine.model.TopModel;
import com.polopoly.siteengine.mvc.RenderControllerBase;

/**
 * MapRenderController.
 *
 */
public class MapRenderController extends RenderControllerBase {

    private static final Logger LOGGER = Logger.getLogger(MapRenderController.class.getName());

    public static final String CONFIG_EXT_ID = "plugins.com.atex.plugins.map.Config";

    @Override
    public void populateModelBeforeCacheKey(final RenderRequest request, final TopModel m,
                                            final ControllerContext context) {

        super.populateModelBeforeCacheKey(request, m, context);

        final Model model = m.getLocal();

        final Gson gson = new Gson();

        final CmClient cmClient = getCmClient(context);
        if (cmClient == null) {
            throw new CMRuntimeException("Could not fetch cmClient");
        }

        final Policy contentPolicy = (Policy) ModelPathUtil.getBean(context.getContentModel());
        com.atex.onecms.content.ContentId contentId = IdUtil.fromPolicyContentId(contentPolicy.getContentId().getContentId());
        if (contentId != null) {
            try {
                final MapAspectBean info = MapAspectBeanUtil.getMapAspectBean(cmClient, contentId);
                if (info != null) {
                    final String json = gson.toJson(info);
                    ModelPathUtil.set(model, "mapJSON", json);
                }
            } catch (ClassCastException e) {
                throw new CMRuntimeException("Failed to fetch cm client: " + e.getMessage(), e);
            }
        }

        try {
            final MapConfigPolicy mapConfigPolicy = getMapConfiguration(context);
            final MapConfig config = MapConfigFactory.createMapConfig(mapConfigPolicy);
            ModelPathUtil.set(model, "config", mapConfigPolicy);
            ModelPathUtil.set(model, "configJSON", gson.toJson(config));
        } catch (CMException e) {
            LOGGER.log(Level.SEVERE, "Cannot get configuration: " + e.getMessage(), e);
        }
    }

    private MapConfigPolicy getMapConfiguration(final ControllerContext context) throws CMException {

        PolicyCMServer policyCMServer = getCmClient(context).getPolicyCMServer();
        return (MapConfigPolicy) policyCMServer.getPolicy(new ExternalContentId(CONFIG_EXT_ID));

    }

}
