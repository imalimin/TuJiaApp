package com.example.tujia.comic.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.tujia.comic.data.WithId;
import com.example.tujia.comic.data.WithIdUpdateDate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Wlei 2012-5-13
 */
public class CollectionUtil {

  public static boolean isNullOrEmpty(Collection<?> collection) {
    return (collection == null) || (collection.isEmpty());
  }

  public static boolean isNullOrEmpty(Map<?, ?> map) {
    return (map == null) || (map.isEmpty());
  }

  /**
   * Adds element to collection, <code>null</code> element will be ignored.
   */
  public static <T> boolean add(Collection<T> collection, T element) {
    if (element != null) {
      return collection.add(element);
    }
    return false;
  }

  /**
   * Adds specify collection, <code>null</code> or empty collection will be ignored.
   *
   * @param collection collection that to be added.
   * @param toAdd collection to add.
   */
  public static <T> void addAll(Collection<T> collection, Collection<T> toAdd) {
    if (isNullOrEmpty(toAdd)) {
      return;
    }
    collection.addAll(toAdd);
  }

  /**
   * Puts specify map, <code>null</code> or empty map will be ignored.
   *
   * @param map map that to be added.
   * @param toAdd map to add.
   */
  public static <K, V> void putAll(Map<K, V> map, Map<K, V> toPut) {
    if (isNullOrEmpty(toPut)) {
      return;
    }
    map.putAll(toPut);
  }

  public static <ID, T extends WithId<ID>> Map<ID, T> getIdMap(Collection<T> withIds) {
    final Map<ID, T> result = Maps.newHashMap();
    if (!isNullOrEmpty(withIds)) {
      for (final T withId : withIds) {
        result.put(withId.getId(), withId);
      }
    }
    return result;
  }
  
  public static <ID, T extends WithIdUpdateDate<ID, ID>> Map<ID, T> getIdUpdateDateMap(Collection<T> withIdUpdateDates) {
    final LinkedHashMap<ID, T> result = Maps.newLinkedHashMap();
    if (!isNullOrEmpty(withIdUpdateDates)) {
      for (final T withIdUpdateDate : withIdUpdateDates) {
        result.put(withIdUpdateDate.getId(), withIdUpdateDate);
        result.put(withIdUpdateDate.getUpdateDate(), withIdUpdateDate);
      }
    }
    return result;
  }

  public static <T> List<T> subList(Collection<T> items, int size) {
    return subList(items, 0, size);
  }

  public static <T> List<T> subList(Collection<T> items, int startIndex, int size) {
    final List<T> result = Lists.newArrayList();
    int index = 0;
    for (final T item : items) {
      if (index++ < startIndex) {
        continue;
      }
      result.add(item);
      if (result.size() >= size) {
        break;
      }
    }
    return result;
  }
}
