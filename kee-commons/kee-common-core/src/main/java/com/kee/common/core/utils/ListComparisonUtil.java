package com.kee.common.core.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 在表单中的list的比较操作
 * @Description : Object
 * @author: zeng.maosen
 */
public class ListComparisonUtil {

    public static String add = "addExcessive";
    public static String remove = "removeExcessive";
    public static String update = "updateCommon";
    /**
     * 筛选的条件必须保证唯一性，curList对应的field值可以为空，但是preList对应的field值不能为空
     * @param curList 前端list
     * @param preList 后端list
     * @param screenKey
     * @param <K>
     * @return
     */
    private static <K> Map<String, List<K>> twoListComparison(List<K> curList, List<K> preList, String screenKey) {
        try {

            Map<String, List<K>> map = new HashMap<>(16);
            map.put(add, new ArrayList<>());
            map.put(remove, new ArrayList<>());
            map.put(update, new ArrayList<>());
            if (curList.isEmpty()) {
                map.put(remove, preList);
                return map;
            }
            if (preList.isEmpty()) {
                map.put(add, curList);
                return map;
            }
            Field key = findField(curList, screenKey);
            List<Object> curIds = new ArrayList<>();
            Map<Object, K> curKMap = new HashMap<>();
            Map<Object, K> preKMap = new HashMap<>();
            for (K k : curList) {
                Object o = key.get(k);
                if (StringUtils.isNull(o)) {
                    List<K> ks = map.get(add);
                    ks.add(k);
                    map.put(add, ks);
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

            mapIds(map, curKMap, copyCurIds, add);

            mapIds(map, preKMap, copyPreIds, remove);

            mapIds(map, curKMap, copyCurIds2, update);

            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <K> Map<String, Object> listAndStringComparison(List<K> preList, List<String> curIds, String screenKey) {
        try {
            Map<String, Object> map = new HashMap<>(16);
            map.put(add, new ArrayList<>());
            map.put(remove, new ArrayList<>());
            map.put(update, new ArrayList<>());
            if (curIds.isEmpty()) {
                map.put(remove, preList);
                return map;
            }
            if (preList.isEmpty()) {
                map.put(add, curIds);
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
            //common
            preIds.removeAll(copyPreIds);

            for (String id : copyCurIds) {
                List<String> ks = (List<String>) map.get(add);
                ks.add(id);
                map.put(add, ks);
            }
            for (String preId : copyPreIds) {
                List<K> ks = (List<K>) map.get(remove);
                ks.add(preKMap.get(preId));
                map.put(remove, ks);
            }
            for(String id:preIds){
                List<K> ks = (List<K>) map.get(update);
                ks.add(preKMap.get(id));
                map.put(update, ks);
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

////    测试案例
//    public static void main(String[] args) {
//        List<JointInvitationLetter> jointInvitationLetters = new ArrayList<>();
//        JointInvitationLetter jointInvitationLetter = new JointInvitationLetter();
//        jointInvitationLetter.setJointInvitationLetterId(1L);
//        JointInvitationLetter jointInvitationLetter1 = new JointInvitationLetter();
//        jointInvitationLetter1.setJointInvitationLetterId(2L);
//        jointInvitationLetters.add(jointInvitationLetter);
//        jointInvitationLetters.add(jointInvitationLetter1);
//        List<String> list = new ArrayList<>();
//        list.add("1");
//        list.add("3");
//        Map<String, Object> map = listAndStringComparison(jointInvitationLetters, list, "jointInvitationLetterId");
//        System.out.println(map.get(update));
//        System.out.println(map.get(add));
//        System.out.println(map.get(remove));

//        List<JointInvitationLetter> jointInvitationLetters = new ArrayList<>();
//        JointInvitationLetter jointInvitationLetter = new JointInvitationLetter();
//        jointInvitationLetter.setJointInvitationLetterId(1L);
//        JointInvitationLetter jointInvitationLetter1 = new JointInvitationLetter();
//        jointInvitationLetter1.setJointInvitationLetterId(2L);
//        jointInvitationLetters.add(jointInvitationLetter);
//        jointInvitationLetters.add(jointInvitationLetter1);
//
//        List<JointInvitationLetter> jointInvitationLetters1 = new ArrayList<>();
//        JointInvitationLetter jointInvitationLetter11 = new JointInvitationLetter();
//        jointInvitationLetter11.setJointInvitationLetterId(1L);
//        JointInvitationLetter jointInvitationLetter12 = new JointInvitationLetter();
//        jointInvitationLetter12.setJointInvitationLetterId(3L);
//        jointInvitationLetters1.add(jointInvitationLetter11);
//        jointInvitationLetters1.add(jointInvitationLetter12);
//
//        Map<String, List<JointInvitationLetter>> map = twoListComparison(jointInvitationLetters1, jointInvitationLetters, "jointInvitationLetterId");
//        System.out.println(map.get(update));
//        System.out.println(map.get(add));
//        System.out.println(map.get(remove));
//    }
}
