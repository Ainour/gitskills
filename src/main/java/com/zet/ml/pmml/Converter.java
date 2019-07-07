/*
 * Copyright 2018, Zetyun StreamTau All rights reserved.
 */

package com.zet.ml.pmml;

public interface Converter<S, T> {
    Class<S> getSourceClass();

    Class<T> getTargetClass();

    T convert(S source) throws ConversionException;

    default T convert(S source, Object context) throws ConversionException {
        return convert(source);
    }
}
