How to integrate into your project
==================================

1. clone the repo and put it into your plugins folder.
2. modify the project pom.xml to include it

	```
	<modules>
		...
		<module>plugins/map</module>
	</modules>


	<dependencies>
		...
		<dependency>
	      <groupId>com.atex.plugins</groupId>
	      <artifactId>map</artifactId>
	      <version>1.2</version>
	    </dependency>
	    ...
	    <dependency>
	      <groupId>com.atex.plugins</groupId>
	      <artifactId>map</artifactId>
	      <version>1.2</version>
	      <classifier>contentdata</classifier>
	    </dependency>
	    ...
	</dependencies>
	```
3. in you common content module specify the plugins configuration:

	```
	id:plugins.com.atex.plugins.map.Config
	major:appconfig
	inputtemplate:com.atex.plugins.map.Configuration
	securityparent:p.siteengine.Configuration.d
	component:polopoly.Content:name:Map Configuration
	component:zoom:value:7
	component:mapheight:value:270px
	component:mapclass:value:v3_embed-content
	component:gmapregion:value:IT
	component:gmaplanguage:value:it
	```

5. There is a `Map` element that you can add on the page.
6. Optionally you can inclue the map as a field of an existing element/article (do not forget to render it in the vm):

	```
	  <layout name="mapPage" input-template="p.Page" label="standard.Article.Map.Page">
        <param name="lazyInit">true</param>

        <field name="map" input-template="com.atex.plugins.map.MapWidget" label="standard.Article.Map.Widget">
        </field>

      </layout>
    ```

7. To use it in ACT, you have to modify the policy (for example ArticlePolicy):

    ```
    @Override
    public ContentResult<ArticleBean> legacyToNew(final PolicyModelDomain modelDomain) throws CMException {
      ...
      return new ContentResult<>(
                      null,
                      ArticleBean.class.getName(),
                      new Aspect<>(ArticleBean.class.getName(), bean),
                      null,
                      null,
                      Arrays.<Aspect>asList(
                              new Aspect<>("atex.Metadata", getMetadataInfo()),
                              new Aspect<>(MapAspectBean.ASPECT_NAME, getMap())
                      ));
    }
    ```

    ```
    @Override
    public void newToLegacy(final ContentWrite<ArticleBean> dataWrite) throws CMException {
        ...
        MapAspectBean mapBean = dataWrite.getAspect(MapAspectBean.ASPECT_NAME,
                MapAspectBean.class);
        if (mapBean != null) {
            setMap(mapBean);
        }
    }
    ```

    ```
    public MapAspectBean getMap() {
        try {
            return MapAspectBeanUtil.getMapAspectBean(this);
        } catch (CMException e) {
            logger.log(Level.SEVERE, "cannot get map: " + e.getMessage(), e);
        }
        return null;
    }

    public void setMap(final MapAspectBean bean)
            throws CMException {
        MapAspectBeanUtil.setMapAspectBean(this, bean);
    }
    ```

    and you should add the dependency to source-common/pom.xml:

    ```
    <dependency>
      <groupId>com.atex.plugins</groupId>
      <artifactId>map</artifactId>
      <version>1.1</version>
    </dependency>
    ```

8. You can use it in act too:

	```
	  "aspects": {
	    "atex.Metadata": {},
	    "com.atex.plugins.map.MapAspectBean": {}
	  },

	  "dataBindings": [
	    {
	      "label": "Map",
	      "name": "map",
	      "widget": "mapWidget",

	      "config": {
	        "height": "300px"
	      },

	      "domainObjects": {
	        "point": "com.atex.plugins.map.MapAspectBean"
	      }
	    }  
	  ]
	```
