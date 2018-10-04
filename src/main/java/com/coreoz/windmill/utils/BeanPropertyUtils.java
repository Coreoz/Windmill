package com.coreoz.windmill.utils;

import com.coreoz.windmill.imports.Cell;
import com.coreoz.windmill.imports.Row;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BeanPropertyUtils {

    public static <T> Function<Row, T> rowTransformer(Class<T> targetClass) {
        return cells -> {
            try {
                return Arrays.stream(PropertyUtils.getPropertyDescriptors(targetClass))
                        .filter(it -> it.getWriteMethod() != null)
                        .filter(it -> cells.columnExists(it.getName()))
                        .map(it -> ImmutablePair.of(cells.cell(it.getName()), it.getWriteMethod()))
                        .reduce(targetClass.newInstance(), (t, p) -> {
                            Cell cell = p.getLeft();
                            Method method = p.getRight();

                            Object arg;
                            Class<?> argumentType = method.getParameterTypes()[0];
                            if (argumentType.isAssignableFrom(Integer.class)) {
                                arg = cell.asInteger().value();
                            } else if (argumentType.isAssignableFrom(Double.class)) {
                                arg = cell.asDouble().value();
                            } else if (argumentType.isAssignableFrom(Float.class)) {
                                arg = cell.asFloat().value();
                            } else if (argumentType.isAssignableFrom(Long.class)) {
                                arg = cell.asLong().value();
                            } else {
                                arg = cell.asString();
                            }

                            try {
                                method.invoke(t, arg);
                            } catch (ReflectiveOperationException e) {
                                throw new IllegalStateException(e);
                            }

                            return t;
                        }, (a, b) -> a);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
        };
    }

    public static <T> LinkedMap<String, Function<T, ?>> beanPropertiesAccessor(Class<T> targetClass) {
        return Arrays.stream(PropertyUtils.getPropertyDescriptors(targetClass))
                .filter(it -> it.getPropertyType() != Class.class)
                .filter(it -> it.getReadMethod() != null)
                .collect(Collectors.toMap(FeatureDescriptor::getName, it -> (Function<T, Object>) t -> {
                    try {
                        return it.getReadMethod().invoke(t);
                    } catch (ReflectiveOperationException e) {
                        throw new IllegalStateException(e);
                    }
                }, (a, b) -> b, LinkedMap::new));
    }
}
