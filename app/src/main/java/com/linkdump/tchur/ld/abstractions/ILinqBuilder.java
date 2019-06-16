package com.linkdump.tchur.ld.abstractions;

import java.util.List;
import java.util.function.Predicate;

public interface ILinqBuilder<T> {

    ILinqBuilder Where(Predicate<T> function);
    List<T> Take(int count);
    ILinqBuilder WhereFirstOrDefault(Predicate<T> function);
}
