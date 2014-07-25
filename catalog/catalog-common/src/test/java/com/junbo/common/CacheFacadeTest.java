package com.junbo.common;

import com.junbo.catalog.common.cache.CacheFacade;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by baojing on 7/24/14.
 */
public class CacheFacadeTest {
    @Test
    public void testBVT() {
        String key1 = "id12345", key2 = "hello";
        String value1 = "wo le ge qu", value2 = "world";

        CacheFacade.ITEM.put(key1, value1);
        CacheFacade.ITEM.put(key2, value2);
        Assert.assertEquals(CacheFacade.ITEM.get(key1), value1);
        Assert.assertEquals(CacheFacade.ITEM.getAllKeys().size(), 2);
        CacheFacade.ITEM.evict(key1);
        Assert.assertEquals(CacheFacade.ITEM.getAllKeys().size(), 1);
        Assert.assertNull(CacheFacade.ITEM.get(key1));
        CacheFacade.ITEM.evictAll();
        Assert.assertNull(CacheFacade.ITEM.get(key2));
        Assert.assertEquals(CacheFacade.ITEM.getAllKeys().size(), 0);

        List<String> list = Arrays.asList("abc", "ef", "afdsdafs");
        CacheFacade.ITEM_CONTROL.put(key1, list);
        List<String> retrieved = CacheFacade.ITEM_CONTROL.get(key1);
        Assert.assertEquals(retrieved.size(), list.size());
    }
}
