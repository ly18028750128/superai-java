package com.lianziyou.bot.utils;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lianziyou.bot.annotate.Query;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyBatisPlusUtil {

    private final static MyBatisPlusUtil myBatisPlusUtil = new MyBatisPlusUtil();

    private MyBatisPlusUtil() {

    }

    public static MyBatisPlusUtil single() {
        return myBatisPlusUtil;
    }

    public <T> QueryWrapper<T> createQueryWrapperByParams(Map<String, Object> params) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        params.forEach((column, value) -> {
            List<?> listValue = null;
            if (value == null) {
                queryWrapper.isNotNull(column);
            } else if (value instanceof Object[]) {
                listValue = Arrays.asList((Object[]) value);
            } else if (value instanceof List<?>) {
                listValue = (List<?>) value;
            } else {
                queryWrapper.eq(column, value);
            }
            if (listValue == null) {
                return;
            }
            if (listValue.size() == 1) {
                queryWrapper.eq(column, value);
                return;
            }
            String[] columns = column.split("::");
            if (columns.length > 1) {
                if ("BETWEEN".equalsIgnoreCase(columns[1])) {
                    queryWrapper.between(column, listValue.get(0), listValue.get(1));
                } else {
                    queryWrapper.in(column, listValue);
                }
            } else {
                queryWrapper.in(column, listValue);
            }
        });
        return queryWrapper;
    }


    public <R, Q> QueryWrapper<R> getPredicate(Q query) {
        QueryWrapper<R> queryWrapper = new QueryWrapper<>();
        if (query == null) {
            return queryWrapper;
        }
        try {
            List<Field> fields = getAllFields(query.getClass(), new ArrayList<>());
            for (Field field : fields) {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                Query q = field.getAnnotation(Query.class);
                if (q == null) {
                    if (ObjectUtil.isNotEmpty(field.get(query))) {
                        queryWrapper.eq(humpToUnderline(field.getName()), field.get(query));
                    }
                    continue;
                }

                if (q.ignore()) {  // 如果为忽略状态，那么跳过
                    continue;
                }

                String propName = q.propName();
                String blurry = q.blurry();
                String attributeName = isBlank(propName) ? field.getName() : propName;
                attributeName = humpToUnderline(attributeName);
                Object val = field.get(query);
                if (ObjectUtil.isEmpty(val) || "".equals(val)) {
                    continue;
                }
                // 模糊多字段
                if (ObjectUtil.isNotEmpty(blurry)) {
                    String[] blurrys = blurry.split(",");
                    //queryWrapper.or();
                    queryWrapper.and(wrapper -> {
                        for (String s : blurrys) {
                            String column = humpToUnderline(s);
                            wrapper.or();
                            wrapper.like(column, val.toString());
                        }
                    });
                    continue;
                }
                String finalAttributeName = attributeName;
                switch (q.type()) {
                    case EQUAL:
                        //queryWrapper.and(wrapper -> wrapper.eq(finalAttributeName, val));
                        queryWrapper.eq(attributeName, val);
                        break;
                    case GREATER_THAN:
                        queryWrapper.ge(finalAttributeName, val);
                        break;
                    case GREATER_THAN_NQ:
                        queryWrapper.gt(finalAttributeName, val);
                        break;
                    case LESS_THAN:
                        queryWrapper.le(finalAttributeName, val);
                        break;
                    case LESS_THAN_NQ:
                        queryWrapper.lt(finalAttributeName, val);
                        break;
                    case INNER_LIKE:
                        queryWrapper.like(finalAttributeName, val);
                        break;
                    case LEFT_LIKE:
                        queryWrapper.likeLeft(finalAttributeName, val);
                        break;
                    case RIGHT_LIKE:
                        queryWrapper.likeRight(finalAttributeName, val);
                        break;
                    case IN:
                        if (ObjectUtil.isNotEmpty(val)) {
                            Collection<?> collectionIn = (Collection<?>) val;
                            queryWrapper.in(finalAttributeName, collectionIn);
                        }
                        break;
                    case NOT_EQUAL:
                        queryWrapper.ne(finalAttributeName, val);
                        break;
                    case NOT_NULL:
                        queryWrapper.isNotNull(finalAttributeName);
                        break;
                    case BETWEEN:
                        if ((val instanceof List) && ObjectUtil.isNotEmpty(val)) {
                            List<?> between = (List<?>) val;
                            queryWrapper.between(finalAttributeName, between.get(0), between.get(1));
                        }
                        break;
                    case UNIX_TIMESTAMP:
                        if ((val instanceof List) && ObjectUtil.isNotEmpty(val)) {
                            List<?> UNIX_TIMESTAMP = (List<?>) val;
                            if (!UNIX_TIMESTAMP.isEmpty()) {
                                SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                if (UNIX_TIMESTAMP.size() == 1 || (UNIX_TIMESTAMP.size() == 2 && ObjectUtil.isEmpty(UNIX_TIMESTAMP.get(1)))) {
                                    Date time1 = fm.parse(UNIX_TIMESTAMP.get(0).toString());
                                    queryWrapper.ge(finalAttributeName, time1);
                                } else if (UNIX_TIMESTAMP.size() == 2 && ObjectUtil.isEmpty(UNIX_TIMESTAMP.get(0))) {
                                    Date time2 = fm.parse(UNIX_TIMESTAMP.get(1).toString());
                                    queryWrapper.le(finalAttributeName, time2);
                                } else {
                                    Date time1 = fm.parse(UNIX_TIMESTAMP.get(0).toString());
                                    Date time2 = fm.parse(UNIX_TIMESTAMP.get(1).toString());
                                    queryWrapper.between(finalAttributeName, time1, time2);
                                }
                            }
                        }
                        break;
                    case LONG_TIMESTAMP:
                        if ((val instanceof List) && ObjectUtil.isNotEmpty(val)) {
                            List<?> LONG_TIMESTAMP = (List<?>) val;
                            if (!LONG_TIMESTAMP.isEmpty()) {
                                if (LONG_TIMESTAMP.size() == 1 || (LONG_TIMESTAMP.size() == 2 && ObjectUtil.isEmpty(LONG_TIMESTAMP.get(1)))) {
                                    Date time1 = new Date(new Long(LONG_TIMESTAMP.get(0).toString()));
                                    queryWrapper.ge(finalAttributeName, time1);
                                } else if (LONG_TIMESTAMP.size() == 2 && ObjectUtil.isEmpty(LONG_TIMESTAMP.get(0))) {
                                    Date time2 = new Date(new Long(LONG_TIMESTAMP.get(1).toString()));
                                    queryWrapper.le(finalAttributeName, time2);
                                } else {
                                    Date time1 = new Date(new Long(LONG_TIMESTAMP.get(0).toString()));
                                    Date time2 = new Date(new Long(LONG_TIMESTAMP.get(1).toString()));
                                    queryWrapper.between(finalAttributeName, time1, time2);
                                }
                            }
                        }
                        break;

                    default:
                        break;
                }

                field.setAccessible(accessible);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return queryWrapper;
    }


    private boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private List<Field> getAllFields(Class<?> clazz, List<Field> fields) {
        if (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            getAllFields(clazz.getSuperclass(), fields);
        }
        return fields;
    }

    /***
     * 驼峰命名转为下划线命名
     *
     * @param para
     *        驼峰命名的字符串
     */

    public String humpToUnderline(String para) {
        StringBuilder sb = new StringBuilder(para);
        int temp = 0;//定位
        if (!para.contains("_")) {
            for (int i = 0; i < para.length(); i++) {
                if (Character.isUpperCase(para.charAt(i))) {
                    sb.insert(i + temp, "_");
                    temp += 1;
                }
            }
        }
        return sb.toString();
    }

}
