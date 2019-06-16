package com.linkdump.tchur.ld.abstractions;

public interface IIntentAdapter<T> {
      String Serialize();
      T Deserialize();
}
