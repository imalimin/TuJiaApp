package com.example.tujia.comic.data;

/**
 * Entity with id.
 *
 * @author wlei 2012-6-9
 */
public interface WithId<T> {

  T getId();

  void setId(T id);
}
