package com.spookybox.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Functional {
    public static <T, R> List<R> map(List<T> input, Function<T, R> transform){
        List<R> result = new ArrayList<>(input.size());
        for(T item : input){
            result.add(transform.apply(item));
        }
        return result;
    }
}
