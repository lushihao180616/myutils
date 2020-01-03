package com.lushihao.myutils.collection;

import java.util.*;

public final class LSHListUtils {
    /**
     * 比较一个list集合里是否有重复
     *
     * @param list
     * @return
     */
    public static boolean isRepeat(List<Object> list) {
        Set<Object> set = new HashSet<>(list);
        return set.size() != list.size();
    }

    /**
     * list集合去重
     *
     * @param list
     * @return
     */
    public static List<Object> removeRepeat(List<Object> list) {
        Set<Object> set = new HashSet<>(list);
        return new ArrayList<>(set);
    }

    /**
     * list集合去重，顺序不变
     *
     * @param list
     */
    public static void removeRepeatWithOrder(List list) {
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            Object element = iter.next();
            if (set.add(element)) {
                newList.add(element);
            }
        }
        list.clear();
        list.addAll(newList);
    }

    /**
     * list集合并集
     *
     * @param list1
     * @param list2
     * @return
     */
    public static List<Object> listOr(List<Object> list1, List<Object> list2) {
        list1.removeAll(list2);
        list1.addAll(list2);
        return list1;
    }

    /**
     * list集合交集
     *
     * @param list1
     * @param list2
     * @return
     */
    public static List<String> listAnd(List<String> list1, List<String> list2) {
        List<String> commonList = new ArrayList<>();
        Set<Object> hashSet = new HashSet<>();
        Collections.addAll(hashSet, list1);

        for (String item : list2) {
            if (!hashSet.add(item)) {
                commonList.add(item);
            }
        }
        return commonList;
    }

}