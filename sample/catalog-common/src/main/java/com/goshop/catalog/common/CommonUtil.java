package com.goshop.catalog.common;

import org.springframework.util.CollectionUtils;

import java.util.*;

public final class CommonUtil {
    public static Date MinDate = new Date(0, 1, 1);
    public static Date MaxDate = new Date(9999, 12, 30);

    public interface IGetKey<K, V> {
        public K getKey(V obj);
    }

    public static <K, V> Map<K, List<V>> toMapList(List<V> list, IGetKey<K, V> keyMapper) {
        Map<K, List<V>> map = new HashMap<K, List<V>>();
        if(CollectionUtils.isEmpty(list)) return map;

        for(V element : list) {
            K key = (K)keyMapper.getKey(element);

            List<V> mapElement = map.get(key);
            if(mapElement == null) {
                mapElement = new ArrayList<V>();
                map.put(key, mapElement);
            }
            mapElement.add(element);
        }

        return map;
    }
}