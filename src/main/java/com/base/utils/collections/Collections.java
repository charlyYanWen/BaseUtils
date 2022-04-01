package com.base.utils.collections;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description: 在Spring的集合工具类上做拓展
 * @author: yanwen
 * @create: 2022-04-01 10:17
 **/
public class Collections extends CollectionUtils {

    /**
     * 判断指定集合是否非空
     */
    public static boolean notEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 将集合所有元素转为指定类型
     *
     * @param sources     原始集合
     * @param transformer 转换器
     */
    public static <S, R> List<R> mapToList(Collection<S> sources, Function<S, R> transformer) {
        return sources.stream()
                .map(transformer)
                .collect(Collectors.toList());
    }

    /**
     * 将集合所有元素转为指定类型
     *
     * @param sources     原始集合
     * @param transformer 转换器
     */
    public static <S, R> Set<R> mapAsSet(Collection<S> sources, Function<S, R> transformer) {
        return sources.stream()
                .map(transformer)
                .collect(Collectors.toSet());
    }

    /**
     * 将集合转为Map结构
     *
     * @param sources   原始集合
     * @param keyGetter 主键Getter
     */
    public static <S, K> Map<K, S> toMap(Collection<S> sources, Function<S, K> keyGetter) {
        return toMap(sources, keyGetter, Function.identity());
    }

    /**
     * 将集合转为Map结构
     *
     * @param sources     原始集合
     * @param keyGetter   主键Getter
     * @param valueGetter 值Getter
     */
    @SuppressWarnings("WeakerAccess")
    public static <S, K, V> Map<K, V> toMap(Collection<S> sources, Function<S, K> keyGetter, Function<S, V> valueGetter) {
        return sources.stream().collect(Collectors.toMap(
                keyGetter, valueGetter,
                (last, current) -> {
                    // key冲突时判断两个value是否相等
                    // 如果相等，取任意一个都可以
                    // 如果不相等，抛出异常
                    if (Objects.equals(last, current)) {
                        return current;
                    } else {
                        throw new IllegalStateException(String.format("Duplicate key %s", current));
                    }
                }
        ));
    }

    /**
     * 简单groupby
     */
    public static <S, K> Map<K, List<S>> groupingBy(Collection<S> sources, Function<S, K> classifier) {
        return sources.stream().collect(Collectors.groupingBy(
                classifier, Collectors.toList()
        ));
    }

    /**
     * 分页/分批处理，将原始集合按照指定size执行分页，切为多个小集合
     */
    public static <T> List<List<T>> partition(List<T> sources, int pageSize) {
        return Lists.partition(sources, pageSize);
    }

    /**
     * 按照外键FK批量查询并绑定关联实体
     *
     * @param collection 初始集合
     * @param fkGetter   外键属性
     * @param loader     通过外键批量查询对应的关联实体
     * @param consumer   组装数据
     * @param <E>        集合元素数据类型
     * @param <F>        关联实体类型
     * @param <FK>       外键类型
     */
    public static <R, E, F, FK> List<R> bindRelationByFK(
            Collection<E> collection, Function<E, FK> fkGetter,
            Function<Set<FK>, Map<FK, F>> loader, BiFunction<E, F, R> consumer
    ) {
        if (Collections.isEmpty(collection)) {
            return java.util.Collections.emptyList();
        }
        //提取外键集合：去重、过滤null值
        Set<FK> fkList = collection.stream()
                .map(fkGetter)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        //执行加载器，批量加载外键关联实体
        Map<FK, F> fMap = isEmpty(fkList) ? new HashMap<>() : loader.apply(fkList);

        return collection.stream()
                .filter(Objects::nonNull)
                .map(it -> {
                    //再次取出外键
                    FK fk = fkGetter.apply(it);
                    //通过外键，从批量查询结果中找到对应的关联实体
                    F f = fk == null ? null : fMap.get(fk);
                    //调用回调，组装数据
                    return consumer.apply(it, f);
                })
                .collect(Collectors.toList());
    }
}
