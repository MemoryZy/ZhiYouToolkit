package cn.zhiyou.entity.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Memory
 * @since 2023/12/27
 */
public abstract class TemplateEntity {

    /**
     * 将Bean转为Map，key为纯大写的下划线分隔
     *
     * @return Map
     */
    public Map<String, Object> toMap() {
        Map<String, Object> beanMap = BeanUtil.beanToMap(this);
        Map<String, Object> resultMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            String key = entry.getKey();
            if (Objects.nonNull(key)) {
                // 先转下划线，再转大写
                resultMap.put(StrUtil.toUnderlineCase(key).toUpperCase(), entry.getValue());
            }
        }

        return resultMap;
    }

    public Map<String, String> toMapStr() {
        Map<String, Object> map = toMap();
        Map<String, String> resultMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            // 过滤空值
            if (Objects.nonNull(value)) {
                resultMap.put(entry.getKey(), value.toString());
            }
        }

        return resultMap;
    }

    /**
     * 将Map对象转换成实体类对象。
     *
     * @param map         包含实体类属性的Map对象
     * @param entityClass 实体类Class对象
     * @param <T>         实体类类型
     * @return 转换后的实体类对象
     */
    public static <T> T toBean(Map<String, Object> map, Class<T> entityClass) {
        return (MapUtil.isEmpty(map)) ? null : BeanUtil.mapToBean(map, entityClass, true, CopyOptions.create().ignoreCase());
    }

    /**
     * 将Map对象转换成实体类对象。
     *
     * @param mapList     包含实体类属性的Map对象列表
     * @param entityClass 实体类Class对象
     * @param <T>         实体类类型
     * @return 转换后的实体类对象
     */
    public static <T> List<T> toBean(List<Map<String, Object>> mapList, Class<T> entityClass) {
        return (CollUtil.isEmpty(mapList)) ? new ArrayList<>() : mapList.stream().map(el -> toBean(el, entityClass)).collect(Collectors.toList());
    }

}
