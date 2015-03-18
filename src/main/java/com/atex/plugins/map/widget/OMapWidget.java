package com.atex.plugins.map.widget;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.atex.plugins.map.MapAspectBean;
import com.atex.plugins.map.MapConfig;
import com.atex.plugins.map.MapConfigFactory;
import com.atex.plugins.map.MapConfigPolicy;
import com.atex.plugins.map.util.MapAspectBeanUtil;
import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.app.ContentSession;
import com.polopoly.cm.app.Editor;
import com.polopoly.cm.app.Viewer;
import com.polopoly.cm.app.util.URLBuilder;
import com.polopoly.cm.app.widget.OFieldPolicyWidget;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.client.CmClientBase;
import com.polopoly.cm.policy.ContentPolicy;
import com.polopoly.orchid.OrchidException;
import com.polopoly.orchid.context.Device;
import com.polopoly.orchid.context.OrchidContext;
import com.polopoly.orchid.js.JSWidget;
import com.polopoly.orchid.js.JSWidgetUtil;
import com.polopoly.orchid.widget.OHidden;
import com.polopoly.orchid.widget.OTextInput;

/**
 * Widget that render a map inside the GUI.
 */
public class OMapWidget extends OFieldPolicyWidget implements Viewer, Editor, JSWidget {

    private static Logger logger = Logger.getLogger(OMapWidget.class.getName());

    public static final ExternalContentId PLUGIN_FILES_EXTERNAL_CONTENT_ID =
            new ExternalContentId("plugins.com.atex.plugins.map.files");
    public static final String CONFIG_EXT_ID = "plugins.com.atex.plugins.map.Config";

    public static final String WIDGET_JS_FILE_PATH = "js/widget.js";

    private boolean isEditMode;

    private OTextInput locationField;
    private OHidden jsonContent;
    private OHidden jsonConfig;

    private String widgetJsPath;

    private Gson gson;
    private CmClient cmClient;

    @Override
    public void initSelf(final OrchidContext oc) throws OrchidException {

        super.initSelf(oc);

        cmClient = (CmClient) oc.getApplication().getApplicationComponent(CmClientBase.DEFAULT_COMPOUND_NAME);

        gson = new Gson();

        isEditMode = (getContentSession().getMode() == ContentSession.EDIT_MODE);

        locationField = new OTextInput();
        locationField.setSize(50);
        addAndInitChild(oc, locationField);

        jsonContent = new OHidden();
        addAndInitChild(oc, jsonContent);

        jsonConfig = new OHidden();
        addAndInitChild(oc, jsonConfig);

        widgetJsPath = lookupContentFile(PLUGIN_FILES_EXTERNAL_CONTENT_ID, WIDGET_JS_FILE_PATH, oc);
    }

    @Override
    public String[] getJSScriptDependencies() {
        return new String[] {
                widgetJsPath,
        };
    }

    @Override
    public String[] getInitParams() throws OrchidException {
        return new String[]{
                JSWidgetUtil.toJSString(this.getCompoundId()),
                JSWidgetUtil.toJSString(jsonContent.getCompoundId()),
                JSWidgetUtil.toJSString(jsonConfig.getCompoundId()),
                JSWidgetUtil.toJSString(Boolean.toString(isEditMode))
        };
    }

    @Override
    public String getInitScript() throws OrchidException {
        return JSWidgetUtil.genInitScript(this);
    }

    @Override
    public String getJSWidgetClassName() throws OrchidException {
        return "JSMapWidget";
    }

    @Override
    public String getFriendlyName() throws OrchidException {
        return null;
    }

    @Override
    public boolean isAjaxTopWidget() {
        return true;
    }

    private String lookupContentFile(final ContentId contentId, final String fileName, final OrchidContext oc) {

        try {
            return URLBuilder.getFileUrl(contentId, fileName, oc);
        } catch (OrchidException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
        return null;

    }

    @Override
    public void storeSelf() throws CMException {

        final MapAspectBean bean = gson.fromJson(jsonContent.getValue(), MapAspectBean.class);

        final ContentPolicy parentPolicy = (ContentPolicy) getPolicy().getParentPolicy();
        MapAspectBeanUtil.setMapAspectBean(parentPolicy, bean);

    }

    @Override
    public void initValueFromPolicy() throws CMException {

        final ContentPolicy parentPolicy = (ContentPolicy) getPolicy().getParentPolicy();
        final MapAspectBean bean = Optional.fromNullable(
                MapAspectBeanUtil.getMapAspectBean(parentPolicy)).or(new MapAspectBean());

        jsonContent.setValue(gson.toJson(bean));

        final MapConfigPolicy configPolicy = (MapConfigPolicy) cmClient.getPolicyCMServer().getPolicy(
                new ExternalContentId(CONFIG_EXT_ID));
        final MapConfig config = MapConfigFactory.createMapConfig(configPolicy);

        jsonConfig.setValue(gson.toJson(config));
    }

    @Override
    public void localRender(final OrchidContext oc) throws IOException, OrchidException {

        Device device = oc.getDevice();

        jsonContent.render(oc);
        jsonConfig.render(oc);

        device.println("<div class=\"mapWidget\">");
        String indicatorMessage = "<img src=\"images/ajax/busy_indicator_big_green.gif\"/>"
                + "GOOGLE MAP being loaded...";
                //+ LocaleUtil.format("com.atex.plugins.image-gallery.label.dnd", oc.getMessageBundle());

        device.println("<span class=\"message\">" + indicatorMessage + "</span>");
        device.println("</div>");

    }

}
