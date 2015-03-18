package com.atex.plugins.map;


import java.util.Date;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit test for {@link com.atex.plugins.map.MapAspectBean}.
 */
@RunWith(MockitoJUnitRunner.class)
public class MapAspectBeanTest {
    
    @Test
    public void testPointsNotNull() {
        
        final MapAspectBean bean = new MapAspectBean();
        Assert.assertNotNull(bean.getPoints());
        Assert.assertEquals(0, bean.getPoints().size());

    }

    @Test
    public void testAddPoints() {

        final MapAspectBean bean = new MapAspectBean();
        bean.getPoints().add(new MapPoint());
        bean.getPoints().add(new MapPoint());
        bean.getPoints().add(new MapPoint());

        Assert.assertEquals(3, bean.getPoints().size());

    }

    @Test
    public void testSetNullPoints() {

        final MapAspectBean bean = new MapAspectBean();
        try {
            bean.setPoints(null);
            Assert.fail("setPoints should raise an exception!");
        } catch (Exception e) {
            Assert.assertTrue("this is expected", true);
        }

    }

    @Test
    public void testVersion() {

        final MapAspectBean bean = new MapAspectBean();

        Assert.assertEquals(MapAspectBean.LATEST_VERSION, bean.getVersion());
        
        final int randomVersion = new Random(new Date().getTime()).nextInt();

        bean.setVersion(randomVersion);

        Assert.assertEquals(randomVersion, bean.getVersion());
    }
}
