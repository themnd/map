package com.atex.plugins.map;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit test for {@link com.atex.plugins.map.MapPoint}.
 */
@RunWith(MockitoJUnitRunner.class)
public class MapPointTest {
    
    private MapPoint point;
    
    @Before
    public void before() {
        point = new MapPoint();
    }

    @Test
    public void testGetLongitude() {
        Assert.assertNull(point.getLongitude());

        final String value = RandomStringUtils.random(10);

        point.setLongitude(value);
        Assert.assertEquals(value, point.getLongitude());
    }

    @Test
    public void testSetLongitude() {

        try {
            point.setLongitude(null);
            Assert.fail("setLongitude should raise an exception!");
        } catch (Exception e) {
            Assert.assertTrue("this is expected", true);
        }

    }

    @Test
    public void testGetLatitude() {

        Assert.assertNull(point.getLatitude());

        final String value = RandomStringUtils.random(10);

        point.setLatitude(value);
        Assert.assertEquals(value, point.getLatitude());

    }

    @Test
    public void testSetLatitude() {

        try {
            point.setLatitude(null);
            Assert.fail("setLatitude should raise an exception!");
        } catch (Exception e) {
            Assert.assertTrue("this is expected", true);
        }

    }

    @Test
    public void testGetZoom() {

        Assert.assertNull(point.getZoom());

        final String value = RandomStringUtils.random(10);

        point.setZoom(value);
        Assert.assertEquals(value, point.getZoom());

    }

    @Test
    public void testSetZoom() {

        try {
            point.setZoom(null);
            Assert.fail("setZoom should raise an exception!");
        } catch (Exception e) {
            Assert.assertTrue("this is expected", true);
        }

    }
}
