package com.linkdump.tchur.ld.abstractions.eventbased;

public interface IOnCompletedListener<T> {
      void OnCompleted(T t);
}
