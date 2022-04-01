package com.base.utils.collections;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @description: 集合工具类
 * @author: yanwen
 * @create: 2022-04-01 09:58
 **/
public class CollectionDiffer {
    public static <S, T, K> DiffResult<S, T> diff(
            Collection<S> sourceList, Collection<T> targetList,
            Function<S, K> sourceKey, Function<T, K> targetKey
    ) {
        //将两个集合都转为Map结构，以其主键字段为Key
        Map<K, S> sourceMap = Collections.toMap(sourceList, sourceKey);
        Map<K, T> targetMap = Collections.toMap(targetList, targetKey);

        //求两个集合主键互相匹配的交集
        List<K> matchedKeys = new ArrayList<>(sourceMap.keySet());
        matchedKeys.retainAll(targetMap.keySet());

        List<S> matchedSourceList = new ArrayList<>();
        List<T> matchedTargetList = new ArrayList<>();
        List<MatchedItem<S, T>> matchedItemList = new ArrayList<>();
        for (K key : matchedKeys) {
            S source = sourceMap.get(key);
            T target = targetMap.get(key);
            matchedSourceList.add(source);
            matchedTargetList.add(target);
            matchedItemList.add(new MatchedItem<>(source, target));
        }

        //求source集合中独有的子集
        List<S> unmatchedSourceList = new ArrayList<>(sourceList);
        unmatchedSourceList.removeAll(matchedSourceList);

        //求target集合中独有的子集
        List<T> unmatchedTargetList = new ArrayList<>(targetList);
        unmatchedTargetList.removeAll(matchedTargetList);

        return new DiffResult<>(unmatchedSourceList, unmatchedTargetList, matchedItemList);
    }

    /**
     * 匹配的结果
     *
     * @param <S>源数据类型
     * @param <T>目标数据类型
     */
    @Getter
    public static class DiffResult<S, T> {

        /**
         * 未匹配上的源集合中元素列表
         */
        private final List<S> unmatchedSourceList;

        /**
         * 未匹配上的目标集合中元素列表
         */
        private final List<T> unmatchedTargetList;

        /**
         * 通过外键互相匹配到的元素集合
         */
        private final List<MatchedItem<S, T>> matchedItemList;

        public DiffResult(List<S> unmatchedSourceList, List<T> unmatchedTargetList, List<MatchedItem<S, T>> matchedItemList) {
            this.unmatchedSourceList = unmatchedSourceList;
            this.unmatchedTargetList = unmatchedTargetList;
            this.matchedItemList = matchedItemList;
        }

        public void consumeEachUnmatchedSource(Consumer<S> consumer) {
            for (S source : unmatchedSourceList) {
                consumer.accept(source);
            }
        }

        public void consumeEachUnmatchedTarget(Consumer<T> consumer) {
            for (T target : unmatchedTargetList) {
                consumer.accept(target);
            }
        }

        public void consumeEachMatchedItem(BiConsumer<S, T> consumer) {
            for (MatchedItem<S, T> item : matchedItemList) {
                consumer.accept(item.getSource(), item.getTarget());
            }
        }
    }

    @Getter
    public static class MatchedItem<S, T> {

        /**
         * 源集合中的数据
         */
        private final S source;

        /**
         * 目标集合中的数据
         */
        private final T target;

        public MatchedItem(S source, T target) {
            this.source = source;
            this.target = target;
        }
    }
}
