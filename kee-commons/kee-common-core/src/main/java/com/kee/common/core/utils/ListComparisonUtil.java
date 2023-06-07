package com.kee.common.core.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 对应于在表单中的list的比较操作crud
 * @Description : Object
 * @author: zeng.maosen
 */
public class ListComparisonUtil {

    /**
     * 筛选的条件必须保证唯一性，curList对应的field值可以为空，但是preList对应的field值不能为空
     * @param curList
     * @param preList
     * @param screenKey
     * @param <K>
     * @return
     */
    private static <K> Map<String, List<K>> twoListComparison(List<K> curList, List<K> preList, String screenKey) {
        try {
            String addExcessive = "addExcessive";
            String removeExcessive = "removeExcessive";
            String updateCommon = "updateCommon";
            Map<String, List<K>> map = new HashMap<>(16);
            map.put(addExcessive, new ArrayList<>());
            map.put(removeExcessive, new ArrayList<>());
            map.put(updateCommon, new ArrayList<>());
            if (curList.isEmpty()) {
                map.put(removeExcessive, preList);
                return map;
            }
            if (preList.isEmpty()) {
                map.put(addExcessive, curList);
                return map;
            }
            Field key = findField(curList, screenKey);
            List<Object> curIds = new ArrayList<>();
            Map<Object, K> curKMap = new HashMap<>();
            Map<Object, K> preKMap = new HashMap<>();
            for (K k : curList) {
                Object o = key.get(k);
                if (StringUtils.isNull(o)) {
                    List<K> ks = map.get(addExcessive);
                    ks.add(k);
                    map.put(addExcessive, ks);
                    continue;
                }
                curKMap.put(o, k);
                curIds.add(o);
            }
            List<Object> copyCurIds = new ArrayList<>(curIds);
            List<Object> copyCurIds2 = new ArrayList<>(curIds);
            List<Object> preIds = new ArrayList<>();
            for (K k : preList) {
                Object o = key.get(k);
                if (StringUtils.isNull(o)) {
                    continue;
                }
                preKMap.put(o, k);
                preIds.add(o);
            }
            List<Object> copyPreIds = new ArrayList<>(preIds);
            //add
            copyCurIds.removeAll(copyPreIds);
            //remove
            copyPreIds.removeAll(copyCurIds2);
            //common
            copyCurIds2.retainAll(preIds);

            mapIds(map, curKMap, copyCurIds, addExcessive);

            mapIds(map, preKMap, copyPreIds, removeExcessive);

            mapIds(map, curKMap, copyCurIds2, updateCommon);

            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <K> Map<String, Object> listAndStringComparison(List<K> preList, List<String> curIds, String screenKey) {
        try {
            String addExcessive = "addExcessive";
            String removeExcessive = "removeExcessive";
            Map<String, Object> map = new HashMap<>(16);
            map.put(addExcessive, new ArrayList<>());
            map.put(removeExcessive, new ArrayList<>());
            if (curIds.isEmpty()) {
                map.put(removeExcessive, preList);
                return map;
            }
            if (preList.isEmpty()) {
                map.put(addExcessive, curIds);
                return map;
            }
            Field key = findField(preList, screenKey);
            List<String> preIds = new ArrayList<>();
            Map<String, K> preKMap = new HashMap<>();
            for (K k : preList) {
                Object o = key.get(k);
                if (Long.class == key.getType() || o instanceof Long) {
                    o = String.valueOf(o);
                }
                if (Integer.class == key.getType() || o instanceof Integer) {
                    o = String.valueOf(o);
                }
                if (StringUtils.isNull(o)) {
                    continue;
                }
                preKMap.put((String) o, k);
                preIds.add((String) o);
            }
            List<String> copyCurIds = new ArrayList<>(curIds);
            List<String> copyCurIds2 = new ArrayList<>(curIds);
            List<String> copyPreIds = new ArrayList<>(preIds);
            //add
            copyCurIds.removeAll(copyPreIds);
            //remove
            copyPreIds.removeAll(copyCurIds2);

            for (String id : copyCurIds) {
                List<String> ks = (List<String>) map.get(addExcessive);
                ks.add(id);
                map.put(addExcessive, ks);
            }
            for (String preId : copyPreIds) {
                List<K> ks = (List<K>) map.get(removeExcessive);
                ks.add(preKMap.get(preId));
                map.put(addExcessive, ks);
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <K> void mapIds(Map<String, List<K>> map, Map<Object, K> objectKMap, List<Object> objects, String mapKey) {
        for (Object id : objects) {
            List<K> ks = map.get(mapKey);
            ks.add(objectKMap.get(id));
            map.put(mapKey, ks);
        }
    }

    private static <K> Field findField(List<K> curList, String screenKey) {
        K k0 = curList.get(0);
        Class<?> clazz = k0.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Field key = null;
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().equals(screenKey)) {
                key = field;
                break;
            }
        }
        return key;
    }
}
