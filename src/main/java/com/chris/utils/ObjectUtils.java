package com.chris.utils;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;

/**
 * YuedaoXingApi
 * com.ydx.api.libs.utils
 * Created by Chris Chen
 * 2017/12/4
 * Explain:实体类工具
 */
public class ObjectUtils {
    /**
     * 复制名称相同类型相同的字段数据
     * <p>
     * 功能：把源数据中和目标数据类型中名称相同，类型相匹配的字段名复制给一个新的目标数据类型的实例
     * 类型匹配：
     * 1.基本数据类型和包装类自动复制
     * 2.TimeStamp和Long、long长整型自动转换，忽略时区
     *
     * @param sourceObj 源数据对象
     * @param clazz     目标数据class
     * @param <T1>      源数据类型
     * @param <T2>      目标数据类型
     * @return 复制后的目标数据对象
     */
    public static <T1, T2> T2 copyData(T1 sourceObj, Class<T2> clazz) {
        if (sourceObj == null) {
            return null;
        }
        //2. 创建一个目标数据对象
        T2 targetObj = getInstance(clazz);
        if (targetObj == null) {
            return null;
        }
        //2. 复制两个对象相同的字段
        copyData(sourceObj, targetObj);
        return targetObj;
    }

    /**
     * 复制两个对象中相同字段的值
     * <p>
     * 功能：把源数据对象中和目标数据对象名称相同、类型相匹配的字段的值赋值给目标数据对象
     *
     * @param sourceObj 源数据对象
     * @param targetObj 目标数据对象
     * @param <T1>      源数据类型
     * @param <T2>      目标数据类型
     */
    public static <T1, T2> void copyData(T1 sourceObj, T2 targetObj) {
        //1. 获取两个对象的类
        Class<?> clazz1 = sourceObj.getClass();
        Class<?> clazz2 = targetObj.getClass();
        //3. 获取两个类字段集合
        Field[] fields1 = clazz1.getDeclaredFields();
        Field[] fields2 = clazz2.getDeclaredFields();
        //4. 遍历fields1
        for (Field f1 : fields1) {
            //4-1. 遍历fields2
            for (Field f2 : fields2) {
                //4-2. 复制字段
                copyFieldValue(sourceObj, targetObj, f1, f2);
            }
        }
    }

    /**
     * 复制列表
     *
     * @param sourceObjList
     * @param targetClass
     * @param <T1>
     * @param <T2>
     * @return
     */
    public static <T1, T2> List<T2> copyDataList(List<T1> sourceObjList, Class<T2> targetClass) {
        List<T2> targetObjList = new ArrayList<>();
        if (sourceObjList == null || targetClass == null) {
            return null;
        }
        for (T1 t : sourceObjList) {
            targetObjList.add(copyData(t, targetClass));
        }
        return targetObjList;
    }

    /**
     * 复制列表
     *
     * @param sourceObjList
     * @param targetObjList
     * @param <T1>
     * @param <T2>
     */
    public static <T1, T2> void copyDataList(List<T1> sourceObjList, List<T2> targetObjList, Class<T2> targetClass) {
        if (sourceObjList == null || targetObjList == null) {
            return;
        }
        for (T1 t : sourceObjList) {
            targetObjList.add(copyData(t, targetClass));
        }
    }

    /**
     * 根据字段列表复制字段的值
     * <p>
     * 功能：把源数据对象中与目标数据类型中名称相同且在字段列表中，类型还匹配的字段的值赋值给新创建的目标数据对象
     * 提示：及时类型匹配、名称相同，但是名称不在字段名列表中的字段是不会复制的
     *
     * @param sourceObj 源数据对象
     * @param clazz     目标数据class
     * @param fields    需要复制的字段名列表
     * @param <T1>      源数据类型
     * @param <T2>      目标数据类型
     * @return 目标数据对象
     */
    public static <T1, T2> T2 copyData(T1 sourceObj, Class<T2> clazz, String[] fields) {
        //1. 获取源数据的类
        Class<?> clazz1 = sourceObj.getClass();
        //2. 创建一个目标数据对象
        T2 targetObj = getInstance(clazz);
        if (targetObj == null) {
            return null;
        }
        //3. 获取两个类字段集合
        Field[] fields1 = clazz1.getDeclaredFields();
        Field[] fields2 = clazz.getDeclaredFields();
        //4. 复制字段
        copyFieldValue(sourceObj, targetObj, fields, fields1, fields2);
        return targetObj;
    }

    /**
     * 根据字段表复制两个对象中相同字段的值
     * <p>
     * 功能：把源数据对象中与目标数据对象中名称相同且在字段列表中，类型还匹配的字段的值赋值给的目标数据对象
     *
     * @param sourceObj 源数据对象
     * @param targetObj 目标数据对象
     * @param fields    需要复制的字段名称列表
     * @param <T1>      源数据类型
     * @param <T2>      目标数据类型
     */
    public static <T1, T2> void copyData(T1 sourceObj, T2 targetObj, String[] fields) {
        //1. 获取源两个对象的类
        Class<?> clazz1 = sourceObj.getClass();
        Class<?> clazz2 = targetObj.getClass();
        //3. 获取两个类字段集合
        Field[] fields1 = clazz1.getDeclaredFields();
        Field[] fields2 = clazz2.getDeclaredFields();
        //4. 复制字段
        copyFieldValue(sourceObj, targetObj, fields, fields1, fields2);
    }

    /**
     * 根据字段列表排除复制对象中相同字段的值
     * 体质：凡是在字段列表中出现的不进行复制
     *
     * @param sourceObj
     * @param clazz
     * @param fields
     * @param <T1>
     * @param <T2>
     * @return
     */
    public static <T1, T2> T2 copyDataExclude(T1 sourceObj, Class<T2> clazz, String[] fields) {
        //1. 获取源数据的类
        Class<?> clazz1 = sourceObj.getClass();
        //2. 创建一个目标数据对象
        T2 targetObj = getInstance(clazz);
        if (targetObj == null) {
            return null;
        }
        //3. 获取两个类字段集合
        Field[] fields1 = clazz1.getDeclaredFields();
        Field[] fields2 = clazz.getDeclaredFields();
        //4. 复制字段
        copyFieldValueExclude(sourceObj, targetObj, fields, fields1, fields2);
        return targetObj;
    }

    /**
     * 根据字段列表排除复制两个对象中相同字段的值
     * 提示：凡是在字段列表中出现的不进行复制
     *
     * @param sourceObj
     * @param targetObj
     * @param fields
     * @param <T1>
     * @param <T2>
     */
    public static <T1, T2> void copyDataExclude(T1 sourceObj, T2 targetObj, String[] fields) {
        //1. 获取源两个对象的类
        Class<?> clazz1 = sourceObj.getClass();
        Class<?> clazz2 = targetObj.getClass();
        //3. 获取两个类字段集合
        Field[] fields1 = clazz1.getDeclaredFields();
        Field[] fields2 = clazz2.getDeclaredFields();
        //4. 复制字段
        copyFieldValueExclude(sourceObj, targetObj, fields, fields1, fields2);
    }

    /**
     * 排除字段复制
     * 内部方法：
     * 逻辑：比较sourceObj和targetObj两个对象，获取两个对象的字段列表，只要是在fields字段名数组中出现过的字段就不会处理
     * 否则，机会把名称相同的字段进行复制
     *
     * @param sourceObj
     * @param targetObj
     * @param fields
     * @param fields1
     * @param fields2
     * @param <T1>
     * @param <T2>
     */
    private static <T1, T2> void copyFieldValueExclude(T1 sourceObj, T2 targetObj, String[] fields, Field[] fields1, Field[] fields2) {
        for (String fieldName : fields) {
            //4-1. 遍历fields1
            for (Field f1 : fields1) {
                //4-3. 是否匹配这个字段
                if (fieldName.equals(f1.getName())) {
                    //只要包含该字段就跳过
                    continue;
                }
                //4-4. 遍历fields2
                for (Field f2 : fields2) {
                    //4-5. 复制字段
                    copyFieldValue(sourceObj, targetObj, f1, f2);
                }

            }
        }
    }

    /**
     * 根据字段列表复制两个对象中相同字段的值
     * 内部方法：
     * 逻辑：如果两个字段名称相同就会进行复制
     *
     * @param sourceObj
     * @param targetObj
     * @param fields
     * @param fields1
     * @param fields2
     * @param <T1>
     * @param <T2>
     */
    private static <T1, T2> void copyFieldValue(T1 sourceObj, T2 targetObj, String[] fields, Field[] fields1, Field[] fields2) {
        for (String fieldName : fields) {
            //4-1. 遍历fields1
            for (Field f1 : fields1) {
                //4-3. 是否匹配这个字段
                if (fieldName.equals(f1.getName())) {
                    //4-4. 遍历fields2
                    for (Field f2 : fields2) {
                        //4-3. 是否匹配这个字段
                        if (fieldName.equals(f2.getName())) {
                            //4-2. 复制字段
                            copyFieldValue(sourceObj, targetObj, f1, f2);
                        }
                    }
                }
            }
        }
    }

    /**
     * 复制字段的值
     * 逻辑：比较两个对象的字段，如果类型相匹配，而且名称相同就会复制字段值
     * 类型匹配：
     * 1.基本数据类型和包装类自动复制
     * 2.TimeStamp和Long、long长整型自动转换，忽略时区
     *
     * @param sourceObj
     * @param targetObj
     * @param field1
     * @param field2
     * @param <T1>
     * @param <T2>
     */
    public static <T1, T2> void copyFieldValue(T1 sourceObj, T2 targetObj, Field field1, Field field2) {
        try {
            //1. 判断两个字段是否名称相同
            if (field1.getName().equals(field2.getName())) {
                //2. 获取源数据字段的值
                field1.setAccessible(true);
                field2.setAccessible(true);
                Object value = field1.get(sourceObj);
                if (equalFieldsType(field1, field2)) {
                    //2-1. 两个字段类型相同或者等同，直接赋值
                    field2.set(targetObj, value);
                } else if (timeStampEquals(field1, field2)) {
                    //特殊处理：TimeStamp和Long、longd的自动识别与转换
                    //2-2. 如果源是时间戳，目标是长整型，则取出time数值，赋值
                    field2.set(targetObj, value == null ? null : ((Timestamp) value).getTime());
                } else if (timeStampEquals(field2, field1)) {
                    //2-3. 如果源是长整型，目标是时间戳，则建立TimeStamp对象，赋值
                    field2.set(targetObj, value == null ? null : new Timestamp((Long) value));
                }
                //3. 访问权限还原
                field2.setAccessible(false);
                field1.setAccessible(false);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断TimeStamp和Long\long的匹配关系
     * 内部方法
     *
     * @param field1
     * @param field2
     * @return
     */
    private static boolean timeStampEquals(Field field1, Field field2) {
        if (field1 == null || field2 == null) {
            return false;
        }
        String timeStampName = Timestamp.class.getName();
        String fieldName1 = field1.getType().getName();
        String fieldName2 = field2.getType().getName();
        if (timeStampName.equals(fieldName1) &&
                (long.class.getName().equals(fieldName2) || Long.class.getName().equals(fieldName2))) {
            //如果前者为timestamp，而后者为long或者Long，则返回true
            return true;
        }
        return false;
    }

    /**
     * 获取一个泛型的实例
     * 内部方法
     *
     * @param clazz
     * @param <T>
     * @return
     */
    private static <T> T getInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            //e.printStackTrace();
        } catch (IllegalAccessException e) {
            //e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断两个字段的类型是否相同
     * 内部方法
     *
     * @param field1 复制源
     * @param field2 复制目标
     * @return
     */
    private static boolean equalFieldsType(Field field1, Field field2) {
        String fTypeName1 = field1.getType().getSimpleName();
        String fTypeName2 = field2.getType().getSimpleName();
        //1. 处理基本数据类型和包装类
        Map<String, String> map = new HashMap<String, String>();
        map.put(int.class.getSimpleName(), Integer.class.getSimpleName());
        map.put(byte.class.getSimpleName(), Byte.class.getSimpleName());
        map.put(short.class.getSimpleName(), Short.class.getSimpleName());
        map.put(char.class.getSimpleName(), Character.class.getSimpleName());
        map.put(long.class.getSimpleName(), Long.class.getSimpleName());
        map.put(float.class.getSimpleName(), Float.class.getSimpleName());
        map.put(double.class.getSimpleName(), Double.class.getSimpleName());
        map.put(boolean.class.getSimpleName(), Boolean.class.getSimpleName());

        /**
         * 在涉及包装类的判断逻辑中，源数据不能是包装类
         * 因为包装类一旦为null，会引发异常
         */
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            if (key.equals(fTypeName1) && map.get(key).equals(fTypeName2)) {
                return true;
            }
            if (key.equals(fTypeName2) && map.get(key).equals(fTypeName1)) {
                return true;
            }
        }
        //2. 名称相同、类型相同
        if (fTypeName1.equals(fTypeName2)) {
            return true;
        }
        return false;
    }

    /**
     * 判断两个类型是否相当（todo 暂时不可用）
     * 主要作用于基本数据类型和其包装类
     *
     * @param clazz1
     * @param clazz2
     * @return
     */
    public static boolean equalsType(Class<?> clazz1, Class<?> clazz2) {
        Class<?>[][] clazzArr = {
                {int.class, Integer.class},
                {short.class, Short.class},
                {long.class, Long.class},
                {float.class, Float.class},
                {double.class, Double.class},
                {char.class, Character.class},
                {byte.class, Byte.class},
                {boolean.class, Boolean.class}
        };

        String clazz1Name = clazz1.getName();
        String clazz2Name = clazz2.getName();
        for (int i = 0; i < clazzArr.length; i++) {
            String name0 = clazzArr[i][0].getName();
            String name1 = clazzArr[i][1].getName();
            if ((clazz1Name.equals(name0) && clazz2Name.equals(name1)) || (clazz1Name.equals(name1) && clazz2Name.equals(name0))) {
                return true;
            }
        }
        return false;
    }
}
