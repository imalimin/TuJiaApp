package com.example.tujia.comic.data;


public interface WithIdUpdateDate<T, M> {
  T getId();

  void setId(T id);
  
  M getUpdateDate();

  void setUpdateDate(M id);
}
